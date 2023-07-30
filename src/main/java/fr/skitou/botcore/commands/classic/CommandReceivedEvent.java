package fr.skitou.botcore.commands.classic;


import lombok.Getter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.LinkedList;


/**
 * This class represents a custom event that is triggered when a command is received.
 * It extends the {@link MessageReceivedEvent} class and adds additional information about the command.
 */
@Getter
public class CommandReceivedEvent extends MessageReceivedEvent {

    /**
     * Command name
     */
    private final String command;

    /**
     * The arguments for the command extracted from the received message.
     */
    private final LinkedList<String> args;

    /**
     * The original {@link MessageReceivedEvent} that triggered this command event.
     */
    private final MessageReceivedEvent messageReceivedEvent;

    /**
     * Constructs a new {@code CommandReceivedEvent} object.
     *
     * @param event The original {@link MessageReceivedEvent} that triggered this command event.
     */
    CommandReceivedEvent(MessageReceivedEvent event) {
        super(event.getJDA(), event.getResponseNumber(), event.getMessage());

        // Extract the command and arguments from the received message
        String[] rowMessage = event.getMessage().getContentRaw().split(" ");
        this.command = rowMessage[0].substring(1);
        this.args = new LinkedList<>(Arrays.asList(Arrays.copyOfRange(rowMessage, 1, rowMessage.length)));

        this.messageReceivedEvent = event;
    }
}

