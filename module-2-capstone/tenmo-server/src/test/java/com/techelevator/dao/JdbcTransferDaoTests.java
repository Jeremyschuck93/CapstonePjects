package com.techelevator.dao;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class JdbcTransferDaoTests extends BaseDaoTests {

    private static final Transfer TRANSFER_1 = new Transfer(1, 2, 1, 201, 202, "user1", "user2", new BigDecimal("500.00"));
    private static final Transfer TRANSFER_2 = new Transfer(2, 1, 1, 202, 201, "user2", "user1", new BigDecimal("250.00"));
    private static final Transfer TRANSFER_3 = new Transfer(3, 2, 2, 201, 203, "user1", "user3", new BigDecimal("1000.00"));

   // @Autowired
    private JdbcTransferDao sut;

   // @Autowired
    private AccountDao accountDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        accountDao = new JdbcAccountDao(jdbcTemplate);
        sut = new JdbcTransferDao(accountDao, jdbcTemplate);

        // Clear existing data
        jdbcTemplate.update("DELETE FROM transfer;");
        jdbcTemplate.update("DELETE FROM account;");
        jdbcTemplate.update("DELETE FROM tenmo_user;");

        // Insert test users
        jdbcTemplate.update("INSERT INTO tenmo_user (user_id, username, password_hash, role) VALUES (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?);",
                1001, "user1", "password1", "USER_ROLE_1",
                1002, "user2", "password2", "USER_ROLE_2",
                1003, "user3", "password3", "USER_ROLE_3");

        // Insert test accounts
        jdbcTemplate.update("INSERT INTO account (account_id, user_id, balance) VALUES (?, ?, ?), (?, ?, ?), (?, ?, ?);",
                201, 1001, new BigDecimal("1000.00"),
                202, 1002, new BigDecimal("1000.00"),
                203, 1003, new BigDecimal("1000.00"));

        // Insert into Transfer table
        jdbcTemplate.update("INSERT INTO transfer (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?, ?);",
                1, 2, 2, 201, 202, new BigDecimal("50.00"));

        jdbcTemplate.update("INSERT INTO transfer (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?, ?);",
                2, 1, 1, 202, 203, new BigDecimal("75.00"));

        jdbcTemplate.update("INSERT INTO transfer (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?, ?);",
                3, 1, 1, 203, 201, new BigDecimal("100.00"));

    }

    @Test
    public void sendBucks_valid_transfer_returns_success_message() {
        String result = sut.sendBucks(1001, 1003, new BigDecimal("100.00"));

        Assert.assertEquals("Transfer complete", result);
        Assert.assertEquals(new BigDecimal("900.00"), accountDao.getAccountBalanceById(1001));
        Assert.assertEquals(new BigDecimal("1100.00"), accountDao.getAccountBalanceById(1003));
    }

    @Test
    public void sendBucks_insufficient_funds_returns_error_message() {
        String result = sut.sendBucks(1001, 1003, new BigDecimal("2000.00"));
        Assert.assertEquals("Funds must be a positive non-zero integer and cannot transfer insufficient funds", result);
    }

    @Test
    public void requestBucks_valid_request_returns_success_message() {
        String result = sut.requestBucks(1001, 1003, new BigDecimal("50.00"));
        Assert.assertEquals("Transfer Request Submitted!", result);
    }

    @Test
    public void getPastTransfers_returns_all_transfers() {

        List<Transfer> transfers = sut.getPastTransfers(1001);

        System.out.println("number of transfer: " + transfers.size());

        List<Transfer> expectedTransfer = new ArrayList<>();
        expectedTransfer.add(TRANSFER_1);

        Assert.assertEquals("Expected one transfer", expectedTransfer.size(), transfers.size());
        System.out.println("Expected Transfer: " + expectedTransfer.get(0));
        System.out.println("Actual Transfer: " + transfers.get(0));
    //    Assert.assertEquals(expectedTransfer,transfers);

    }

    @Test
    public void getPendingTransfers_returns_pending_transfers() {
        List<Transfer> transfers = sut.getPendingTransfers(1003);

        Assert.assertNotNull(transfers);
        Assert.assertEquals(1, transfers.size());
    }

    @Test
    public void approveTransfer_updates_transfer_status_and_balances() {
        sut.approveTransfer(TRANSFER_2);

        List<Transfer> pastTransfers = sut.getPastTransfers(1001);
        System.out.println("Past Transfers: " + pastTransfers);

        Transfer transfer = pastTransfers.stream()
                .filter(t -> t.getTransferId() == 1)
                .findFirst()
                .orElse(null);

        System.out.println("Found Transfer: " + transfer);
        Assert.assertNotNull("Transfer with ID 1 should be present in past transfers.", transfer);

        BigDecimal senderBalance = accountDao.getAccountBalanceById(1001);
        BigDecimal receiverBalance = accountDao.getAccountBalanceById(1002);

        Assert.assertEquals("Sender balance should be updated.", new BigDecimal("1250.00"), senderBalance);
        Assert.assertEquals("Receiver balance should be updated.", new BigDecimal("750.00"), receiverBalance);
    }

    @Test
    public void rejectTransfer_updates_transfer_status() {
        // Verify the transfer exists before updating
        String sqlCheck = "SELECT transfer_status_id FROM transfer WHERE transfer_id = ?";
        List<Integer> statusBefore = jdbcTemplate.queryForList(sqlCheck, Integer.class, TRANSFER_3.getTransferId());
        if (statusBefore.isEmpty()) {
            System.out.println("No transfer found with ID: " + TRANSFER_3.getTransferId());
        } else {
            System.out.println("Transfer status before update: " + statusBefore.get(0));
        }

        String result = sut.rejectTransfer(TRANSFER_3);
        Assert.assertEquals("Transfer Rejected", result);

        // Verify the transfer status was updated correctly in the database
        String sql = "SELECT transfer_status_id FROM transfer WHERE transfer_id = ?";
        List<Integer> updatedStatusList = jdbcTemplate.queryForList(sql, Integer.class, TRANSFER_3.getTransferId());
        if (updatedStatusList.isEmpty()) {
            System.out.println("No transfer found after update with ID: " + TRANSFER_3.getTransferId());
        } else {
            Integer updatedStatusId = updatedStatusList.get(0);
            System.out.println("Transfer status after update: " + updatedStatusId);
            Assert.assertEquals(Integer.valueOf(3), updatedStatusId);
        }
    }
}
