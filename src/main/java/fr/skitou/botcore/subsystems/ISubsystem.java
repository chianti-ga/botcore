package fr.skitou.botcore.subsystems;

import fr.skitou.botcore.commands.classic.ICommand;
import fr.skitou.botcore.utils.Children;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * An interface representing a subsystem in a modular application architecture.
 * Subsystems are components that can be enabled or disabled independently to control their behavior.
 * This interface extends {@link EventListener} to allow handling of events related to the subsystem.
 */
@Children(targetPackages = "fr.skitou.botcore.subsystems.internal")
public interface ISubsystem extends EventListener {

    /**
     * Gets the name of the subsystem.
     *
     * @return The name of the subsystem as a {@link String}.
     */
    @NotNull
    String getName();

    /**
     * Gets the description of the subsystem.
     *
     * @return The description of the subsystem as a {@link String}.
     */
    @NotNull
    String getDescription();

    /**
     * Gets the list of commands declared by this subsystem.
     * By default, it returns an empty list
     *
     * @return A list of {@link ICommand} associated with this subsystem.
     */
    @NotNull
    default List<ICommand> getDeclaredCommands() {
        return Collections.emptyList();
    }

    /**
     * Enables this subsystem.
     * This is a convenient wrapper for {@link #setEnabled(boolean) setEnabled(true)}.
     *
     * @see #setEnabled(boolean)
     */
    default void enable() {
        setEnabled(true);
    }

    /**
     * Disables this subsystem.
     * This is a convenient wrapper for {@link #setEnabled(boolean) setEnabled(false)}.
     *
     * @see #setEnabled(boolean)
     */
    default void disable() {
        setEnabled(false);
    }

    /**
     * Checks whether the subsystem is currently enabled.
     *
     * @return {@code true} if the subsystem is enabled, {@code false} otherwise.
     */
    boolean isEnabled();

    /**
     * Enables or disables this subsystem. This operation is idempotent.
     * The {@link #enable()} and {@link #disable()} methods internally use this method.
     *
     * @param enabled The state to which to set this subsystem.
     */
    void setEnabled(boolean enabled);
}

