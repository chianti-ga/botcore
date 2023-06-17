package fr.skitou.botcore.subsystems.test;

import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.subsystems.AbstractSubsystem;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@SuppressWarnings("unused") //Automatically discovered via reflection. See SubsystemAdapter.
public class MPListener extends AbstractSubsystem {
    private final Logger logger = LoggerFactory.getLogger(MPListener.class);

    @Override
    public @NotNull String getName() {
        return this.getClass().getSimpleName();
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Transfer all private messages to the logs.";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (event.getAuthor().getId().equalsIgnoreCase(BotInstance.getJda().getSelfUser().getId()))
                logger.info("Sent dm to " + Objects.requireNonNull(event.getChannel().asPrivateChannel().getUser()).getName() + ": " + event.getMessage().getContentDisplay());
            else
                logger.info("Received dm from " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());
        }
    }
}
