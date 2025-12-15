package com.dragoncon_reminder.util;

import java.util.List;
import java.util.ArrayList;

import java.time.Duration;
import java.time.LocalDate;

/**
 * This is a helper class that builds out a reminder that will be posted to a Discord channel.
 */
public final class Reminder {
    /**
     * Builds reminders to be posted to a Discord channel.
     * @param dragonConRates the list of DragonCon rates and deadlines that need to be posted.
     * @return
     */
    public static final String build(final List<DragonConRate> dragonConRates) {
        // Grab today's date to check against the parsed deadlines.
        final LocalDate today = LocalDate.now(Constants.ZONE);
        // Constant thresholds measured in days to determine whether today's date is in one of these windows.
        final int[] thresholdsInDays = { 30, 14, 7, 3, 1, 0 };

        final StringBuffer reminder = new StringBuffer();

        for (final DragonConRate dragonConRate : dragonConRates) {
            if (dragonConRate.deadline == null) {
                continue;
            }

            // Determine the difference between today's date and a given deadline in days.
            final long days = Duration.between(today.atStartOfDay(Constants.ZONE), 
            dragonConRate.deadline.atStartOfDay(Constants.ZONE)).toDays();

            // Check the difference (duration) against the pre-definined thresholds in order to determine whether a reminder should be built.
            for (final int threshold : thresholdsInDays) {
                if (threshold == days) {
                    reminder.append(String.format("The DragonCon membership price (current: %s) will increase in %d day(s) (%s).",
                        dragonConRate.price,
                        days,
                        dragonConRate.deadline
                    ));

                    break;
                }
            }
        }

        return reminder.toString();
    }
}
