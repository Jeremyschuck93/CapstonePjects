package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.List;


public class AccountService {

    private String baseUrl ;
    private final RestTemplate restTemplate = new RestTemplate();

    AuthenticatedUser currentUser;

    public AccountService(String baseUrl, AuthenticatedUser currentUser) {
        this.baseUrl = baseUrl;
        this.currentUser = currentUser;
    }

    public void getUsers() {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(baseUrl + "/users",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                });
        List<User> users = responseEntity.getBody();
        for (User user : users) {
            if (user.getId() == currentUser.getUser().getId()) {
            } else
                System.out.println(user.getId() + "      " + user.getUsername());
        }
    }

    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal(String.valueOf(restTemplate.getForObject(baseUrl + "/account/" + currentUser.getUser().getId(), BigDecimal.class)));
        System.out.println("Your current account balance is: " + balance);
        return balance;
    }
}
