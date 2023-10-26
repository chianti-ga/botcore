package fr.skitou.botcore.commands.classic.admin;

import fr.skitou.botcore.commands.classic.AbstractCommand;
import fr.skitou.botcore.commands.classic.CommandAdapter;
import fr.skitou.botcore.commands.classic.CommandReceivedEvent;
import fr.skitou.botcore.commands.classic.ICommand;
import fr.skitou.botcore.utils.IsSenderAllowed;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * Command to manage others commands
 *
 * @author Unknown, reworked by Skitou
 */

@SuppressWarnings("unused") //Automatically discovered via reflection. See CommandAdapter.
public class CommandManager extends AbstractCommand {
    public final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    @Override
    public @NotNull String getCommand() {
        return "command";
    }

    @Override
    public @NotNull String getName() {
        return "CommandManager";
    }

    @Override
    public @NotNull String getHelp() {
        return ICommand.PREFIX + "command (list|enable|disable) (command|all)";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage(getHelp()).queue();
            return;
        }

        switch (event.getArgs().get(0).toLowerCase()) {
            case "list" -> listCommand(event);
            case "enable" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage(getHelp()).queue();
                    return;
                }
                if (event.getArgs().get(1).equalsIgnoreCase("all")) {
                    CommandAdapter.getInstance().getCommands().stream()
                            .filter(iCommand -> !iCommand.getName().equalsIgnoreCase(getName()))
                            .peek(e -> logger.info("Enabling " + e.getName()))
                            .forEach(ICommand::enable);
                    listCommand(event);
                    return;
                }
                CommandAdapter.getInstance().getCommands().stream()
                        .filter(iCommand -> !iCommand.getName().equalsIgnoreCase(getName()))
                        .filter(e -> e.getName().equalsIgnoreCase(event.getArgs().get(1)))
                        .peek(e -> logger.info("Enabling " + e.getName()))
                        .forEach(ICommand::enable);
                listCommand(event);
            }
            case "disable" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage(getHelp()).queue();
                    return;
                }
                if (event.getArgs().get(1).equalsIgnoreCase("all")) {
                    CommandAdapter.getInstance().getCommands().stream()
                            .filter(iCommand -> !iCommand.getName().equalsIgnoreCase(getName()))
                            .peek(e -> logger.info("Disabling " + e.getName()))
                            .forEach(ICommand::disable);
                    listCommand(event);
                    return;
                }
                CommandAdapter.getInstance().getCommands().stream()
                        .filter(iCommand -> !iCommand.getName().equalsIgnoreCase(getName()))
                        .filter(e -> e.getName().equalsIgnoreCase(event.getArgs().get(1)))
                        .peek(e -> logger.info("Disabling " + e.getName()))
                        .forEach(ICommand::disable);
                listCommand(event);
            }
            default -> sendHelp(event);
        }
    }

    private void listCommand(CommandReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Subsystems").setDescription("subsystem status");
        CommandAdapter.getInstance().getCommands().stream().sorted(Comparator.comparing(ICommand::getName)).forEach(iCommand ->
                builder.addField((iCommand.isEnabled() ? "🟢 " : "🔴 ") + iCommand.getName(), iCommand.getHelp(), false)
        );
        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
