package fr.skitou.botcore.slashcommand;

import fr.skitou.botcore.commands.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ISubCommand {
    /**
     * @return The name of the subcommand.
     */
    @NotNull
    String getName();

    /**
     * @return The displayed help of the subcommand. <br>
     * By default returns {@link ICommand#prefix} + {@link ICommand#getCommand()}.
     */
    @NotNull
    String getHelpDescription();

    void onSubCommandReceived(SlashCommandInteractionEvent event);

    default SubcommandData getSubcommandData() {
        return new SubcommandData(getName(), getHelpDescription());
    }

    String CommandClassName();

    default Set<OptionData> getOptionData() {
        return Set.of();
    }
}
