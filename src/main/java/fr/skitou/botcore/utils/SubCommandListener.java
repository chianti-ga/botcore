package fr.skitou.botcore.utils;

import fr.skitou.botcore.core.BotInstance;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SubCommandListener extends ListenerAdapter {
    private final User user;
    private final MessageChannel channel;
    private final Consumer<Message> action;

    public SubCommandListener(User user, MessageChannel channel, Consumer<Message> action) {
        this.user = user;
        this.channel = channel;
        this.action = action;

        BotInstance.getJda().addEventListener(this);
    }

    public SubCommandListener() {
        this.user = null;
        this.channel = null;
        this.action = null;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!event.getChannel().getId().equals(channel.getId())) return;
        if(!event.getAuthor().getId().equals(user.getId())) return;
        action.accept(event.getMessage());
        BotInstance.getJda().removeEventListener(this);

    }
}
