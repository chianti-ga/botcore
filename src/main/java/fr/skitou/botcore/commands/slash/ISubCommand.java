package fr.skitou.botcore.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public interface ISubCommand {
    /**
     * @return The name of the command.
     */
    @NotNull
    String getName();

    /**
     * @return The displayed help of the command. <br>
     * By default returns "description".
     */
    @NotNull
    default String getHelp() {
        return "No description";
    }

    void onSubCommandReceived(SlashCommandInteractionEvent event);

    default SubcommandData getSubcommandData() {
        return new SubcommandData(getName(), getHelp());
    }

    String CommandClassName();

    /**
     * see <a href="https://jda.wiki/using-jda/interactions/">Discord JDA</a>
     *
     * @return Return {@link Set} containing all {@link OptionData}.
     */

    default Set<OptionData> getOptionData() {
        return Set.of();
    }
}
