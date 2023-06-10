package fr.skitou.nakbot.core;

import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class DebugListener extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(DebugListener.class);

    @Override
    public void onGenericGuild(@Nonnull GenericGuildEvent event) {
        logger.debug("Received a GenericGuildEvent from " + event.getGuild().getName() + ".");
    }
}
