import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.time.LocalDate;
import java.util.List;
import com.dragoncon_reminder.util.DragonConRateParser;
import com.dragoncon_reminder.util.DragonConRate;

public class DragonConRateParserTest {

    /**
     * Helper method to create a mock HTML document with the given rate content.
     * The parser expects content with actual text nodes separated by br elements.
     * @param rates variable number of rate strings
     * @return a Jsoup Document with the mock HTML structure
     */
    private final Document createMockDocument(final String... rates) {
        final Document doc = Jsoup.parse(
            "<html><body>" +
            "<h2>Dragon Con 5-Day Membership Rates</h2>" +
            "<p></p>" +
            "</body></html>"
        );

        // Build content with text nodes and br elements
        final Element p = doc.select("p").first();
        for (int i = 0; i < rates.length; i++) {
            p.appendText(rates[i]);
            if (i < rates.length - 1) {
                p.appendElement("br");
            }
        }

        return doc;
    }

    /**
     * Test parsing a single rate with a valid deadline.
     */
    @Test
    void testFetchRatesAndDeadlines_SingleRateWithDeadline() throws Exception {
        final Document mockDoc = createMockDocument("$100 through 12/31/2025");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();
        final DragonConRate rate = rates.get(0);

        Assertions.assertEquals(1, rates.size(), "Should parse one rate");
        Assertions.assertEquals("$100", rate.getPrice(), "Price should be $100");
        Assertions.assertEquals(LocalDate.of(2025, 12, 31), rate.getDeadline(), "Deadline should be 12/31/2025");
        Assertions.assertFalse(rate.getIsTBA(), "Should not be TBA");
    }

    /**
     * Test parsing a TBA price.
     */
    @Test
    void testFetchRatesAndDeadlines_TBAPrice() throws Exception {
        final Document mockDoc = createMockDocument("TBA through 12/31/2025");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();
        final DragonConRate rate = rates.get(0);

        Assertions.assertEquals(1, rates.size());
        Assertions.assertEquals("TBA", rate.getPrice(), "Price should be TBA");
        Assertions.assertTrue(rate.getIsTBA(), "Should be marked as TBA");
        Assertions.assertEquals(LocalDate.of(2025, 12, 31), rate.getDeadline());
    }

    /**
     * Test parsing a rate without a deadline (no "through" clause).
     */
    @Test
    void testFetchRatesAndDeadlines_RateWithoutDeadline() throws Exception {
        final Document mockDoc = createMockDocument("$200");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();
        final DragonConRate rate = rates.get(0);

        Assertions.assertEquals(1, rates.size());
        Assertions.assertEquals("$200", rate.getPrice());
        Assertions.assertNull(rate.getDeadline(), "Deadline should be null when no date is provided");
    }

    /**
     * Test parsing with various date formats (single digit month/day).
     */
    @Test
    void testFetchRatesAndDeadlines_SingleDigitDateFormat() throws Exception {
        final Document mockDoc = createMockDocument("$100 through 1/5/2025");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();
        final DragonConRate rate = rates.get(0);

        Assertions.assertEquals(1, rates.size());
        Assertions.assertEquals(LocalDate.of(2025, 1, 5), rate.getDeadline(), "Should parse single-digit month and day");
    }

    /**
     * Test parsing with trailing punctuation (period, semicolon).
     */
    @Test
    void testFetchRatesAndDeadlines_TrailingPunctuation() throws Exception {
        final Document mockDoc = createMockDocument("$100 through 6/15/2025.");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();

        Assertions.assertEquals(1, rates.size());
        Assertions.assertEquals(LocalDate.of(2025, 6, 15), rates.get(0).getDeadline(), "Should strip trailing period");
    }

    /**
     * Test parsing with "Through" (capitalized).
     */
    @Test
    void testFetchRatesAndDeadlines_CapitalizedThrough() throws Exception {
        final Document mockDoc = createMockDocument("$100 Through 12/31/2025");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();

        Assertions.assertEquals(1, rates.size());
        Assertions.assertEquals("$100", rates.get(0).getPrice());
        Assertions.assertEquals(LocalDate.of(2025, 12, 31), rates.get(0).getDeadline());
    }

    /**
     * Test parsing with extra whitespace around "through".
     */
    @Test
    void testFetchRatesAndDeadlines_ExtraWhitespace() throws Exception {
        final Document mockDoc = createMockDocument("$100   through   12/31/2025");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();

        Assertions.assertEquals(1, rates.size());
        Assertions.assertEquals("$100", rates.get(0).getPrice());
        Assertions.assertEquals(LocalDate.of(2025, 12, 31), rates.get(0).getDeadline());
    }

    /**
     * Test parsing with invalid date format (should result in null deadline).
     */
    @Test
    void testFetchRatesAndDeadlines_InvalidDateFormat() throws Exception {
        final Document mockDoc = createMockDocument("$100 through December 31, 2025");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();

        Assertions.assertEquals(1, rates.size());
        Assertions.assertEquals("$100", rates.get(0).getPrice());
        Assertions.assertNull(rates.get(0).getDeadline(), "Deadline should be null for invalid date format");
    }

    /**
     * Test that raw line is preserved correctly.
     */
    @Test
    void testFetchRatesAndDeadlines_RawLinePreserved() throws Exception {
        final String expectedRawLine = "$100 through 12/31/2025";
        final Document mockDoc = createMockDocument(expectedRawLine);
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();

        Assertions.assertEquals(1, rates.size());
        Assertions.assertEquals(expectedRawLine, rates.get(0).getRawLine(), "Raw line should be preserved");
    }

    /**
     * Test error handling when heading is missing.
     */
    @Test
    void testFetchRatesAndDeadlines_MissingHeading() {
        final String html = "<html><body><p>$100 through 12/31/2025</p></body></html>";
        final Document mockDoc = Jsoup.parse(html);
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            parser.fetchRatesAndDeadlines();
        });

        Assertions.assertTrue(exception.getMessage().contains("section heading"),
            "Exception should mention missing section heading");
    }

    /**
     * Test error handling when content block is missing after heading.
     */
    @Test
    void testFetchRatesAndDeadlines_MissingContentBlock() {
        final String html = "<html><body><h2>Dragon Con 5-Day Membership Rates</h2></body></html>";
        final Document mockDoc = Jsoup.parse(html);
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            parser.fetchRatesAndDeadlines();
        });

        Assertions.assertTrue(exception.getMessage().contains("content after the heading"),
            "Exception should mention missing content after heading");
    }

    /**
     * Test parsing with case-insensitive "tba".
     */
    @Test
    void testFetchRatesAndDeadlines_TBACaseInsensitive() throws Exception {
        final Document mockDoc = createMockDocument("tba through 6/15/2025");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();

        Assertions.assertEquals(1, rates.size());
        Assertions.assertTrue(rates.get(0).getIsTBA(), "Lowercase 'tba' should be recognized");
    }

    /**
     * Test parsing with realistic DragonCon-style content (at the door pricing).
     */
    @Test
    void testFetchRatesAndDeadlines_AtTheDoorPricing() throws Exception {
        final Document mockDoc = createMockDocument("$175 at the door");
        final DragonConRateParser parser = new DragonConRateParser(mockDoc);

        final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();

        Assertions.assertEquals(1, rates.size());
        Assertions.assertNull(rates.get(0).getDeadline(), "'At the door' pricing should have no deadline");
        Assertions.assertEquals("$175 at the door", rates.get(0).getPrice());
    }
}
