package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public int promptForApprovalSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public void   printTransferableUsers() {

        System.out.println("-----------------------------");
        System.out.println("--------Send TE Bucks--------");

        System.out.println("Users");
        System.out.println("ID        Name");
        System.out.println("-----------------------------");
    }

    public void   printRequestableUsers() {

        System.out.println("-----------------------------");
        System.out.println("--------Request TE Bucks--------");

        System.out.println("Users");
        System.out.println("ID        Name");
        System.out.println("-----------------------------");
    }

    public void   printPastTransfers() {

        System.out.println("-----------------------------");
        System.out.println("--------Past Transfers-------");
        System.out.println("-----------------------------");
        System.out.println("ID       From/To       Amount");
        System.out.println("-----------------------------");
        System.out.println();
    }

    public void   printPendingTransfers() {
        System.out.println("-----------------------------");
        System.out.println("------Pending Transfers------");
        System.out.println("-----------------------------");
        System.out.println("ID          To         Amount");
        System.out.println("-----------------------------");
        System.out.println();
    }

    public BigDecimal promptForUserAmount() {
        System.out.println("\n Enter an amount to transfer");

        BigDecimal BDinput = null;
        try {
            BDinput = new BigDecimal(String.valueOf(scanner.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Incorrect number format");
        }
        return BDinput;
    }

    public void   printApprovalMenu() {
        System.out.println("-----------------------------");
        System.out.println("---Please Select an option---");
        System.out.println("-----------------------------");
        System.out.println();
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println();
    }

}
