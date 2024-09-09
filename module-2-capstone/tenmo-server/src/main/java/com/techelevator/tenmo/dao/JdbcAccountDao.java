package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getAccountBalanceById(int id) {
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                return results.getBigDecimal("balance");
            } else {
                throw new DaoException("User ID " + id + " not found.");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (Exception e) {
            throw new DaoException("An unexpected error occurred while retrieving balance.", e);
        }
    }

    @Override
    public Account getAccountByUserId(int id) {
        Account userAccount = new Account();
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            userAccount = mapRowToAccount(results);
        }
        return userAccount;
    }


    @Override
    public Account getAccountByAccountId(int id){
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            return mapRowToAccount(results);
        }
        else {
            return null;
        }
    }


    @Override
    public Account addAmount(int id, BigDecimal amount) {
        Account account = getAccountByUserId(id);
        if (account == null) {
            throw new DaoException("Account not found for user ID: " + id);
        }
        BigDecimal newBalance = account.getBalance().add(amount);

        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        int rowsAffected = jdbcTemplate.update(sql, newBalance, account.getAccountId());

        if (rowsAffected == 0) {
            throw new DaoException("Failed to update the account balance for account ID: " + account.getAccountId());
        }
        account.setBalance(newBalance);
        return account;
    }


    @Override
    public Account subtractAmount(int id, BigDecimal amount) {
        Account account = getAccountByUserId(id);
        if (account == null) {
            throw new DaoException("Account not found for user ID: " + id);
        }
        BigDecimal newBalance = account.getBalance().subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new DaoException("Insufficient funds in account ID: " + account.getAccountId());
        }
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        int rowsAffected = jdbcTemplate.update(sql, newBalance, account.getAccountId());

        if (rowsAffected == 0) {
            throw new DaoException("Failed to update the account balance for account ID: " + account.getAccountId());
        }
        account.setBalance(newBalance);
        return account;
    }


    private Account mapRowToAccount(SqlRowSet results){
        Account account = new Account();
        account.setAccountId(results.getInt("account_id"));
        account.setUserId(results.getInt("user_id"));
        account.setBalance(results.getBigDecimal("balance"));

        return account;
    }
}
