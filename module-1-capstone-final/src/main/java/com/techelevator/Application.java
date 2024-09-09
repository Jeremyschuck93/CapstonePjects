package com.techelevator;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Application {

	public static void main(String[] args) {

		// Initialize the vending machine
		VendingMachine vendingMachine = new VendingMachine();
		// Load the inventory from a CSV file
		vendingMachine.loadInventory("vendingmachine.csv"); // Ensure the file exists with proper format
		// Create the menu and display the main menu
		Menu menu = new Menu(vendingMachine);


		menu.displayMainMenu();
	}
}

