import com.techelevator.VendingItems;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;


public class VendingItemsTest {
    private VendingItems potatoCrisps;
    private VendingItems moonPie;

    @BeforeEach
    public void setUp() {
        potatoCrisps = new VendingItems("A1", "Potato Crisps", new BigDecimal("3.05"),"Chip");
        moonPie = new VendingItems("B1", "Moonpie", new BigDecimal("1.80"),"Candy");
    }

    @Test
    public void testGetSlotId() {
        assertEquals("A1", potatoCrisps.getSlotID());
        assertEquals("B1", moonPie.getSlotID());

    }

    @Test
    public void testGetName() {
        assertEquals("Potato Crisps", potatoCrisps.getName());
        assertEquals("Moonpie", moonPie.getName());

    }

    @Test
    public void testGetPrice() {
        assertEquals(new BigDecimal("3.05"), potatoCrisps.getPrice());
        assertEquals(new BigDecimal("1.80"), moonPie.getPrice());
    }

    @Test
    public void testGetType() {
        assertEquals("Chip", potatoCrisps.getType());
        assertEquals("Candy", moonPie.getType());
    }

    @Test
    public void testGetQuantity() {
        assertEquals(5, potatoCrisps.getQuantity());
        assertEquals(5, moonPie.getQuantity());
    }

    @Test
    public void testReduceQuantity() {
        potatoCrisps.reduceQuantity();
        assertEquals(4, potatoCrisps.getQuantity());
        moonPie.reduceQuantity();;
        assertEquals(4, moonPie.getQuantity());
    }
}
