package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class AccountContoller {

    private AccountDao accountDao;
    private UserDao userDao;


    public AccountContoller(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @GetMapping("/account/{id}")
    public BigDecimal getBalance(@PathVariable int id){
        BigDecimal balance =  accountDao.getAccountBalanceById(id);
        return balance;
    }

    @GetMapping("/users")
    public List<User> listUsers() {
        List<User> users = userDao.getUsers();
        return users;
    }
}
