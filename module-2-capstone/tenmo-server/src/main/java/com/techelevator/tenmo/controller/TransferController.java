package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransferController {
    private TransferDao transferDao;

    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @PostMapping("/transfer/send")
    public String sendBucks(@RequestBody Transfer transfer) {
        String results = transferDao.sendBucks(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return results;
    }

    @PostMapping("/transfer/request")
    public String sendTransferRequest(@RequestBody Transfer transfer) {
        String results = transferDao.requestBucks(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return results;

    }

    @GetMapping("/transfers/{id}")
    public List<Transfer> listTransfers(@PathVariable int id) {
        List<Transfer> transfers = transferDao.getPastTransfers(id);
        return transfers;
    }

    @GetMapping("/transfer/pending/{id}")
    public List<Transfer> listPendingTransfers(@PathVariable int id) {
        List<Transfer> transfers = transferDao.getPendingTransfers(id);
        return transfers;
    }

    @PostMapping("/transfer/approve")
    public String approveTransaction(@RequestBody Transfer transfer) {
        String results = transferDao.approveTransfer(transfer);
        return results;
    }

    @PostMapping("/transfer/reject")
    public String rejectTransaction(@RequestBody Transfer transfer) {
        String results = transferDao.rejectTransfer(transfer);
        return results;
    }
}
