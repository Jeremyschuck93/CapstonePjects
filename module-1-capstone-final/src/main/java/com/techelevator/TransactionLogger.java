package com.techelevator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionLogger {

    public static final String LOG_FILE = "Log.txt";

    public static void logTransaction(String action, BigDecimal amountBefore,BigDecimal amountAfter) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
            String formattedTime = currentTime.format(formatter);
            writer.println(formattedTime + " " + action + ": $" + amountBefore + " $" + amountAfter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
