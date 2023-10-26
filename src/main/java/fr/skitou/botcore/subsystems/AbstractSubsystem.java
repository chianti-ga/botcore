package fr.skitou.botcore.subsystems;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract base class for implementing subsystems in a modular application architecture.
 * Subsystems are components that can be enabled or disabled independently to control their behavior.
 * This class extends {@link ListenerAdapter} to allow handling of Discord events, and it implements {@link ISubsystem} to define the subsystem behavior.
 */
public abstract class AbstractSubsystem extends ListenerAdapter implements ISubsystem {

    /**
     * The logger instance used for logging messages related to this subsystem.
     */
    private final Logger logger = LoggerFactory.getLogger(AbstractSubsystem.class);

    /**
     * A flag indicating whether the subsystem is currently enabled or disabled.
     */
    private boolean enabled;

    /**
     * A flag indicating whether the default state for enabling/disabling the subsystem has been overridden.
     */
    private boolean isDefaultStateOverridden;

    /**
     * Checks whether the subsystem is currently enabled.
     * If the default state has been overridden, the value set using {@link #setEnabled(boolean)} is returned.
     * Otherwise, the result is determined by the {@link #isEnabledByDefault()} method.
     *
     * @return {@code true} if the subsystem is enabled, {@code false} otherwise.
     */
    @Override
    public final boolean isEnabled() {
        if (isDefaultStateOverridden) {
            return enabled;
        }
        return isEnabledByDefault();
    }

    /**
     * Sets the enabled state of the subsystem. If the new state is the same as the current state, no action is taken.
     * When the enabled state is changed, an informational log message is recorded.
     *
     * @param isEnabled {@code true} to enable the subsystem, {@code false} to disable it.
     */
    @Override
    public void setEnabled(boolean isEnabled) {
        if (this.isEnabled() == isEnabled) {
            return;
        }
        logger.info((isEnabled ? "Enabled " : "Disabled ") + "subsystem " + this.getName() + ".");
        enabled = isEnabled;
        isDefaultStateOverridden = true;
    }

    /**
     * Define the default state for the subsystem.
     *
     * @return {@code false} by default. To modify this value, {@link Override} this method.
     */
    public boolean isEnabledByDefault() {
        return false;
    }
}
