package com.dragoncon_reminder.util;

import java.util.List;

import java.time.Duration;
import java.time.LocalDate;

import com.google.common.annotations.VisibleForTesting;

/**
 * This is a helper class that builds out a reminder that will be posted to a Discord channel.
 */
public final class Reminder {
    /**
     * Constructor to instantiate a DragonConRateParser object. 
     * This is needed primarily to be able to mock for testing.
     */
    @VisibleForTesting
    public Reminder() {
        // Intentionally left blank.
    }

    /**
     * Builds a reminder String to be posted to a Discord channel.
     * @param dragonConRates the list of DragonCon rates and deadlines that need to be posted.
     * @return a String that contains the reminder content to be posted.
     */
    public final String build(final List<DragonConRate> dragonConRates) {
        // Grab today's date to check against the parsed deadlines.
        final LocalDate today = LocalDate.now(Constants.ZONE);
        // Constant thresholds measured in days to determine whether today's date is in one of these windows.
        final int[] thresholdsInDays = { Constants.DAYS_IN_MONTH, (Constants.DAYS_IN_WEEK * 2), Constants.DAYS_IN_WEEK, 3, 1, 0 };

        final StringBuffer reminder = new StringBuffer();

        for (final DragonConRate dragonConRate : dragonConRates) {
            if (dragonConRate.deadline == null) {
                continue;
            }

            // Determine the difference between today's date and a given deadline in days.
            final long numDays = Duration.between(today.atStartOfDay(Constants.ZONE), 
            dragonConRate.deadline.atStartOfDay(Constants.ZONE)).toDays();

            // Check the difference (duration) against the pre-definined thresholds in order to determine whether a reminder should be built.
            for (final int threshold : thresholdsInDays) {
                if (threshold == numDays) {
                    reminder.append(String.format("The DragonCon membership price (current: %s) will increase in %d day(s) (%s).",
                        dragonConRate.price,
                        numDays,
                        dragonConRate.deadline
                    ));

                    break;
                }
            }
        }

        return reminder.toString();
    }
}
