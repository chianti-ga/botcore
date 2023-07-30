package fr.skitou.botcore.subsystems;

import fr.skitou.botcore.commands.CommandAdapter;
import fr.skitou.botcore.slashcommand.ISlashCommand;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SlashCommandUpdater extends AbstractSubsystem {
    @Override
    public @NotNull String getName() {
        return getClass().getName();
    }

    @Override
    public @NotNull String getDescription() {
        return "Update guild slash commands when bot join.";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        HashSet<ISlashCommand> slashCommands = CommandAdapter.getInstance().getSlashcommands();
        Set<SlashCommandData> a = slashCommands.stream()
                .map(iSlashCommand ->
                        Commands.slash(iSlashCommand.getName().toLowerCase(), iSlashCommand.getHelp())
                                .addOptions(iSlashCommand.getOptionData())
                                .addSubcommands(iSlashCommand.getSubcommandDatas())).collect(Collectors.toSet());
        event.getGuild().updateCommands().addCommands(a).queue();
    }
}
