package fr.skitou.botcore.subsystems.internal;

import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.subsystems.AbstractSubsystem;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.jetbrains.annotations.NotNull;

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
        BotInstance.updateGuildCommands();
    }
}
