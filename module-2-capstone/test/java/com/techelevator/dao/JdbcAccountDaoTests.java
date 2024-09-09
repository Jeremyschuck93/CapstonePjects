package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import java.math.BigDecimal;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class JdbcAccountDaoTests extends BaseDaoTests{

    private static final Account ACCOUNT_1 = new Account(2001, 1001, new BigDecimal("1500.00"));
    private static final Account ACCOUNT_2 = new Account(2002, 1002, new BigDecimal("2500.00"));
    private static final Account ACCOUNT_3 = new Account(2003, 1003, new BigDecimal("3500.00"));

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        // Initialize JdbcAccountDao
        sut = new JdbcAccountDao(new JdbcTemplate(dataSource));

        String insertSql = "INSERT INTO account (account_id, user_id, balance) VALUES (?, ?, ?);";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.update(insertSql, ACCOUNT_1.getAccountId(), ACCOUNT_1.getUserId(), ACCOUNT_1.getBalance());
        jdbcTemplate.update(insertSql, ACCOUNT_2.getAccountId(), ACCOUNT_2.getUserId(), ACCOUNT_2.getBalance());
        jdbcTemplate.update(insertSql, ACCOUNT_3.getAccountId(), ACCOUNT_3.getUserId(), ACCOUNT_3.getBalance());
    }

    @Test
    public void getAccountBalanceById_returns_correct_balance() {
        BigDecimal expectedBalance = ACCOUNT_1.getBalance();
        int userId = ACCOUNT_1.getUserId();

        BigDecimal actualBalance = sut.getAccountBalanceById(userId);

        Assert.assertNotNull("Balance should not be null", actualBalance);
        Assert.assertEquals("The balance returned is incorrect.", expectedBalance, actualBalance);
        Assert.assertEquals(actualBalance.compareTo(expectedBalance), 0);
    }

    @Test
    public void getAccountByUserId_returns_correct_account() {
        Account account = sut.getAccountByUserId(ACCOUNT_1.getUserId());

        Assert.assertNotNull(account);
        Assert.assertEquals(ACCOUNT_1.getAccountId(), account.getAccountId());
        Assert.assertEquals(ACCOUNT_1.getUserId(), account.getUserId());
        Assert.assertEquals(ACCOUNT_1.getBalance(), account.getBalance());
    }

    @Test
    public void getAccountByAccountId_returns_correct_account() {
        Account account = sut.getAccountByAccountId(ACCOUNT_2.getAccountId());

        Assert.assertNotNull(account);
        Assert.assertEquals(ACCOUNT_2.getAccountId(), account.getAccountId());
        Assert.assertEquals(ACCOUNT_2.getUserId(), account.getUserId());
        Assert.assertEquals(ACCOUNT_2.getBalance(), account.getBalance());
    }

    @Test
    public void addAmount_increases_balance() {
        BigDecimal amountToAdd = new BigDecimal("500.00");
        BigDecimal expectedBalance = ACCOUNT_1.getBalance().add(amountToAdd);

        Account account = sut.addAmount(ACCOUNT_1.getUserId(), amountToAdd);

        Assert.assertEquals(expectedBalance, account.getBalance());
    }

    @Test
    public void subtractAmount_decreases_balance() {
        BigDecimal amountToSubtract = new BigDecimal("700.00");
        BigDecimal expectedBalance = ACCOUNT_2.getBalance().subtract(amountToSubtract);

        Account account = sut.subtractAmount(ACCOUNT_2.getUserId(), amountToSubtract);

        Assert.assertEquals(expectedBalance, account.getBalance());
    }

}
