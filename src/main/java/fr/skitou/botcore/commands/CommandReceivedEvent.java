package fr.skitou.botcore.commands;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.LinkedList;


public class CommandReceivedEvent extends MessageReceivedEvent {
    private final String command;
    private final LinkedList<String> args;
    private final MessageReceivedEvent messageReceivedEvent;

    CommandReceivedEvent(MessageReceivedEvent event) {
        super(event.getJDA(), event.getResponseNumber(), event.getMessage());
        String[] rowMessage = event.getMessage().getContentRaw().split(" ");
        this.command = rowMessage[0].substring(1);
        this.args = new LinkedList<>(Arrays.asList(Arrays.copyOfRange(rowMessage, 1, rowMessage.length)));
        this.messageReceivedEvent = event;
    }

    public String getCommand() {
        return command;
    }

    public LinkedList<String> getArgs() {
        return args;
    }

    public MessageReceivedEvent getMessageReceivedEvent() {
        return messageReceivedEvent;
    }
}
