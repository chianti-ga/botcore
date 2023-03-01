package fr.skitou.nakbot.commands.admin;

import fr.skitou.nakbot.commands.AbstractCommand;
import fr.skitou.nakbot.commands.CommandReceivedEvent;
import fr.skitou.nakbot.core.BotInstance;
import fr.skitou.nakbot.subsystems.ISubsystem;
import fr.skitou.nakbot.subsystems.SubsystemAdapter;
import fr.skitou.nakbot.utils.IsSenderAllowed;
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
public class SubsystemCommand extends AbstractCommand {
    public final Logger logger = LoggerFactory.getLogger(SubsystemCommand.class);

    @Override
    public @NotNull String getCommand() {
        return "subsystem";
    }

    @Override
    public @NotNull String getName() {
        return "Subsystem";
    }

    @Override
    public @NotNull String getHelp() {
        return prefix + getCommand() + " (list|enable|disable) (subsystem)";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin.and(IsSenderAllowed.Admin, IsSenderAllowed.G_DEV);
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage(getHelp()).queue();
            return;
        }

        switch (event.getArgs().get(0).toLowerCase()) {
            case "list" -> listSubsystemSubCommand(event);
            case "enable" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage(getHelp()).queue();
                    return;
                }
                if (event.getArgs().get(1).equalsIgnoreCase("all")) {
                    SubsystemAdapter.getSubsystems().stream()
                            .peek(iSubsystem -> sendMessage(event, "Enabling " + iSubsystem.getName()))
                            .forEach(ISubsystem::enable);
                    return;
                }
                SubsystemAdapter.getSubsystems().stream()
                        .filter(iSubsystem -> iSubsystem.getName().equalsIgnoreCase(event.getArgs().get(1)))
                        .peek(iSubsystem -> logger.info("Enabling " + iSubsystem.getName()))
                        .forEach(ISubsystem::enable);
                listSubsystemSubCommand(event);
            }
            case "disable" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage(getHelp()).queue();
                    return;
                }
                if (event.getArgs().get(1).equalsIgnoreCase("all")) {
                    SubsystemAdapter.getSubsystems().stream()
                            .peek(iSubsystem -> logger.info("Disabling " + iSubsystem.getName()))
                            .forEach(ISubsystem::disable);
                    listSubsystemSubCommand(event);
                    return;
                }
                SubsystemAdapter.getSubsystems().stream()
                        .filter(iSubsystem -> iSubsystem.getName().equalsIgnoreCase(event.getArgs().get(1)))
                        .peek(iSubsystem -> sendMessage(event, "Disabling " + iSubsystem.getName()))
                        .forEach(ISubsystem::disable);
                listSubsystemSubCommand(event);
            }
            case "reload" ->
                    sendMessage(event, "It would simply be useless, as we are reusing the same instance anyway.");
            default -> sendHelp(event);
        }
    }

    private void listSubsystemSubCommand(CommandReceivedEvent event) {
        StringBuilder jdaBuilder = new StringBuilder();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("JavaBot Subsystems").setDescription("JavaBot subsystem status");
        SubsystemAdapter.getSubsystems().stream().sorted(Comparator.comparing(ISubsystem::getName)).forEach(iSubsystem -> builder.addField((iSubsystem.isEnabled() ? "🟢 " : "🔴 ") + iSubsystem.getName(), iSubsystem.getDescription(), false)
        );
        BotInstance.getJda().getRegisteredListeners().forEach(o -> jdaBuilder.append(o.getClass().getSimpleName()).append("\n"));
        builder.addField("JDA Actual Listeners:", jdaBuilder.toString(), true);

        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
