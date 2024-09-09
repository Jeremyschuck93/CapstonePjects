package com.techelevator.tenmo.model;

import com.techelevator.tenmo.controller.TransferController;

import java.math.BigDecimal;

public class Transfer {
    private int TransferId;
    private int transferTypeId;
    private int transferStatusId;

    private int accountFrom;
    private int accountTo;

    private String userFrom;
    private String userTo;

    private BigDecimal amount;

    public Transfer(){

    }

    public Transfer(int transferId, int transferTypeId, int transferStatusId, int accountFrom, int accountTo, String userFrom, String userTo, BigDecimal amount) {
        TransferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.amount = amount;
    }

    public int getTransferId() {
        return TransferId;
    }

    public void setTransferId(int transferId) {
        TransferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
