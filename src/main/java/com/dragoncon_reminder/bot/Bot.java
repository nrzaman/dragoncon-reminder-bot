package com.dragoncon_reminder.bot;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dragoncon_reminder.util.Constants;
import com.dragoncon_reminder.util.DragonConRate;
import com.dragoncon_reminder.util.DragonConRateParser;
import com.dragoncon_reminder.util.Reminder;

/**
 * This is the main Java class that will execute the bot.
 */
public class Bot {
    public static void main(String[] args) throws Exception {
        // Retrieve relevant environment variables to be able to connect to Discord.
        final DiscordConnector discordConnector = new DiscordConnector();

        // Create the task that will check rates and send reminders
        Runnable task = () -> {
            try {
                System.out.println("Running scheduled check at: " + ZonedDateTime.now(Constants.ZONE));

                // Retrieve DragonCon membership rates and build the text reminders.
                final DragonConRateParser dragonConRateParser = new DragonConRateParser();
                final List<DragonConRate> dragonConRates = dragonConRateParser.fetchRatesAndDeadlines();
                final Reminder reminder = new Reminder();
                final String reminderAsString = reminder.build(dragonConRates);

                // Send the reminder to the channel in case a threshold is met (list would be populated if so).
                if (!reminderAsString.isEmpty()) {
                    // Send the message to the channel.
                    discordConnector.sendMessage(reminderAsString);
                    System.out.println("Reminder sent: " + reminderAsString);
                } else {
                    System.out.println("No reminders to send (no deadlines match threshold).");
                }
            } catch (Exception e) {
                System.err.println("Error during scheduled task execution:");
                e.printStackTrace();
            }
        };

        // Calculate the initial delay until the first run date
        final long initialDelayDays = calculateInitialDelay();

        System.out.println("Bot starting...");
        System.out.println("First check scheduled for: " + Constants.FIRST_RUN_DATE);
        System.out.println("Initial delay: " + initialDelayDays + " days");
        System.out.println("Will check every " + Constants.INTERVAL_DAYS + " days after first run");

        // Create scheduled executor service
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Schedule the task to run at fixed rate
        scheduler.scheduleAtFixedRate(
            task,
            initialDelayDays,        // Initial delay in days
            Constants.INTERVAL_DAYS, // Period between runs (90 days)
            TimeUnit.DAYS            // Time unit
        );

        // Keep the application running
        // Add shutdown hook to gracefully shutdown the scheduler
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down bot...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }));

        System.out.println("Bot is now running. Press Ctrl+C to stop.");
    }

    /**
     * Calculate the number of days until the first scheduled run.
     * If the first run date has already passed, calculate when the next run should be
     * based on the 90-day interval.
     *
     * @return the number of days until the next scheduled run
     */
    private static long calculateInitialDelay() {
        final LocalDate today = LocalDate.now(Constants.ZONE);

        // If we haven't reached the first run date yet, calculate days until then
        if (today.isBefore(Constants.FIRST_RUN_DATE)) {
            return ChronoUnit.DAYS.between(today, Constants.FIRST_RUN_DATE);
        }

        // If we've passed the first run date, calculate the next run based on the interval
        final long daysSinceFirstRun = ChronoUnit.DAYS.between(Constants.FIRST_RUN_DATE, today);
        final long missedIntervals = daysSinceFirstRun / Constants.INTERVAL_DAYS;
        final LocalDate nextRunDate = Constants.FIRST_RUN_DATE.plusDays((missedIntervals + 1) * Constants.INTERVAL_DAYS);

        return ChronoUnit.DAYS.between(today, nextRunDate);
    }
}
