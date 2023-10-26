package fr.skitou.botcore.utils.reporter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.google.common.base.Splitter;
import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.core.Config;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Logger class for logback passing all errors to Sentry
 *
 * @author uku
 */
public class DiscordLogger extends AppenderBase<ILoggingEvent> {
    private final WebhookClient webhook = WebhookClient.withUrl(Config.CONFIG.getPropertyOrDefault("loggerUrl"));

    /**
     * Logs each message to a discord webhook, with a {@link Level} equal or higher than {@link Level#WARN}.
     *
     * @param event The logging event.
     */
    @Override
    protected void append(ILoggingEvent event) {
        if (!event.getLevel().isGreaterOrEqual(Level.ERROR)) return;
        Splitter.fixedLength(MessageEmbed.TEXT_MAX_LENGTH).split(event.getFormattedMessage()).forEach(s -> {
            WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
                    .setColor(getColor(event.getLevel()))
                    .setTitle(new EmbedTitle(String.format("%s by `%s`", event.getLevel().levelStr, getLoggerName(event)), null))
                    .setDescription(s)
                    .setFooter(new EmbedFooter(String.format("Thread `%s`", event.getThreadName()),
                            BotInstance.getJda().getSelfUser().getEffectiveAvatarUrl()));
            webhook.send(builder.build());
        });
    }

    private int getColor(Level level) {
        switch (level.levelInt) {
            case Level.WARN_INT -> {
                return 0xd67309;
            }
            case Level.ERROR_INT -> {
                return 0xdb3f23;
            }
            default -> {
                return 0xc0c0c0;
            }
        }
    }

    /**
     * Gets the class name, out of the logger name.
     *
     * @param event The logging event.
     * @return The class' name.
     */
    private String getLoggerName(ILoggingEvent event) {
        String[] parts = event.getLoggerName().split("\\.");
        return parts[parts.length - 1];
    }
}
