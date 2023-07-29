package fr.skitou.botcore.commands.admin;

import fr.skitou.botcore.commands.AbstractCommand;
import fr.skitou.botcore.commands.CommandReceivedEvent;
import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.utils.IsSenderAllowed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Stop JDA first then the JVM
 *
 * @author Unknown
 */

@SuppressWarnings("unused") //Automatically discovered via reflection. See CommandAdapter.
public class StopCommand extends AbstractCommand {

    @Override
    public @NotNull String getCommand() {
        return "stop";
    }

    @Override
    public @NotNull String getName() {
        return "stop";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if(event.getArgs().size() == 1 && event.getArgs().get(0).equalsIgnoreCase("now")) {
            stop(event.getChannel().asTextChannel());
            Runtime.getRuntime().exit(0);
        }
        stop(event.getGuildChannel().asTextChannel());
    }

    public static void stop(TextChannel c) {
        c.sendMessage("Stopping the bot...").queue();
        BotInstance.getJda().shutdownNow();
        Runtime.getRuntime().exit(0);
    }
}
