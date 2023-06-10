package fr.skitou.botcore.slashcommand;

import fr.skitou.botcore.utils.Children;
import fr.skitou.botcore.utils.ReflectionUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Children(targetPackages = "fr.mounak.discord.nakbot.slashcommand")
public abstract class AbstractSlashCommand implements ISlashCommand {

    public final Set<ISubCommand> subCommands = ReflectionUtils.getSubClasses(ISubCommand.class, this.getClass()).stream()
            .map(ReflectionUtils::getInstanceFromClass)
            .collect(Collectors.toSet());
    private final Logger logger = LoggerFactory.getLogger(AbstractSlashCommand.class);

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

    @Override
    public @NotNull Set<SubcommandData> getSubcommandDatas() {
        return subCommands.stream().filter(iSubCommand -> iSubCommand.CommandClassName().equalsIgnoreCase(this.getClass().getName())).map(iSubCommand -> new SubcommandData(iSubCommand.getName(), iSubCommand.getHelpDescription()).addOptions(iSubCommand.getOptionData())).collect(Collectors.toSet());
    }
}
