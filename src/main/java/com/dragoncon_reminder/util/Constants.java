package com.dragoncon_reminder.util;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * This is a static class that contains universal constants that are used by this Discord bot.
 */
public final class Constants {
    public static final String MEMBERSHIP_URL = "https://dragoncon.org/about/membership-info/";
    public static final ZoneId ZONE = ZoneId.of("America/New_York");
    public static final String SECTION_HEADING = "Dragon Con 5-Day Membership Rates";
    
    // Schedule configuration: First run on March 1, 2026, then every 3 months
    public static final LocalDate FIRST_RUN_DATE = LocalDate.of(2026, 3, 1);
    public static final long INTERVAL_DAYS = 90; // 3 months = ~90 days

    public static final String DISCORD_TOKEN_KEY = "DISCORD_TOKEN";
    public static final String DISCORD_CHANNEL_ID_KEY = "DISCORD_CHANNEL_ID";

    public static final String LIST_ALL_DEADLINES_COMMAND = "list-all-deadlines";
    public static final String NEXT_DEADLINE_COMMAND = "next-deadline";
}
