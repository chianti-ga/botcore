package fr.skitou.botcore.commands.classic.admin;

import fr.skitou.botcore.commands.classic.AbstractCommand;
import fr.skitou.botcore.commands.classic.CommandReceivedEvent;
import fr.skitou.botcore.utils.IsSenderAllowed;
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
