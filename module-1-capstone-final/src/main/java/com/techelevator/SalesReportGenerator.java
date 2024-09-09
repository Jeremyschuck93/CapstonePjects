package com.techelevator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SalesReportGenerator {

    private static Map<String,VendingItems> inventory;


    public static void setInventory(Map<String, VendingItems> inventory) {
        SalesReportGenerator.inventory = inventory;
    }

    public static void generateSalesReport(Map<String, Integer> salesData) {
        String filename = generateFileName();
        try(PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Integer> entry : salesData.entrySet()) {
                writer.println(entry.getKey() + "|" + entry.getValue());
            }
            writer.println();
            writer.println("** TOTAL SALES ** $" + calculateTotalSales(salesData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateFileName() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formattedTime = currentTime.format(formatter);
        return "SalesReport_" + formattedTime + ".txt";

    }

    public static BigDecimal calculateTotalSales (Map<String, Integer> salesData) {
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : salesData.entrySet()){
            BigDecimal price = getPrice(entry.getKey());
            BigDecimal quantity = BigDecimal.valueOf(entry.getValue());
            totalSales = totalSales.add(price.multiply(quantity));
        }
        return totalSales;
    }

    private static BigDecimal getPrice(String productName) {
        if (inventory == null) {
            throw new IllegalStateException("Inventory is not set");
        }
        for (VendingItems items : inventory.values()) {
            if (items.getName().equals(productName)) {
                    return items.getPrice();

            }
        }
        return BigDecimal.ZERO;
    }

}
