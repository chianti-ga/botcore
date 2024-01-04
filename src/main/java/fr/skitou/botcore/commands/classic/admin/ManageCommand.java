package fr.skitou.botcore.commands.classic.admin;

import fr.skitou.botcore.commands.classic.AbstractCommand;
import fr.skitou.botcore.commands.classic.CommandReceivedEvent;
import fr.skitou.botcore.hibernate.Database;
import fr.skitou.botcore.hibernate.entities.MembersBlacklist;
import fr.skitou.botcore.utils.IsSenderAllowed;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
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
        return "manage";
    }

    @Override
    public @NotNull String getHelp() {
        return "^manage restrict/unrestrict {user}";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().size() < 2) {
            event.getChannel().sendMessage("Invalid syntax").queue();
            sendHelp(event.getChannel().asTextChannel());
            return;
        }
        switch (event.getArgs().get(0)) {
            case "restrict" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage("Invalid syntax").queue();
                    sendHelp(event.getChannel().asTextChannel());
                    return;
                }
                if (event.getGuild().getMemberById(event.getArgs().get(1)) == null) {
                    event.getChannel().sendMessage("User doesn't exist").queue();
                    return;
                }

                Set<MembersBlacklist> membersBlacklists = Database.getAll(MembersBlacklist.class).stream()
                        .filter(blacklist -> blacklist.getGuild() == event.getGuild().getIdLong())
                        .collect(Collectors.toSet());

                if (membersBlacklists.isEmpty()) {
                    new MembersBlacklist(event.getGuild().getIdLong(), Set.of(Long.valueOf(event.getArgs().get(1))));
                } else {
                    Set<Long> blacklistedMembers = new HashSet<>(membersBlacklists.stream().findFirst().get().getBlacklistedMembers());
                    blacklistedMembers.add(Long.valueOf(event.getArgs().get(1)));
                    new MembersBlacklist(event.getGuild().getIdLong(), blacklistedMembers);

                }

                event.getChannel().sendMessage(event.getGuild().getMemberById(event.getArgs().get(1)).getAsMention() + " has been restricted from using the bot").queue();
            }
            case "unrestrict" -> {

                if (event.getGuild().getMemberById(event.getArgs().get(1)) == null) {
                    event.getChannel().sendMessage("User doesn't exist");
                    return;
                }

                Set<MembersBlacklist> membersBlacklists = Database.getAll(MembersBlacklist.class).stream()
                        .filter(blacklist -> blacklist.getGuild() == event.getGuild().getIdLong())
                        .collect(Collectors.toSet());


                Set<Long> blacklistedMembers = new HashSet<>(membersBlacklists.stream().findFirst().get().getBlacklistedMembers());

                blacklistedMembers.remove(Long.valueOf(event.getArgs().get(1)));

                new MembersBlacklist(event.getGuild().getIdLong(), blacklistedMembers);

                event.getChannel().sendMessage(":white_check_mark:").queue();
            }
            case "list" -> {

            }
        }
    }
}
