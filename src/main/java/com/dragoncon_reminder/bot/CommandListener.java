package com.dragoncon_reminder.bot;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import com.dragoncon_reminder.util.Constants;
import com.dragoncon_reminder.util.DragonConRate;
import com.dragoncon_reminder.util.DragonConRateParser;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Handles slash commands for the DragonCon Reminder Bot.
 */
public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "list-all-deadlines":
                handleListAllDeadlines(event);
                break;
            case "next-deadline":
                handleNextDeadline(event);
                break;
            default:
                event.reply("Unknown command.").setEphemeral(true).queue();
        }
    }

    /**
     * Handles the /list-all-deadlines command.
     * Fetches all DragonCon rates and displays them with their deadlines.
     * @param event the interaction event to handle.
     */
    private void handleListAllDeadlines(SlashCommandInteractionEvent event) {
        // Defer reply since fetching from website may take time
        event.deferReply().queue();

        try {
            // Fetch rates from the website
            final DragonConRateParser parser = new DragonConRateParser();
            final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();

            if (rates.isEmpty()) {
                event.getHook().sendMessage("No pricing information found on the DragonCon website.").queue();
                return;
            }

            // Build the response message
            final StringBuilder response = new StringBuilder("**DragonCon 5-Day Membership Rates:**\n\n");

            for (final DragonConRate rate : rates) {
                response.append("• **").append(rate.getPrice()).append("**");

                if (rate.getDeadline() != null) {
                    final LocalDate today = LocalDate.now(Constants.ZONE);
                    final long daysUntil = ChronoUnit.DAYS.between(today, rate.getDeadline());

                    response.append(" - Valid through **").append(rate.getDeadline()).append("**");

                    if (daysUntil > 0) {
                        response.append(" (").append(daysUntil).append(" day");
                        if (daysUntil != 1) response.append("s");
                        response.append(" remaining)");
                    } else if (daysUntil == 0) {
                        response.append(" (**Last day!**)");
                    } else {
                        response.append(" (Expired)");
                    }
                } else {
                    response.append(" - No deadline specified");
                }

                response.append("\n");
            }

            event.getHook().sendMessage(response.toString()).queue();

        } catch (Exception e) {
            event.getHook().sendMessage("Error fetching rates from DragonCon website: " + e.getMessage()).queue();
            e.printStackTrace();
        }
    }

    /**
     * Handles the /next-deadline command.
     * Displays the next upcoming deadline for DragonCon membership rates.
     * @param event the interaction event to handle.
     */
    private void handleNextDeadline(SlashCommandInteractionEvent event) {
        // Defer reply since fetching from website may take time
        event.deferReply().queue();

        try {
            // Fetch rates from the website
            final DragonConRateParser parser = new DragonConRateParser();
            final List<DragonConRate> rates = parser.fetchRatesAndDeadlines();

            final LocalDate today = LocalDate.now(Constants.ZONE);

            // Filter rates with future deadlines and find the closest one
            final DragonConRate nextDeadline = rates.stream()
                .filter(rate -> rate.getDeadline() != null)
                .filter(rate -> !rate.getDeadline().isBefore(today))
                .min(Comparator.comparing(DragonConRate::getDeadline))
                .orElse(null);

            if (nextDeadline == null) {
                event.getHook().sendMessage("No upcoming deadlines found. All current rates may have expired.").queue();
                return;
            }

            // Build the response message
            final long daysUntil = ChronoUnit.DAYS.between(today, nextDeadline.getDeadline());
            final StringBuilder response = new StringBuilder("**Next Deadline:**\n\n");

            response.append("• Current rate: **").append(nextDeadline.getPrice()).append("**\n");
            response.append("• Deadline: **").append(nextDeadline.getDeadline()).append("**\n");
            response.append("• Time remaining: **").append(daysUntil).append(" day");
            if (daysUntil != 1) response.append("s");
            response.append("**\n\n");

            if (daysUntil <= 7) {
                response.append("⚠️ **Deadline is approaching soon!**");
            } else if (daysUntil <= 30) {
                response.append("⏰ Deadline is coming up this month.");
            } else {
                response.append("✅ You still have plenty of time.");
            }

            event.getHook().sendMessage(response.toString()).queue();

        } catch (Exception e) {
            event.getHook().sendMessage("Error fetching rates from DragonCon website: " + e.getMessage()).queue();
            e.printStackTrace();
        }
    }
}
