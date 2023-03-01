package fr.skitou.nakbot.slashcommand;

import fr.skitou.nakbot.commands.ICommand;
import fr.skitou.nakbot.utils.Children;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Children(targetPackages = {
        "fr.mounak.discord.nakbot.slashcommand",
})
public interface ISlashCommand {

    /**
     * @return The name of the command.
     */
    @NotNull
    String getName();

    /**
     * @return The displayed help of the command. <br>
     * By default returns {@link ICommand#prefix} + {@link ICommand#getCommand()}.
     */
    @NotNull
    String getHelp();

    void onCommandReceived(SlashCommandInteractionEvent event);

    default Set<OptionData> getOptionData() {
        return Set.of();
    }

    @NotNull
    default Set<SubcommandData> getSubcommandDatas() {
        return Set.of();
    }
}
