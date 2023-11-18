package fr.skitou.botcore.commands.slash;

import fr.skitou.botcore.hibernate.Database;
import fr.skitou.botcore.hibernate.entities.MembersBlacklist;
import fr.skitou.botcore.utils.IsSenderAllowed;
import fr.skitou.botcore.utils.QuickColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Manage implements ISlashCommand {

    @Override
    public @NotNull Set<SubcommandData> getSubcommandDatas() {
        return Set.of(new SubcommandData("list", "(botadmin) List all restricted members on this server"),
                new SubcommandData("restric", "(botadmin) Restric someone from using the bot."));
    }

    @Override
    public @NotNull String getName() {
        return "manage";
    }

    @Override
    public @NotNull String getHelp() {
        return "(botadmin) Restric or unrestrict someone";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {

    }
}

class Restric implements ISubCommand{

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.USER, "user", "user to restrict", true),
                new OptionData(OptionType.INTEGER, "duration", "duration in seconds", true));
    }

    @Override
    public @NotNull String getName() {
        return "restric";
    }

    @Override
    public @NotNull String getHelp() {
        return "";
    }

    @Override
    public void onSubCommandReceived(SlashCommandInteractionEvent event) {
        if (!IsSenderAllowed.SERVER_ADMIN.test(event.getMember()) || !IsSenderAllowed.BotAdmin.test(event.getMember())) {
            event.reply("You don't have the permission to execute this command.").queue();
            return;
        }
        int duration = event.getOption("duration").getAsInt();

        if (duration <= 0) {
            event.reply("Invalid duration").queue();
            return;
        }

        if (IsSenderAllowed.BotAdmin.test(event.getMember())) {
            event.reply("Restricted for " + duration + "s").queue();
        }

        Set<MembersBlacklist> membersBlacklists = Database.getAll(MembersBlacklist.class).stream()
                .filter(blacklist -> blacklist.getGuild() == event.getGuild().getIdLong())
                .collect(Collectors.toSet());

        Map<Long, Integer> map = new HashMap<>();

        map.put(event.getOption("user").getAsMember().getIdLong(), duration);

        if (membersBlacklists.isEmpty()) {
            new MembersBlacklist(event.getGuild().getIdLong(), map);
        }
    }

    @Override
    public String CommandClassName() {
        return "manage";
    }
}

class List implements ISubCommand {
    @Override
    public @NotNull String getName() {
        return "list";
    }

    @Override
    public @NotNull String getHelp() {
        return "";
    }

    @Override
    public void onSubCommandReceived(SlashCommandInteractionEvent event) {
        Set<MembersBlacklist> membersBlacklists = Database.getAll(MembersBlacklist.class).stream()
                .filter(blacklist -> blacklist.getGuild() == event.getGuild().getIdLong())
                .collect(Collectors.toSet());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("```\n");

        membersBlacklists.forEach(blacklist -> {
            blacklist.getBlacklistedMembers().forEach((aLong, integer) -> {
                stringBuilder.append(event.getJDA().getUserById(aLong).getName()).append(" : ").append(integer).append(" seconds of restriction").append("\n");
            });
        });
        stringBuilder.append("```\n");
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Restricted member on: " + event.getGuild().getName())
                .setColor(QuickColors.LIGHT_BLUE)
                .setDescription(stringBuilder.toString());
        event.replyEmbeds(builder.build()).queue();
    }

    @Override
    public String CommandClassName() {
        return "manage";
    }
}
