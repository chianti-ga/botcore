package fr.skitou.botcore.commands.admin;

import fr.skitou.botcore.commands.AbstractCommand;
import fr.skitou.botcore.commands.CommandReceivedEvent;
import fr.skitou.botcore.utils.QuickColors;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Simple ping command
 *
 * @author Skitou
 */

@SuppressWarnings("unused") //Automatically discovered via reflection. See CommandAdapter.
public class PingCommand extends AbstractCommand {
    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        long startTime = System.nanoTime();

        EmbedBuilder builder = new EmbedBuilder()
                .addField("WS:", event.getJDA().getGatewayPing() + "ms", false)
                .setColor(QuickColors.MOUNAK_HIGHLIGHT_DARKER);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;

        event.getChannel().sendMessageEmbeds(builder.addField("Typing:", duration + "ms", false).build()).queue();

    }

    @Override
    public @NotNull String getCommand() {
        return "ping";
    }

    @Override
    public @NotNull String getName() {
        return "ping";
    }
}
