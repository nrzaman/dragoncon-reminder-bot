package com.dragoncon_reminder.bot;

import com.dragoncon_reminder.util.Constants;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * This class handles the connection to Discord and sending messages.
 */
public class DiscordConnector {
    private final String discordToken;
    private final String discordChannelId;
    private final JDA jda;

    /**
     * Default constructor leveraging environment variables to get the Discord token and channel ID.
     * Sets up JDA with slash command support.
     * @throws InterruptedException if there is an error connecting to the Discord server.
     */
    public DiscordConnector() throws InterruptedException {
        this.discordToken = getEnvValue(Constants.DISCORD_TOKEN_KEY);
        this.discordChannelId = getEnvValue(Constants.DISCORD_CHANNEL_ID_KEY);

        // Build JDA with necessary intents and command listener
        this.jda = configureJDA();

        // Register slash commands
        this.jda.updateCommands().addCommands(
            Commands.slash(Constants.LIST_ALL_DEADLINES_COMMAND, "Display all DragonCon membership rate deadlines"),
            Commands.slash(Constants.NEXT_DEADLINE_COMMAND, "Display the next upcoming deadline")
        ).queue();

        System.out.println("Slash commands registered: /list-all-deadlines and /next-deadline");
    }

    /**
     * Custom constructor to set the Discord token and channel ID.
     * Sets up JDA with slash command support.
     * @param discordToken the Discord token to connect to the server.
     * @param discordChannelId the Channel ID of the Discord channel.
     * @throws InterruptedException if there is an error connecting to the Discord server.
     */
    public DiscordConnector(final String discordToken, final String discordChannelId) throws InterruptedException {
        this.discordToken = discordToken;
        this.discordChannelId = discordChannelId;

        // Build JDA with necessary intents and command listener
        this.jda = configureJDA();

        // Register slash commands
        this.jda.updateCommands().addCommands(
            Commands.slash(Constants.LIST_ALL_DEADLINES_COMMAND, "Display all DragonCon membership rate deadlines"),
            Commands.slash(Constants.NEXT_DEADLINE_COMMAND, "Display the next upcoming deadline")
        ).queue();

        System.out.println("Slash commands registered: /list-all-deadlines and /next-deadline");
    }

    /**
     * Custom constructor to set the Discord token and channel ID.
     * @param discordToken the Discord token to connect to the server.
     * @param discordChannelId the Channel ID of the Discord channel.
     * @param jda the JDA used to connect to the Discord server.
     * @throws InterruptedException if there is an error connecting to the Discord server.
     */
    public DiscordConnector(final String discordToken, final String discordChannelId, final JDA jda) throws InterruptedException {
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

    /**
     * Returns a properly configured JDA to connect to Discord and listen for commands.
     * @return a properly configured JDA.
     * @throws InterruptedException in case there is an issue connecting to Discord.
     */
    private final JDA configureJDA() throws InterruptedException {
        return JDABuilder.createDefault(this.discordToken)
            .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(new CommandListener())
            .build()
            .awaitReady();
    }
}
