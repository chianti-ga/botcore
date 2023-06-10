package fr.skitou.botcore.subsystems;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSubsystem extends ListenerAdapter implements ISubsystem {
    private final Logger logger = LoggerFactory.getLogger(AbstractSubsystem.class);
    private boolean enabled;
    private boolean isDefaultStateOverridden;

    @Override
    public final boolean isEnabled() {
        if (isDefaultStateOverridden) {
            return enabled;
        }
        return isEnabledByDefault();
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        if (this.isEnabled() == isEnabled) return;
        logger.info(isEnabled ? "Enabled " : "Disabled " + "subsystem " + this.getName() + ".");
        enabled = isEnabled;
        isDefaultStateOverridden = true;
    }

    /**
     * Define the default state for a subsystem.
     *
     * @return {@code false} by default. To modify this value, {@link Override} this method.
     */
    public boolean isEnabledByDefault() {
        return false;
    }
}