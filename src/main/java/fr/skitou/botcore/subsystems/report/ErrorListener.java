package fr.skitou.botcore.subsystems.report;

import io.sentry.Sentry;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ErrorListener.class);

    @Override
    public void onException(@NotNull ExceptionEvent event) {
        logger.error("JDA threw a {}: {}", event.getCause().getClass().getSimpleName(), event.getCause().getMessage());
        Sentry.captureException(event.getCause());
    }
}
