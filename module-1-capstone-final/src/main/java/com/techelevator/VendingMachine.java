package com.techelevator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class VendingMachine {
    private LinkedHashMap<String, Integer> salesData;
    private BigDecimal balance;
    private LinkedHashMap<String, VendingItems> inventory;

    public VendingMachine()  {
        inventory = new LinkedHashMap<>();
        balance = BigDecimal.ZERO;
        salesData = new LinkedHashMap<>();
    }

    public void loadInventory(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                String slotLocation = parts[0];
                String name = parts[1];
                BigDecimal price = new BigDecimal(parts[2]);
                String type = parts[3];
                VendingItems item = new VendingItems(slotLocation, name, price, type);
                inventory.put(slotLocation, item);
                salesData.put(name, 0); // Initialize sales data
            }
            SalesReportGenerator.setInventory(inventory); // Set inventory in SalesReportGenerator
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void feedMoney(BigDecimal amount) {
        BigDecimal balanceBefore = balance;
        balance = balance.add(amount);
        TransactionLogger.logTransaction("FEED MONEY", balanceBefore, balance);
    }

    public void purchase(String slotID) {
        VendingItems item = inventory.get(slotID);
        if (item == null) {
            System.out.println("Invalid product code");
            return;
        }
        if (item.getQuantity() == 0) {
            System.out.println("SOLD OUT.");
        }
        if (balance.compareTo(item.getPrice()) < 0) {
            System.out.println("Insufficient Funds.");
            return;
        }
        BigDecimal balanceBefore = balance;
        balance = balance.subtract(item.getPrice());
        item.reduceQuantity();
        salesData.put(item.getName(), salesData.get(item.getName()) +1);
        TransactionLogger.logTransaction(item.getName() + " " + slotID, balanceBefore, balance);
        System.out.println("Dispensed: " + item.getName() + " Cost: $" + item.getPrice() + " Remaining Balance: $" + balance);
        System.out.println(getDispenseMessage(item.getType()));
    }


    public void finishTransaction() {
        BigDecimal balanceBefore = balance;
        giveChange(balance);
        TransactionLogger.logTransaction("GIVE CHANGE", balanceBefore, BigDecimal.ZERO);
        balance = BigDecimal.ZERO;

    }

    public void giveChange(BigDecimal amount) {
        int quarters = amount.divide(new BigDecimal("0.25")).intValue();
        amount = amount.remainder(new BigDecimal("0.25"));
        int dimes = amount.divide(new BigDecimal("0.10")).intValue();
        amount = amount.remainder(new BigDecimal("0.10"));
        int nickels = amount.divide(new BigDecimal("0.05")).intValue();

        System.out.println("Change given: " + quarters + " quarters, " + dimes + " dimes, " + nickels + " nickels.");
    }

    public String getDispenseMessage(String type) {
        switch (type.toLowerCase()) {
            case "chip": return "Crunch Crunch, Yum!";
            case "candy": return "Munch Munch, Yum!";
            case "drink": return "Glug Glug, Yum!";
            case "gum": return "Chew Chew, Yum!";
            default: return "";
        }
    }

    public Map<String, VendingItems> getInventory() { return inventory; }
    public BigDecimal getBalance() { return balance; }
    public Map<String, Integer> getSalesData() { return salesData; }

}
