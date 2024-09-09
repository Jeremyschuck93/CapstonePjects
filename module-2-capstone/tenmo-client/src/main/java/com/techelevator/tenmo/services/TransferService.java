package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class TransferService {

    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    AuthenticatedUser currentUser;
    private final Scanner scanner = new Scanner(System.in);

    public TransferService(String baseUrl, AuthenticatedUser currentUser) {
        this.baseUrl = baseUrl;
        this.currentUser = currentUser;
    }

    private ConsoleService consoleService = new ConsoleService();

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    public void sendBucks() {
        Transfer transfer = new Transfer();
        AccountService accountService = new AccountService(baseUrl, currentUser);
        consoleService.printTransferableUsers();
        accountService.getUsers();
        System.out.println("-----------------------------");
        System.out.println("Please Enter User id To Send to.");
        System.out.println("-----------------------------");
        try {
            transfer.setAccountTo(Integer.parseInt(scanner.nextLine()));
            transfer.setAccountFrom(currentUser.getUser().getId());
        } catch (NumberFormatException ex) {
            System.out.println("Error: Invalid user ID input");
            return;
        }

        System.out.println("-----------------------------");
        System.out.println("---Please Enter the Amount---.");
        System.out.println("-----------------------------");
        String amountInput = scanner.nextLine();
        // Validate input before setting it to BigDecimal
        if (isValidAmountInput(amountInput)) {
            transfer.setAmount(new BigDecimal(amountInput));
            String result = restTemplate.exchange(baseUrl + "/transfer/send", HttpMethod.POST, makeTransferEntity(transfer), String.class).getBody();
            System.out.println(result);
        } else {
            System.out.println("Invalid input. Please enter a valid amount.");
        }
    }

    public void requestBucks() {
        Transfer transfer = new Transfer();
        AccountService accountService = new AccountService(baseUrl, currentUser);
        consoleService.printRequestableUsers();
        accountService.getUsers();
        System.out.println("-----------------------------");
        System.out.println("Please Enter User id To Request From.");
        System.out.println("-----------------------------");
        try {
            transfer.setAccountTo(currentUser.getUser().getId());
            transfer.setAccountFrom(Integer.parseInt(scanner.nextLine()));
        } catch (NumberFormatException ex) {
            System.out.println("Error: Invalid user ID input");
            return;
        }

        System.out.println("-----------------------------");
        System.out.println("---Please Enter the Amount---.");
        System.out.println("-----------------------------");
        String amountInput = scanner.nextLine();
        // Validate input before setting it to BigDecimal
        if (isValidAmountInput(amountInput)) {
            transfer.setAmount(new BigDecimal(amountInput));
            String result = restTemplate.exchange(baseUrl + "/transfer/request", HttpMethod.POST, makeTransferEntity(transfer), String.class).getBody();
            System.out.println(result);
        } else {
            System.out.println("Invalid input. Please enter a valid amount.");
        }
    }

    public void transferDetails(Transfer transfer){
        System.out.println("-----------------------------");
        System.out.println("       Transfer Details      ");
        System.out.println("-----------------------------");
        System.out.println("ID: " + transfer.getTransferId());
        System.out.println("From: " + transfer.getUserFrom());
        System.out.println("To: " + transfer.getUserTo());
        String type = null;
        if (transfer.getTransferTypeId() == 1){
            type = "Request";
        } else if (transfer.getTransferTypeId() == 2) {
            type = "Send";
        }
        System.out.println("Type: " + type);
        String status = null;
        if (transfer.getTransferStatusId() == 1) {
            status = "Pending";
        } else if (transfer.getTransferStatusId() == 2) {
            status = "Approved";
        } else if (transfer.getTransferStatusId() == 3) {
            status = "Rejected";
        }
        System.out.println("Status: " + status);
        System.out.println("Amount: " + transfer.getAmount());
    }

    public void getPastTransfers() {
        consoleService.printPastTransfers();
        ResponseEntity<List<Transfer>> responseEntity = restTemplate.exchange(
                baseUrl + "/transfers/" + currentUser.getUser().getId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transfer>>() {}
        );
        List<Transfer> transfers = responseEntity.getBody();
        for (Transfer transfer : transfers) {
            String ToFrom = "";
            String userName = "";
            if (transfer.getTransferTypeId() == 1) {
                ToFrom = "From:";
                userName = transfer.getUserFrom();
            } else if (transfer.getTransferTypeId() == 2) {
                ToFrom = "To:";
                userName = transfer.getUserTo();
            }
            System.out.println(transfer.getTransferId() + "     " +ToFrom + " " + userName + "     " + transfer.getAmount()); // prints a line including the transfer id, ToFrom(type of transfer),the users name, and the amount
        }
    }

    public void getPendingTransfers() {
        boolean restart = true; // Flag to control whether to restart the method or not
        while (restart) {
            consoleService.printPendingTransfers();
            ResponseEntity<List<Transfer>> responseEntity = restTemplate.exchange(
                    baseUrl + "/transfer/pending/" + currentUser.getUser().getId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Transfer>>() {}
            );
            List<Transfer> transfers = responseEntity.getBody();
            for (Transfer transfer : transfers) {
                String userName = transfer.getUserTo();
                System.out.println(transfer.getTransferId() + "        " + userName + "       " + transfer.getAmount());
            }
            System.out.println();
            System.out.println("Please enter transfer ID to approve/reject (0 to cancel):");
            try {
                int userInput = Integer.parseInt(scanner.nextLine());
                boolean found = false;
                for (Transfer transfer : transfers) {
                    if (transfer.getTransferId() == userInput) {
                        found = true;
                        transferDetails(transfer);
                        consoleService.printApprovalMenu();
                        int menuSelection = consoleService.promptForApprovalSelection(" Please Select an option:");
                        if (menuSelection == 1){
                            approveTransfer(transfer);
                        } else if (menuSelection == 2) {
                            rejectTransfer(transfer);
                        } else if (menuSelection == 3) {
                            // Handle other menu selections
                        } else {
                            System.out.println("Invalid Selection, Please make a valid selection:");
                            consoleService.printApprovalMenu();
                            menuSelection = consoleService.promptForApprovalSelection(" Please Select an option:");
                        }
                        break;
                    }
                }
                if (!found && userInput != 0) {
                    System.out.println("No such transfer exists");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine(); // Wait for the user to press Enter
                    continue; // Restart the loop
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
            restart = false; // Set restart to false to exit the loop
        }
    }

    private boolean isValidAmountInput(String input) {
        try {
            new BigDecimal(input);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void approveTransfer(Transfer transfer) {
        String result = restTemplate.exchange(baseUrl + "/transfer/approve", HttpMethod.POST, makeTransferEntity(transfer), String.class).getBody();//sends request to server
        System.out.println("Transfer Approved");
    }

    public void rejectTransfer(Transfer transfer) {
        String result = restTemplate.exchange(baseUrl + "/transfer/reject", HttpMethod.POST, makeTransferEntity(transfer), String.class).getBody();//sends request to server
        System.out.println("Transfer Rejected");
    }
}
