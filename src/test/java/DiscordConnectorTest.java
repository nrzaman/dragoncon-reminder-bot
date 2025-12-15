import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import com.dragoncon_reminder.bot.DiscordConnector;

import static org.mockito.Mockito.*;

/**
 * Test suite for DiscordMessageSender.
 * Uses Mockito to mock JDA and Discord API interactions.
 */
@ExtendWith(MockitoExtension.class)
public class DiscordConnectorTest {

    @Mock
    private JDA mockJda;

    @Mock
    private TextChannel mockTextChannel;

    @Mock
    private MessageCreateAction mockMessageAction;

    private final String defaultTestToken = "test-token";
    private final String defaultTestChannelId = "123456789";
    private final String defaultTestMessage = "Test reminder message";

    /**
     * Test sending a message successfully.
     */
    @Test
    void testSendMessage_Success() throws InterruptedException {
        // Setup mock behavior
        when(mockJda.getTextChannelById(defaultTestChannelId)).thenReturn(mockTextChannel);
        when(mockTextChannel.sendMessage(defaultTestMessage)).thenReturn(mockMessageAction);
        doNothing().when(mockMessageAction).queue();

        // Create sender with mocked JDA
        final DiscordConnector sender = new DiscordConnector(defaultTestToken, defaultTestChannelId, mockJda);

        // Send message
        sender.sendMessage(defaultTestMessage);

        // Verify interactions
        verify(mockJda, times(1)).getTextChannelById(defaultTestChannelId);
        verify(mockTextChannel, times(1)).sendMessage(defaultTestMessage);
        verify(mockMessageAction, times(1)).queue();
    }

    /**
     * Test sending multiple messages.
     */
    @Test
    void testSendMessage_MultipleMessages() throws InterruptedException {
        final String message1 = "First message";
        final String message2 = "Second message";

        // Setup mock behavior
        when(mockJda.getTextChannelById(defaultTestChannelId)).thenReturn(mockTextChannel);
        when(mockTextChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
        doNothing().when(mockMessageAction).queue();

        // Create sender with mocked JDA
        final DiscordConnector sender = new DiscordConnector(defaultTestToken, defaultTestChannelId, mockJda);

        // Send multiple messages
        sender.sendMessage(message1);
        sender.sendMessage(message2);

        // Verify interactions
        verify(mockJda, times(2)).getTextChannelById(defaultTestChannelId);
        verify(mockTextChannel, times(1)).sendMessage(message1);
        verify(mockTextChannel, times(1)).sendMessage(message2);
        verify(mockMessageAction, times(2)).queue();
    }

    /**
     * Test sending an empty message (edge case).
     */
    @Test
    void testSendMessage_EmptyMessage() throws InterruptedException {
        final String emptyMessage = "";

        // Setup mock behavior
        when(mockJda.getTextChannelById(defaultTestChannelId)).thenReturn(mockTextChannel);
        when(mockTextChannel.sendMessage(emptyMessage)).thenReturn(mockMessageAction);
        doNothing().when(mockMessageAction).queue();

        // Create sender with mocked JDA
        final DiscordConnector sender = new DiscordConnector(defaultTestToken, defaultTestChannelId, mockJda);

        // Send empty message
        sender.sendMessage(emptyMessage);

        // Verify interactions
        verify(mockJda, times(1)).getTextChannelById(defaultTestChannelId);
        verify(mockTextChannel, times(1)).sendMessage(emptyMessage);
        verify(mockMessageAction, times(1)).queue();
    }

    /**
     * Test sending a message with special characters.
     */
    @Test
    void testSendMessage_SpecialCharacters() throws InterruptedException {
        final String messageWithSpecialChars = "Price: $100 🎉 Deadline: 12/31/2025 @ 11:59 PM";

        // Setup mock behavior
        when(mockJda.getTextChannelById(defaultTestChannelId)).thenReturn(mockTextChannel);
        when(mockTextChannel.sendMessage(messageWithSpecialChars)).thenReturn(mockMessageAction);
        doNothing().when(mockMessageAction).queue();

        // Create sender with mocked JDA
        final DiscordConnector sender = new DiscordConnector(defaultTestToken, defaultTestChannelId, mockJda);

        // Send message
        sender.sendMessage(messageWithSpecialChars);

        // Verify interactions
        verify(mockJda, times(1)).getTextChannelById(defaultTestChannelId);
        verify(mockTextChannel, times(1)).sendMessage(messageWithSpecialChars);
        verify(mockMessageAction, times(1)).queue();
    }

    /**
     * Test sending a long message.
     */
    @Test
    void testSendMessage_LongMessage() throws InterruptedException {
        final String longMessage = "The DragonCon membership price (current: $100) will increase in 30 day(s) (2025-12-31)." +
                                   "The DragonCon membership price (current: $125) will increase in 14 day(s) (2026-01-15).";

        // Setup mock behavior
        when(mockJda.getTextChannelById(defaultTestChannelId)).thenReturn(mockTextChannel);
        when(mockTextChannel.sendMessage(longMessage)).thenReturn(mockMessageAction);
        doNothing().when(mockMessageAction).queue();

        // Create sender with mocked JDA
        final DiscordConnector sender = new DiscordConnector(defaultTestToken, defaultTestChannelId, mockJda);

        // Send message
        sender.sendMessage(longMessage);

        // Verify interactions
        verify(mockJda, times(1)).getTextChannelById(defaultTestChannelId);
        verify(mockTextChannel, times(1)).sendMessage(longMessage);
        verify(mockMessageAction, times(1)).queue();
    }

    /**
     * Test that the correct channel ID is used.
     */
    @Test
    void testSendMessage_CorrectChannelId() throws InterruptedException {
        // Setup mock behavior
        when(mockJda.getTextChannelById(defaultTestChannelId)).thenReturn(mockTextChannel);
        when(mockTextChannel.sendMessage(defaultTestMessage)).thenReturn(mockMessageAction);
        doNothing().when(mockMessageAction).queue();

        // Create sender with specific channel ID
        final DiscordConnector sender = new DiscordConnector(defaultTestToken, defaultTestChannelId, mockJda);

        // Send message
        sender.sendMessage(defaultTestMessage);

        // Verify the correct channel ID was used
        verify(mockJda, times(1)).getTextChannelById(defaultTestChannelId);
        verify(mockJda, never()).getTextChannelById("wrong-channel-id");
    }

    /**
     * Test constructor with null JDA throws appropriate error.
     * Note: This tests the constructor's behavior, not sendMessage.
     */
    @Test
    void testConstructor_WithNullJDA() {
        // This should not throw during construction
        Assertions.assertDoesNotThrow(() -> {
            new DiscordConnector(defaultTestToken, defaultTestChannelId, null);
        });
    }

    /**
     * Test that sendMessage is called with exact message content.
     */
    @Test
    void testSendMessage_ExactMessageContent() throws InterruptedException {
        final String exactMessage = "The DragonCon membership price (current: $100) will increase in 7 day(s) (2025-12-25).";

        // Setup mock behavior
        when(mockJda.getTextChannelById(defaultTestChannelId)).thenReturn(mockTextChannel);
        when(mockTextChannel.sendMessage(exactMessage)).thenReturn(mockMessageAction);
        doNothing().when(mockMessageAction).queue();

        // Create sender
        final DiscordConnector sender = new DiscordConnector(defaultTestToken, defaultTestChannelId, mockJda);

        // Send message
        sender.sendMessage(exactMessage);

        // Verify exact message was sent
        verify(mockTextChannel, times(1)).sendMessage(exactMessage);
        verify(mockTextChannel, never()).sendMessage("wrong message");
    }
}
