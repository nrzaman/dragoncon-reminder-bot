package com.dragoncon_reminder.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/**
 * This class handles the connection to Discord and sending messages.
 */
public class DiscordMessageSender {
    private final String discordToken;
    private final String discordChannelId;
    private final JDA jda;
 
    /**
     * Default constructor leveraging environment variables to get the Discord token and channel ID.
     * @throws InterruptedException if there is an error connecting to the Discord server.
     */
    public DiscordMessageSender() throws InterruptedException {
        this.discordToken = getEnvValue("DISCORD_TOKEN");
        this.discordChannelId = getEnvValue("DISCORD_CHANNEL_ID");
        // Use the Discord token to connect to the server.
        this.jda = JDABuilder.createDefault(discordToken).build().awaitReady();
    }

    /**
     * Custom constructor to set the Discord token and channel ID.
     * @param discordToken the Discord token to connect to the server.
     * @param discordChannelId the Channel ID of the Discord channel.
     * @throws InterruptedException if there is an error connecting to the Discord server.
     */
    public DiscordMessageSender(final String discordToken, final String discordChannelId) throws InterruptedException {
        this.discordToken = discordToken;
        this.discordChannelId = discordChannelId;
        // Use the Discord token to connect to the server.
        this.jda = JDABuilder.createDefault(discordToken).build().awaitReady();
    }

    /**
     * Custom constructor to set the Discord token and channel ID.
     * @param discordToken the Discord token to connect to the server.
     * @param discordChannelId the Channel ID of the Discord channel.
     * @param jda the JDA used to connect to the Discord server.
     * @throws InterruptedException if there is an error connecting to the Discord server.
     */
    public DiscordMessageSender(final String discordToken, final String discordChannelId, final JDA jda) throws InterruptedException {
        this.discordToken = discordToken;
        this.discordChannelId = discordChannelId;
        this.jda = jda;
    }

    /**
     * Sends a message to the Discord channel.
     * @param message the message to be sent to the Discord channel.
     */
    public void sendMessage(final String message) {
        // Send the message to the channel.
        jda.getTextChannelById(discordChannelId).sendMessage(message).queue();
    }
    
    /**
     * Returns the value of an environment variable, given the key.
     * @param envKey the environment key represented as a String.
     * @return the environment value paired with the provided environment key.
     */
    private final String getEnvValue(final String envKey) {
        // Retrieve the environment variable value.
        final String envValue = System.getenv(envKey);

        // Error in case no value is found.
        if (envValue == null || envValue.isBlank()) {
            throw new IllegalArgumentException("The following environment variable was missing: " + envKey);
        }

        return envValue.trim();
    }
}
