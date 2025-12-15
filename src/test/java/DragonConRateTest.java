import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.time.LocalDate;
import com.dragoncon_reminder.util.DragonConRate;

public class DragonConRateTest {
    /**
     * This method tests the creation of a DragonConRate object.
     */
    @Test
    void testDragonConRateCreation() {
        // Expected values to create the object with.
        final String expectedRawLine = "$100 through 11/4/2025";
        final LocalDate expectedDeadline = LocalDate.of(2025, 11, 4);
        final String expectedPrice = "$100";
        final boolean expectedTBA = false;

        // Test creating the object.
        final DragonConRate testRateObject = new DragonConRate(expectedRawLine, 
        expectedPrice, 
        expectedTBA, 
        expectedDeadline);

        // Confirm that expected values match actual.
        Assertions.assertEquals(expectedRawLine, testRateObject.getRawLine(), "The expected and actual raw line objects should match.");
        Assertions.assertEquals(expectedPrice, testRateObject.getPrice(), "The expected and actual prices should match.");
        Assertions.assertEquals(expectedTBA, testRateObject.getIsTBA(), "The expected and actual boolean TBA values should match.");
        Assertions.assertEquals(expectedDeadline, testRateObject.getDeadline(), "The expected and actual deadline objects should match.");
    }
}
