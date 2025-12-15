package com.dragoncon_reminder.bot;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.dragoncon_reminder.util.DragonConRate;
import com.dragoncon_reminder.util.RateDeadlineParser;
import com.dragoncon_reminder.util.Reminder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/**
 * This is the main Java class that will execute the bot.
 */
public class Bot {
    public static void main(String[] args) throws Exception {
        // Retrieve relevant environment variables to be able to connect to Discord.
        final String discordToken = getEnvValue("DISCORD_TOKEN");
        final String discordChannelId = getEnvValue("DISCORD_CHANNEL_ID");

        // Connect to the Discord server.
        JDA jda = JDABuilder.createDefault(discordToken)
        .build()
        .awaitReady();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                // Retrieve DragonCon membership rates and build the text reminders.
                final List<DragonConRate> dragonConRates = RateDeadlineParser.fetchRatesAndDeadlines();
                final String reminder = Reminder.build(dragonConRates);

                // Send the reminder to the channel in case a threshold is met (list would be populated if so).
                if (!reminder.isEmpty()) {
                    // Send the message to the channel.
                    jda.getTextChannelById(discordChannelId).sendMessage(reminder).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        task.run();
    }

    /**
     * Returns the value of an environment variable, given the key.
     * @param envKey the environment key represented as a String.
     * @return the environment value paired with the provided environment key.
     */
    private static String getEnvValue(final String envKey) {
        // Retrieve the environment variable value.
        final String envValue = System.getenv(envKey);

        // Error in case no value is found.
        if (envValue == null || envValue.isBlank()) {
            throw new IllegalArgumentException("The following environment variable was missing: " + envKey);
        }

        return envValue.trim();
    }
}
