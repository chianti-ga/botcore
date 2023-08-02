package fr.skitou.botcore.commands.classic;


import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.core.Config;
import fr.skitou.botcore.utils.Children;
import fr.skitou.botcore.utils.IsSenderAllowed;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Define common methods for commands that are called from Discord.
 * <br/>
 * For most purposes, {@link AbstractCommand} should be used instead.
 *
 * @author U_Bren
 * @see AbstractCommand
 */
@Children(targetPackages = {
        "fr.skitou.botcore.commands.classic",
        "fr.skitou.botcore.subsystems"
})
public interface ICommand {
    String PREFIX = BotInstance.isTestMode() ? "?" : Config.CONFIG.getPropertyOrDefault("bot.prefix");

    /**
     * @return The command, without its prefix.
     */
    @NotNull
    String getCommand();

    /**
     * @return The name of the command.
     */
    @NotNull
    String getName();

    /**
     * @return The displayed help of the command. <br>
     * By default returns {@link ICommand#PREFIX} + {@link ICommand#getCommand()}.
     */
    @NotNull
    default String getHelp() {
        return PREFIX + getCommand();
    }

    /**
     * @return If the command is enabled, true.
     */
    boolean isEnabled();

    /**
     * Sets the state for the command. <br>
     * <code>true</code> to enable and <code>false</code> to disable.
     *
     * @param isEnabled State of the command.
     */
    void setEnabled(boolean isEnabled);

    /**
     * Enables the command. Uses {@link ICommand#setEnabled(boolean) setEnabled(true)}
     */
    default void enable() {
        setEnabled(true);
    }

    /**
     * Disables the command. Uses {@link ICommand#setEnabled(boolean) setEnabled(false)}
     */
    default void disable() {
        setEnabled(false);
    }

    /**
     * The entry point of a command. Is executed when the command is detected by {@link CommandAdapter}.
     *
     * @param event An {@link CommandReceivedEvent event} extending {@link net.dv8tion.jda.api.events.message.MessageReceivedEvent}.
     */
    void onCommandReceived(CommandReceivedEvent event);

    /**
     * A list of other strings which are valid for triggering this command
     */
    @NotNull
    default List<String> getAliases() {
        return Collections.emptyList();
    }

    /**
     * A {@link Predicate} returning {@literal true} if the {@link Member} calling this command is authorised to do so.
     * <br/>
     * If {@literal false}, {@link #onCommandReceived(CommandReceivedEvent)} will not be called.
     *
     * @return {@literal true} if the {@link Member} calling this command is authorised to do so, {@literal false} otherwise.
     */
    default Predicate<Member> isSenderAllowed() {
        return IsSenderAllowed.Default;
    }
}