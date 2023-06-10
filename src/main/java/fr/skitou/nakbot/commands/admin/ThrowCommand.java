package fr.skitou.nakbot.commands.admin;

import fr.skitou.nakbot.commands.AbstractCommand;
import fr.skitou.nakbot.commands.CommandReceivedEvent;
import fr.skitou.nakbot.utils.IsSenderAllowed;
import org.jetbrains.annotations.NotNull;

/**
 * Throw an error
 *
 * @author Uku
 */

@SuppressWarnings("unused") //Automatically discovered via reflection. See CommandAdapter.
public class ThrowCommand extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "throw";
    }

    @Override
    public @NotNull String getName() {
        return "ThrowCommand";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        throw new NullPointerException();
    }

}
