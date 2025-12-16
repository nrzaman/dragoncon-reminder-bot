package com.dragoncon_reminder.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.time.LocalDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * This is a utility class that parses relevant DragonCon membership rate information to determine when to send reminders.
 */
public final class DragonConRateParser {
    private final Document dragonConMembershipSite;
    /**
     * Constructor to instantiate a DragonConRateParser object.
     */
    public DragonConRateParser() throws IOException {
        this.dragonConMembershipSite = Jsoup.connect(Constants.MEMBERSHIP_URL)
            .userAgent("Mozilla/5.0 (compatible; ReminderBot/1.0)")
            .timeout(15000)
            .get();
    }

    /**
     * Custom constructor the set the website by default.
     * @param dragonConMembershipSite the DragonCon membership site to read from.
     */
    public DragonConRateParser(final Document dragonConMembershipSite) {
        this.dragonConMembershipSite = dragonConMembershipSite;
    }
    
    /**
     * Retrieves a list of DragonCon membership rates and deadlines.
     * @return a list of DragonCon membership rates and deadlines.
     * @throws Exception in case there is an error while trying to connect to the website, parse the website, and/or grab content.
     */
    public final List<DragonConRate> fetchRatesAndDeadlines() throws Exception {
        // Grab the relevant data from the website based on the header.
        final String rawData = getRelevantTextBlock(this.dragonConMembershipSite);
        final List<DragonConRate> dragonConRates = new ArrayList<>();

        // Parse each line from the relevant data and store it.
        for (final String line : rawData.split("\n")) {
            final String newLine = line.trim();
            if (newLine.isEmpty()) {
                continue;
            }
            dragonConRates.add(parseDragonConRate(newLine));
        }

        return dragonConRates;
    }

    /**
     * This helper method parses a website and only retrieves the relevant text block pertaining to DragonCon membership rates and deadlines.
     * @param doc the website as represented by a Document object.
     * @return a String representing the relevant text block containing rates and deadlines.
     * @throws IllegalStateException in case there are any issues when parsing the text block(s).
     */
    private final String getRelevantTextBlock(final Document doc) throws IllegalStateException {
        // Find the relevant heading to grab the data.
        Element heading = null;
        for (Element h : doc.select("h1,h2,h3,h4,h5,h6")) {
            String headingText = h.text().trim();

            if (headingText.equalsIgnoreCase(Constants.SECTION_HEADING)) {
                heading = h;
                break;
            }
        }

        if (heading == null) {
            throw new IllegalStateException("There was an error trying to grab the following section heading: " + Constants.SECTION_HEADING);
        }

        // Grab and parse the text block following the heading.
        final Element block = heading.nextElementSibling();
        if (block == null) {
            throw new IllegalStateException("There was an error trying to grab the content after the heading: " + Constants.SECTION_HEADING);
        }

        // Use a more direct approach: get the text content while preserving line breaks
        // Create a temporary document with the block's HTML
        final Document tempDoc = Jsoup.parse(block.html());

        // Replace all <br> tags with a unique marker before getting text
        tempDoc.select("br").before("|||NEWLINE|||");

        // Get the text content
        String text = tempDoc.text();

        // Replace the marker with actual newlines
        text = text.replace("|||NEWLINE|||", "\n");

        return text;
    }

    /**
     * This helper method parses an individual line on the DragonCon membership page in order to store the rate and deadline in a local object.
     * @param line an individual line on the DragonCon page.
     * @return The individual line as represented by a DragonConRate object.
     */
    private final DragonConRate parseDragonConRate(final String line) {
        // Parse the line, using the text "through" as a delimiter.
        final String lowercaseLine = line.toLowerCase(Locale.ROOT);
        final String price = line.split("\\s+through\\s+|\\s+Through\\s+")[0].trim();
        final int indexOfThrough = lowercaseLine.indexOf("through");

        // Parse out and store the deadline information from the line.
        String deadlineAsString = null;
        if (indexOfThrough >= 0) {
            deadlineAsString = line.substring(indexOfThrough + "through".length()).trim();
        }

        // Check and store whether the price is "TBA".
        final boolean isTBA = price.equalsIgnoreCase("tba");

        // Store the deadline into a LocalDate object based on the String value that was parsed out earlier.
        LocalDate deadline = null;
        if (deadlineAsString != null) {
            final String deadlineAsStringTrimmed = deadlineAsString.replaceAll("[\\.;]$", "").trim();

            if (deadlineAsStringTrimmed.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                final DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("M/d/yyyy");
                deadline = LocalDate.parse(deadlineAsStringTrimmed, formattedDate);
            }
        }

        return new DragonConRate(line, price, isTBA, deadline);
    }
}
