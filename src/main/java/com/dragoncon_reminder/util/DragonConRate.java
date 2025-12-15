package com.dragoncon_reminder.util;

import java.time.LocalDate;

/**
 * DragonConRate is an object that contains all relevant details pertaining the DragonCon price rates and their associated deadlines.
 */
public final class DragonConRate {
    final String rawLine;
    final String price;
    final boolean isTBA;
    final LocalDate deadline;

    /**
     * Constructor for the DragonConRate object.
     * 
     * @param rawLine the raw line parsed from the website.
     * @param price the price parsed out from the raw line.
     * @param isTBA a boolean indicating whether or not a price has yet to be announced.
     * @param deadline the deadline as represented by a LocalDate object.
     */
    DragonConRate(final String rawLine, final String price, final boolean isTBA, final LocalDate deadline) {
        this.rawLine = rawLine;
        this.price = price;
        this.isTBA = isTBA;
        this.deadline = deadline;
    }
}
