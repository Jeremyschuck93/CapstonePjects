package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {
    AccountDao accountDao;

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTransferDao(AccountDao accountDao, JdbcTemplate jdbcTemplate) {
        this.accountDao = accountDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String sendBucks(int userFrom, int userTo, BigDecimal amount) {
        try {
            if (userFrom == userTo) {
                return "Prohibited Transfer: Unable to send money to yourself";
            }

            BigDecimal senderBalance = accountDao.getAccountBalanceById(userFrom);
            if (amount.compareTo(senderBalance) >= 0 || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return "Funds must be a positive non-zero integer and cannot transfer insufficient funds";
            }

            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES (2, 2, (SELECT account_id FROM account WHERE user_id = ?), (SELECT account_id FROM account WHERE user_id = ?), ?);";
            jdbcTemplate.update(sql, userFrom, userTo, amount);

            accountDao.addAmount(userTo, amount);
            accountDao.subtractAmount(userFrom, amount);

            return "Transfer complete";
        } catch (Exception e) {
            throw new DaoException("Error occurred while sending bucks", e);
        }
    }


    @Override
    public String requestBucks(int userFrom, int userTo, BigDecimal amount) {
        try {
            if (userFrom == userTo) {
                return "Invalid Transaction: Sending money to your own account is not allowed.\nPlease choose another recipient.";
            }
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return "Invalid Transaction: Amount must be a positive non-zero number.";
            }
            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES (1, 1, (SELECT account_id FROM account WHERE user_id = ?), (SELECT account_id FROM account WHERE user_id = ?), ?);";

            int rowsAffected = jdbcTemplate.update(sql, userFrom, userTo, amount);

            if (rowsAffected > 0) {
                return "Transfer Request Submitted!";
            } else {
                return "Transfer Request Failed: No rows were affected.";
            }
        } catch (DataAccessException e) {
            throw new DaoException("Error occurred during transfer request", e);
        } catch (Exception e) {
            throw new DaoException("Unexpected error occurred during transfer request", e);
        }
    }

    @Override
    public List<Transfer> getPastTransfers(int userId) {
        try {
            int accountId = accountDao.getAccountByUserId(userId).getAccountId();
            List<Transfer> transfers = new ArrayList<>();
            String sql = "SELECT t.*, uf.username AS user_from, ut.username AS user_to " +
                    "FROM transfer t " +
                    "JOIN account af ON t.account_from = af.account_id " +
                    "JOIN account at ON t.account_to = at.account_id " +
                    "JOIN tenmo_user uf ON af.user_id = uf.user_id " +
                    "JOIN tenmo_user ut ON at.user_id = ut.user_id " +
                    "WHERE (transfer_status_id = 2 AND transfer_type_id = 1 AND account_to = ?) " +
                    "OR (transfer_status_id = 2 AND transfer_type_id = 2 AND account_from = ?); ";
            try {
                SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
                while (results.next()) {
                    Transfer transfer = mapRowToTransfer(results);
                    transfers.add(transfer);
                }
            } catch (CannotGetJdbcConnectionException e){
                throw new DaoException("Unable to connect to server or database", e);
            }
            return transfers;
        }catch (Exception e) {
            throw new DaoException("Error occurred while getting past transfers", e);
        }
    }

    @Override
    public List<Transfer> getPendingTransfers(int userId) {
        try {
            int accountId = accountDao.getAccountByUserId(userId).getAccountId();
            List<Transfer> transfers = new ArrayList<>();
            String sql = "SELECT t.*, uf.username AS user_from, ut.username AS user_to \n" +
                    "FROM transfer t \n" +
                    "JOIN account af ON t.account_from = af.account_id \n" +
                    "JOIN account at ON t.account_to = at.account_id \n" +
                    "JOIN tenmo_user uf ON af.user_id = uf.user_id \n" +
                    "JOIN tenmo_user ut ON at.user_id = ut.user_id \n" +
                    "WHERE (transfer_status_id = 1  \n" +
                    "AND account_from = ?); ";
            try {
                SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
                while (results.next()) {
                    Transfer transfer = mapRowToTransfer(results);
                    transfers.add(transfer);
                }
            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect to server or database", e);
            }
            return transfers;
        } catch (Exception e) {
            throw new DaoException("Error occurred while getting pending transfers", e);
        }
    }


    @Override
    public String approveTransfer(Transfer transfer) {
        try {
            String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?;";
            jdbcTemplate.update(sql, 2, transfer.getTransferId());

            Account accountTo = accountDao.getAccountByAccountId(transfer.getAccountTo());
            Account accountFrom = accountDao.getAccountByAccountId(transfer.getAccountFrom());

            if (accountTo == null || accountFrom == null) {
                throw new DaoException("Account not found for ID: " + (accountTo == null ? transfer.getAccountTo() : transfer.getAccountFrom()));
            }
            accountDao.addAmount(accountTo.getUserId(), transfer.getAmount());
            accountDao.subtractAmount(accountFrom.getUserId(),transfer.getAmount());

            return "Transfer Approved";

        } catch (Exception e) {
            throw new DaoException("Error occurred while approving transaction", e);
        }
    }

    @Override
    public String rejectTransfer(Transfer transfer) {
        try {
            String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?;";
            jdbcTemplate.update(sql, 3, transfer.getTransferId());
            return "Transfer Rejected";
        } catch (Exception e) {
            throw new DaoException("Error occurred while rejecting transaction", e);
        }
    }

    private Transfer mapRowToTransfer(SqlRowSet results){
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setTransferTypeId(results.getInt("transfer_type_id"));
        transfer.setTransferStatusId(results.getInt("transfer_status_id"));
        transfer.setAccountFrom(results.getInt("account_from"));
        transfer.setAccountTo(results.getInt("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        transfer.setUserFrom(results.getString("user_from"));
        transfer.setUserTo(results.getString("user_to"));
        return transfer;
    }

}
