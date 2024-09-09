import static org.junit.jupiter.api.Assertions.*;


import com.techelevator.VendingItems;
import org.junit.jupiter.api.*;
import com.techelevator.VendingMachine;

import java.math.BigDecimal;
import java.util.Map;


public class VendingMachineTest {

    private VendingMachine vendingMachine;

    @BeforeEach
    public void setUp() {
        vendingMachine = new VendingMachine();
    }

    //testLoadInventory()
    @Test
    public void testLoadInventory() {
        vendingMachine.loadInventory("vendingmachine.csv");

        Map<String, VendingItems> inventory = vendingMachine.getInventory();
        assertEquals(16, inventory.size());

        VendingItems potatoCrisps = inventory.get("A1");
        assertEquals("Potato Crisps", potatoCrisps.getName());
        assertEquals(new BigDecimal("3.05"), potatoCrisps.getPrice());
        assertEquals("Chip", potatoCrisps.getType());

        VendingItems moonPie = inventory.get("B1");
        assertEquals("Moonpie", moonPie.getName());
        assertEquals(new BigDecimal("1.80"), moonPie.getPrice());
        assertEquals("Candy", moonPie.getType());

    }

    //testFeedMoney()
    @Test
    public void testFeedMoney(){

        vendingMachine.feedMoney(new BigDecimal("5.00"));
        assertEquals(new BigDecimal("5.00"), vendingMachine.getBalance());
    }

    //testPurchase
    @Test
    public void testPurchase() {
        vendingMachine.loadInventory("vendingmachine.csv");

        vendingMachine.feedMoney(new BigDecimal("5.00"));
        vendingMachine.purchase("A1");

        assertEquals(new BigDecimal("1.95"), vendingMachine.getBalance());
        assertEquals(1,vendingMachine.getSalesData().get("Potato Crisps"));
    }


    //testDispenseMessage()
    @Test
    public void testDispenseMessage(){

        vendingMachine.getDispenseMessage("Chip");
        vendingMachine.getDispenseMessage("Candy");

        assertEquals("Crunch Crunch, Yum!",vendingMachine.getDispenseMessage("chip"));
        assertEquals("Munch Munch, Yum!",vendingMachine.getDispenseMessage("candy"));
    }

    @Test
    public void testFinishTransaction() {

        vendingMachine.feedMoney(new BigDecimal("1.45"));
        vendingMachine.finishTransaction();

        assertEquals(BigDecimal.ZERO, vendingMachine.getBalance());
    }
}
