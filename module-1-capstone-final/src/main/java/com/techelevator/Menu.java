package com.techelevator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

public class Menu {
    private final VendingMachine vendingMachine;

    public Menu(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    public void displayMainMenu() {
        Scanner sc = new Scanner(System.in);
        boolean isRunning = true;
        while(isRunning) {
            System.out.println("*****************************");
            System.out.println("********** WELCOME **********");
            System.out.println("*****************************");
            System.out.println("Main Menu:");
            System.out.println("(1) Display Vending Machine Items");
            System.out.println("(2) Purchase");
            System.out.println("(3) Exit");
            System.out.print("Select an option: ");
            int option = sc.nextInt();
            switch (option) {
                case 1:
                    displayVendingMachineItems();
                    break;
                case 2:
                    purchaseMenu();
                    break;
                case 3:
                    isRunning = false;
                    System.out.println("************************************");
                    System.out.println("*** Thank you for your business! ***");
                    System.out.println("************************************");
                    break;
                case 4:
                    SalesReportGenerator.generateSalesReport(vendingMachine.getSalesData());
                    runLoggedTransactions();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void displayVendingMachineItems() {
        for (VendingItems item : vendingMachine.getInventory().values()) {
            System.out.println(item.getSlotID() + " |" + item.getName() +"|$" + item.getPrice() +"|" + item.getType());
        }
    }

    private void purchaseMenu() {
        Scanner sc = new Scanner(System.in);
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("***********************");
            System.out.println("**** Purchase Menu ****");
            System.out.println("***********************");
            System.out.println("Current Money Provided: $" + vendingMachine.getBalance());
            System.out.println("(1) Feed Money");
            System.out.println("(2) Select Product");
            System.out.println("(3) Finish Transaction");
            System.out.print("Select an option: ");
            int option = sc.nextInt();
            switch (option) {
                case 1:
                    System.out.print("Enter amount to feed: ");
                    BigDecimal amount = sc.nextBigDecimal();
                    vendingMachine.feedMoney(amount);
                    break;
                case 2:
                    System.out.print("Enter slot location: ");
                    String slotLocation = sc.next();
                    vendingMachine.purchase(slotLocation);
                    break;
                case 3:
                    vendingMachine.finishTransaction();
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void runLoggedTransactions() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Log.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading the log file.");
        }
    }
}
