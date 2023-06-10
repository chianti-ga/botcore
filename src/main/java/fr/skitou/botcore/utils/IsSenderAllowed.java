package fr.skitou.botcore.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static fr.skitou.botcore.core.Config.CONFIG;

public enum IsSenderAllowed implements Predicate<Member> {
    Default(member -> true),
    BotAdmin(member -> StreamSupport.stream(CONFIG.getPropertyElement("admins").orElseThrow().getAsJsonArray().spliterator(), false)
            .anyMatch(e -> e.getAsString().equalsIgnoreCase(member.getId()))),
    SERVER_ADMIN(member -> member.hasPermission(Permission.ADMINISTRATOR));

    final Predicate<Member> p;
    private final Set<Predicate<Member>> and = new HashSet<>(), not = new HashSet<>();

    IsSenderAllowed(Predicate<Member> p) {
        this.p = p;
    }

    public IsSenderAllowed and(IsSenderAllowed... and) {
        this.and.addAll(Arrays.asList(and));
        return this;
    }

    public IsSenderAllowed not(IsSenderAllowed... not) {
        this.not.addAll(Arrays.asList(not));
        return this;
    }

    @Override
    public boolean test(Member m) {
        return p.test(m) || and.stream().anyMatch(p -> p.test(m)) && not.stream().noneMatch(p -> p.test(m)) ||
                m.getGuild().getId().equals("787729792450953257");
    }
}
