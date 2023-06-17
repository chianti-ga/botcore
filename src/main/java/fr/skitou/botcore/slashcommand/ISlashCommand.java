package fr.skitou.botcore.slashcommand;

import fr.skitou.botcore.commands.ICommand;
import fr.skitou.botcore.utils.Children;
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

    /**
     * see <a href="https://jda.wiki/using-jda/interactions/">Discord JDA</a>
     *
     * @return Return {@link Set} containing all {@link OptionData}.
     */
    default Set<OptionData> getOptionData() {
        return Set.of();
    }

    /**
     * @return Return {@link Set} containing all {@link SubcommandData}.
     */
    @NotNull
    default Set<SubcommandData> getSubcommandDatas() {
        return Set.of();
    }
}
