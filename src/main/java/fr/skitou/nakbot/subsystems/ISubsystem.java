package fr.skitou.nakbot.subsystems;

import fr.skitou.nakbot.commands.ICommand;
import fr.skitou.nakbot.utils.Children;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@Children(targetPackages = "fr.mounak.discord.nakbot.subsystems")
public interface ISubsystem extends EventListener {
    @NotNull
    String getName();

    @NotNull
    String getDescription();

    @NotNull
    default List<ICommand> getDeclaredCommands() {
        return Collections.emptyList();
    }

    /**
     * Enable this subsystem.
     * This is a wrapper for {@link #setEnabled(boolean) setEnabled(true)}.
     *
     * @see #setEnabled(boolean)
     */
    default void enable() {
        setEnabled(true);
    }

    /**
     * Disabled this subsystem.
     * This is a wrapper for {@link #setEnabled(boolean) setEnabled(false)}.
     *
     * @see #setEnabled(boolean)
     */
    default void disable() {
        setEnabled(false);
    }

    boolean isEnabled();

    /**
     * Enable or disable this subsystem. This operation is idempotent.
     * {@link #enable()} and {@link #enable()} are internally using this method.
     *
     * @param enabled The state to which define this subsystem.
     */
    void setEnabled(boolean enabled);
}
