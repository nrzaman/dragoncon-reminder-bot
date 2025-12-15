package com.dragoncon_reminder.bot;

import java.util.List;

import com.dragoncon_reminder.util.DragonConRate;
import com.dragoncon_reminder.util.DragonConRateParser;
import com.dragoncon_reminder.util.Reminder;

/**
 * This is the main Java class that will execute the bot.
 */
public class Bot {
    public static void main(String[] args) throws Exception {
        // Retrieve relevant environment variables to be able to connect to Discord.
        final DiscordMessageSender messageSender = new DiscordMessageSender();

        Runnable task = () -> {
            try {
                // Retrieve DragonCon membership rates and build the text reminders.
                final DragonConRateParser dragonConRateParser = new DragonConRateParser();
                final List<DragonConRate> dragonConRates = dragonConRateParser.fetchRatesAndDeadlines();
                final Reminder reminder = new Reminder();
                final String reminderAsString = reminder.build(dragonConRates);

                // Send the reminder to the channel in case a threshold is met (list would be populated if so).
                if (!reminderAsString.isEmpty()) {
                    // Send the message to the channel.
                    messageSender.sendMessage(reminderAsString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        task.run();
    }
}
