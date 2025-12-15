import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.time.LocalDate;
import com.dragoncon_reminder.util.Reminder;
import com.dragoncon_reminder.util.Constants;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.dragoncon_reminder.util.DragonConRate;

public class ReminderTest {
    // Format: "The DragonCon membership price (current: %s) will increase in %d day(s) (%s)."
    private final Reminder reminder = new Reminder();

    /**
     * Test that a reminder is generated when deadline is exactly 30 days away.
     */
    @Test
    void testReminderBuild_ThirtyDaysBeforeDeadline() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate deadline = today.plusDays(30);
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$100 through " + deadline, "$100", false, deadline)
        );

        String result = reminder.build(rates);

        Assertions.assertFalse(result.isEmpty(), "Reminder should not be empty for 30-day threshold");
        Assertions.assertTrue(result.contains("$100"), "Reminder should contain the price");
        Assertions.assertTrue(result.contains("30 day(s)"), "Reminder should mention 30 days");
        Assertions.assertTrue(result.contains(deadline.toString()), "Reminder should contain the deadline date");
    }

    /**
     * Test that a reminder is generated when deadline is exactly 14 days away.
     */
    @Test
    void testReminderBuild_FourteenDaysBeforeDeadline() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate deadline = today.plusDays(14);
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$125 through " + deadline, "$125", false, deadline)
        );

        String result = reminder.build(rates);

        Assertions.assertFalse(result.isEmpty(), "Reminder should not be empty for 14-day threshold");
        Assertions.assertTrue(result.contains("$125"), "Reminder should contain the price");
        Assertions.assertTrue(result.contains("14 day(s)"), "Reminder should mention 14 days");
    }

    /**
     * Test that a reminder is generated when deadline is exactly 7 days away.
     */
    @Test
    void testReminderBuild_SevenDaysBeforeDeadline() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate deadline = today.plusDays(7);
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$150 through " + deadline, "$150", false, deadline)
        );

        String result = reminder.build(rates);

        Assertions.assertFalse(result.isEmpty(), "Reminder should not be empty for 7-day threshold");
        Assertions.assertTrue(result.contains("$150"), "Reminder should contain the price");
        Assertions.assertTrue(result.contains("7 day(s)"), "Reminder should mention 7 days");
    }

    /**
     * Test that a reminder is generated when deadline is exactly 3 days away.
     */
    @Test
    void testReminderBuild_ThreeDaysBeforeDeadline() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate deadline = today.plusDays(3);
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$175 through " + deadline, "$175", false, deadline)
        );

        String result = reminder.build(rates);

        Assertions.assertFalse(result.isEmpty(), "Reminder should not be empty for 3-day threshold");
        Assertions.assertTrue(result.contains("$175"), "Reminder should contain the price");
        Assertions.assertTrue(result.contains("3 day(s)"), "Reminder should mention 3 days");
    }

    /**
     * Test that a reminder is generated when deadline is exactly 1 day away.
     */
    @Test
    void testReminderBuild_OneDayBeforeDeadline() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate deadline = today.plusDays(1);
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$200 through " + deadline, "$200", false, deadline)
        );

        String result = reminder.build(rates);

        Assertions.assertFalse(result.isEmpty(), "Reminder should not be empty for 1-day threshold");
        Assertions.assertTrue(result.contains("$200"), "Reminder should contain the price");
        Assertions.assertTrue(result.contains("1 day(s)"), "Reminder should mention 1 day");
    }

    /**
     * Test that a reminder is generated when deadline is today (0 days away).
     */
    @Test
    void testReminderBuild_DeadlineIsToday() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$225 through " + today, "$225", false, today)
        );

        String result = reminder.build(rates);

        Assertions.assertFalse(result.isEmpty(), "Reminder should not be empty when deadline is today");
        Assertions.assertTrue(result.contains("$225"), "Reminder should contain the price");
        Assertions.assertTrue(result.contains("0 day(s)"), "Reminder should mention 0 days");
    }

    /**
     * Test that NO reminder is generated when deadline is not at a threshold (e.g., 5 days).
     */
    @Test
    void testReminderBuild_NoReminderForNonThresholdDays() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate deadline = today.plusDays(5); // Not a threshold (30, 14, 7, 3, 1, 0)
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$150 through " + deadline, "$150", false, deadline)
        );

        String result = reminder.build(rates);

        Assertions.assertTrue(result.isEmpty(), "Reminder should be empty when deadline is not at a threshold");
    }

    /**
     * Test that NO reminder is generated when deadline is null.
     */
    @Test
    void testReminderBuild_NullDeadline() {
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("TBA", "TBA", true, null)
        );

        String result = reminder.build(rates);

        Assertions.assertTrue(result.isEmpty(), "Reminder should be empty when deadline is null");
    }

    /**
     * Test that NO reminder is generated when deadline has already passed.
     */
    @Test
    void testReminderBuild_PastDeadline() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate pastDeadline = today.minusDays(10);
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$100 through " + pastDeadline, "$100", false, pastDeadline)
        );

        String result = reminder.build(rates);

        Assertions.assertTrue(result.isEmpty(), "Reminder should be empty when deadline is in the past");
    }

    /**
     * Test that NO reminder is generated when the rate list is empty.
     */
    @Test
    void testReminderBuild_EmptyRatesList() {
        final List<DragonConRate> emptyRates = new ArrayList<>();

        String result = reminder.build(emptyRates);

        Assertions.assertTrue(result.isEmpty(), "Reminder should be empty when rate list is empty");
    }

    /**
     * Test with multiple rates where only one matches a threshold.
     */
    @Test
    void testReminderBuild_MultipleRatesOneMatchesThreshold() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate deadline7Days = today.plusDays(7);
        final LocalDate deadline5Days = today.plusDays(5); // Not a threshold
        final LocalDate deadline30Days = today.plusDays(30);

        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$100 through " + deadline5Days, "$100", false, deadline5Days),
            new DragonConRate("$125 through " + deadline7Days, "$125", false, deadline7Days),
            new DragonConRate("$150 through " + deadline30Days, "$150", false, deadline30Days)
        );

        String result = reminder.build(rates);

        Assertions.assertFalse(result.isEmpty(), "Reminder should not be empty when at least one rate matches threshold");
        Assertions.assertTrue(result.contains("$125"), "Reminder should contain the 7-day threshold price");
        Assertions.assertTrue(result.contains("$150"), "Reminder should contain the 30-day threshold price");
        Assertions.assertFalse(result.contains("$100"), "Reminder should not contain non-threshold price");
    }

    /**
     * Test with multiple rates all matching thresholds (edge case: concatenated message).
     */
    @Test
    void testReminderBuild_MultipleRatesAllMatchThresholds() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate deadline3Days = today.plusDays(3);
        final LocalDate deadline7Days = today.plusDays(7);

        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("$125 through " + deadline7Days, "$125", false, deadline7Days),
            new DragonConRate("$150 through " + deadline3Days, "$150", false, deadline3Days)
        );

        String result = reminder.build(rates);

        Assertions.assertFalse(result.isEmpty(), "Reminder should not be empty");
        Assertions.assertTrue(result.contains("$125"), "Reminder should contain first price");
        Assertions.assertTrue(result.contains("$150"), "Reminder should contain second price");
        Assertions.assertTrue(result.contains("7 day(s)"), "Reminder should mention 7 days");
        Assertions.assertTrue(result.contains("3 day(s)"), "Reminder should mention 3 days");
    }

    /**
     * Test that TBA prices with valid deadlines at thresholds still generate reminders.
     */
    @Test
    void testReminderBuild_TBAPriceWithValidDeadline() {
        final LocalDate today = LocalDate.now(Constants.ZONE);
        final LocalDate deadline = today.plusDays(7);
        final List<DragonConRate> rates = Arrays.asList(
            new DragonConRate("TBA through " + deadline, "TBA", true, deadline)
        );

        String result = reminder.build(rates);

        Assertions.assertFalse(result.isEmpty(), "Reminder should not be empty even for TBA prices");
        Assertions.assertTrue(result.contains("TBA"), "Reminder should contain TBA");
        Assertions.assertTrue(result.contains("7 day(s)"), "Reminder should mention 7 days");
    }
}
