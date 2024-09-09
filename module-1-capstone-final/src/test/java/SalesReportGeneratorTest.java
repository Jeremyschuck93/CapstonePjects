import static org.junit.jupiter.api.Assertions.*;

import com.techelevator.SalesReportGenerator;
import com.techelevator.VendingItems;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SalesReportGeneratorTest {

    private Map<String, VendingItems> inventory;
    private Map<String, Integer> salesData;

    @BeforeEach
    public void setUp() {
        inventory = new HashMap<>();
        salesData = new HashMap<>();
        inventory.put("A1", new VendingItems("A1", "Potato Crisps", new BigDecimal("3.05"), "Chip"));
        inventory.put("B1", new VendingItems("B1", "Moonpie", new BigDecimal("1.80"), "Candy"));
        salesData.put("Potato Crisps", 1);
        salesData.put("Moonpie", 1);
        SalesReportGenerator.setInventory(inventory);

    }

    @Test
    public void testCalculateTotalSales() {

        BigDecimal totalSales = SalesReportGenerator.calculateTotalSales(salesData);
        assertEquals(new BigDecimal("4.85"), totalSales);


    }
}
