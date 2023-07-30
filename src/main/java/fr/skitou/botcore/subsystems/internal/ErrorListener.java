package fr.skitou.botcore.subsystems.internal;

import io.sentry.Sentry;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom listener that extends {@link ListenerAdapter} to handle exceptions thrown by JDA (Java Discord API) events.
 * When an exception occurs, it logs the error and captures the exception using Sentry for error tracking.
 */
public class ErrorListener extends ListenerAdapter {

    /**
     * The logger instance used for logging error messages.
     */
    private static final Logger logger = LoggerFactory.getLogger(ErrorListener.class);

    /**
     * This method is called when JDA encounters an uncaught exception in any event.
     * It logs the error with relevant information and captures the exception using Sentry for error tracking.
     *
     * @param event The {@link ExceptionEvent} representing the exception thrown by JDA.
     */
    @Override
    public void onException(@NotNull ExceptionEvent event) {
        logger.error("JDA threw a {}: {}", event.getCause().getClass().getSimpleName(), event.getCause().getMessage());
        Sentry.captureException(event.getCause());
    }
}

