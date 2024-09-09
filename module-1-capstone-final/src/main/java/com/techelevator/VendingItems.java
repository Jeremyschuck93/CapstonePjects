package com.techelevator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VendingItems {
    private String slotID;
    private String name;
    private BigDecimal price;
    private String type;
    private int quantity;
    private final int MAX_QUANTITY = 5;

    public VendingItems(String slotID, String name, BigDecimal price, String type) {
        this.slotID = slotID;
        this.name = name;
        this.price = price;
        this.type = type;
        this.quantity = MAX_QUANTITY;
    }

    // Getters
    public String getSlotID() {
        return slotID;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity() {
        if (quantity > 0) quantity--;
    }
}
