package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {
    BigDecimal getAccountBalanceById(int id);

    Account getAccountByUserId(int id);
    Account addAmount(int id, BigDecimal amount);
    Account subtractAmount(int id, BigDecimal amount);
    Account getAccountByAccountId(int id);

}
