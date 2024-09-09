package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    String sendBucks(int userFrom, int userTo, BigDecimal amount);
    String requestBucks(int userFrom, int userTo, BigDecimal amount);
    String approveTransfer(Transfer transfer);
    String rejectTransfer(Transfer transfer);

    List<Transfer> getPastTransfers(int accountId);
    List<Transfer> getPendingTransfers(int userId);
}
