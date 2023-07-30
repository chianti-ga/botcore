package fr.skitou.botcore.slashcommand;

import fr.skitou.botcore.utils.ReflectionUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An abstract implementation of the {@link ISlashCommand} interface that provides common functionality for handling slash commands and subcommands.
 * Subclasses must implement the {@link #onCommandReceived(SlashCommandInteractionEvent)} and {@link #getSubcommandDatas()} methods.
 *
 * @author Skitou
 */
public abstract class AbstractSlashCommand implements ISlashCommand {

    /**
     * A set containing instances of all available subcommands for this slash command.
     */
    public final Set<ISubCommand> subCommands = ReflectionUtils.getSubClasses(ISubCommand.class, this.getClass()).stream()
            .map(ReflectionUtils::getInstanceFromClass)
            .collect(Collectors.toSet());

    private final Logger logger = LoggerFactory.getLogger(AbstractSlashCommand.class);

    /**
     * Handles the incoming slash command event and delegates it to the appropriate subcommand, if available.
     *
     * @param event The {@link SlashCommandInteractionEvent} representing the received slash command.
     */
    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        Optional<ISubCommand> subCommand = subCommands.stream().filter(subcommand -> subcommand.getName().equalsIgnoreCase(event.getSubcommandName())).findFirst();
        subCommand.ifPresentOrElse(
                subcommand -> subcommand.onSubCommandReceived(event),
                () -> {
                    event.reply("Internal error, subcommand not found.").queue();
                    logger.warn("Error, cannot retrieve subcommand for command : \nFor command" + event.getCommandString() + "\nFor subcommand:" + event.getSubcommandName());
                });
    }

    /**
     * Retrieves SubcommandData of the slash command as a Set
     *
     * @return A Set of {@link SubcommandData} containing the name and description of each subcommand, along with their option data.
     */
    @Override
    public @NotNull Set<SubcommandData> getSubcommandDatas() {
        return subCommands.stream()
                .filter(iSubCommand -> iSubCommand.CommandClassName().equalsIgnoreCase(this.getClass().getName()))
                .map(iSubCommand -> new SubcommandData(iSubCommand.getName(), iSubCommand.getHelp()).addOptions(iSubCommand.getOptionData()))
                .collect(Collectors.toSet());
    }
}
