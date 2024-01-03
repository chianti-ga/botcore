package fr.skitou.botcore.commands.classic.admin;

import fr.skitou.botcore.commands.classic.AbstractCommand;
import fr.skitou.botcore.commands.classic.CommandReceivedEvent;
import fr.skitou.botcore.hibernate.Database;
import fr.skitou.botcore.hibernate.entities.MembersBlacklist;
import fr.skitou.botcore.utils.IsSenderAllowed;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ManageCommand extends AbstractCommand {

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }

    @Override
    public @NotNull String getCommand() {
        return "manage";
    }

    @Override
    public @NotNull String getName() {
        return "^manage restrict/unrestrict {user} (duration)";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().size() < 2) {
            event.getChannel().sendMessage("Invalid syntax").queue();
            sendHelp(event.getChannel().asTextChannel());
            return;
        }
        switch (event.getArgs().get(0)) {
            case "restric" -> {
                if (event.getArgs().size() < 3) {
                    event.getChannel().sendMessage("Invalid syntax").queue();
                    sendHelp(event.getChannel().asTextChannel());
                    return;
                }
                if (event.getGuild().getMemberById(event.getArgs().get(1)) == null) {
                    event.getChannel().sendMessage("User doesn't exist");
                    return;
                }
                int duration;

                try {
                    Integer.parseInt(event.getArgs().get(2));
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage("Invalid duration (in hours)").queue();
                    return;
                }

                duration = Integer.parseInt(event.getArgs().get(2));

                if (duration <= 0) {
                    event.getChannel().sendMessage("User doesn't exist");
                    return;
                }

                Set<MembersBlacklist> membersBlacklists = Database.getAll(MembersBlacklist.class).stream()
                        .filter(blacklist -> blacklist.getGuild() == event.getGuild().getIdLong())
                        .collect(Collectors.toSet());

                Map<Long, Integer> map = new HashMap<>();

                map.put(Long.valueOf(event.getArgs().get(1)), duration);

                if (membersBlacklists.isEmpty()) {
                    Database.saveOrUpdate(new MembersBlacklist(event.getGuild().getIdLong(), map));
                } else {
                    membersBlacklists.stream().findFirst().get().getBlacklistedMembers().put(Long.valueOf(event.getArgs().get(1)), duration);
                    Database.saveOrUpdate(membersBlacklists);
                }

                event.getChannel().sendMessage(event.getGuild().getMemberById(event.getArgs().get(1)).getAsMention() + " has been restricted from using the bot for " + duration + "h").queue();
            }
            case "unrestric" -> {

                if (event.getGuild().getMemberById(event.getArgs().get(1)) == null) {
                    event.getChannel().sendMessage("User doesn't exist");
                    return;
                }

                Set<MembersBlacklist> membersBlacklists = Database.getAll(MembersBlacklist.class).stream()
                        .filter(blacklist -> blacklist.getGuild() == event.getGuild().getIdLong())
                        .filter(blacklist -> blacklist.getBlacklistedMembers().containsKey(Long.valueOf(event.getArgs().get(1))))
                        .collect(Collectors.toSet());
                membersBlacklists.forEach(blacklist -> blacklist.getBlacklistedMembers().remove(Long.valueOf(event.getArgs().get(1))));

                Database.saveOrUpdate(membersBlacklists);
                event.getChannel().sendMessage(":white_check_mark:").queue();
            }
            case "list" -> {

            }
        }
    }
}
