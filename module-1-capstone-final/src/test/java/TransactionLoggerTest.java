import com.techelevator.TransactionLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.math.BigDecimal;




class TransactionLoggerTest {
    private static final String LOG_FILE = "Log.txt";

    @BeforeEach
    public void setUp() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE))) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE))) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testLogTransaction() {
        TransactionLogger.logTransaction("FEED MONEY", new BigDecimal("5.00"), new BigDecimal("10.00"));

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line = reader.readLine();
            assertNotNull(line);
            assertTrue(line.contains("FEED MONEY: $5.00 $10.00"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to read log file");
        }
    }
}


