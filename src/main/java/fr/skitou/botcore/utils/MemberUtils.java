package fr.skitou.botcore.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;


public class MemberUtils {
    private MemberUtils() {
        throw new IllegalStateException("Utility class");
    }

    @NotNull
    public static Member resolveMember(Guild guild, String string) throws NullPointerException {
        string = string.replaceAll("<*@*!*>*", "");
        Member member = guild.retrieveMemberById(string).complete();
        if(member == null) {
            member = guild.retrieveMemberById(string).complete();
        }
        if(member == null) {
            member = guild.retrieveMembersByPrefix(string, 1).get().get(0);
        }
        if(member == null) {
            member = guild.retrieveMemberById(Message.MentionType.USER.getPattern().matcher(string).group()).complete();
        }
        if(member == null) {
            throw new NullPointerException("The member cannot be resolved!");
        }
        return member;
    }
}
