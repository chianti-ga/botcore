package fr.skitou.botcore.utils;

import fr.skitou.botcore.core.BotInstance;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

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

    Admin(member -> member.getRoles().contains(Roles.Admin.get())),
    Gerant(member -> member.getRoles().contains(Roles.Gerant.get())),
    GM(member -> member.getRoles().contains(Roles.GM.get())),
    G_CS(member -> member.getRoles().contains(Roles.G_CS.get())),
    G_DEV(member -> member.getRoles().contains(Roles.G_DEV.get())),
    G_CM(member -> member.getRoles().contains(Roles.G_CM.get())),

    Moderation(member -> member.getRoles().contains(Roles.Moderation.get())),
    SM(member -> member.getRoles().contains(Roles.SM.get())),
    M_C(member -> member.getRoles().contains(Roles.M_C.get())),
    M(member -> member.getRoles().contains(Roles.M.get())),
    MT(member -> member.getRoles().contains(Roles.MT.get())),

    ModerationCS(member -> member.getRoles().contains(Roles.ModerationCS.get())),
    MCS_C(member -> member.getRoles().contains(Roles.MCS_C.get())),
    MCS(member -> member.getRoles().contains(Roles.MCS.get())),
    MCS_T(member -> member.getRoles().contains(Roles.MCS_T.get())),

    Developpeur(member -> member.getRoles().contains(Roles.Developpeur.get())),
    DevC(member -> member.getRoles().contains(Roles.DevC.get())),
    Dev(member -> member.getRoles().contains(Roles.Dev.get())),
    DevT(member -> member.getRoles().contains(Roles.DevT.get())),
    DevJava(member -> member.getRoles().contains(Roles.DevJava.get())),
    DevJS(member -> member.getRoles().contains(Roles.DevJS.get())),
    DevPy(member -> member.getRoles().contains(Roles.DevPy.get())),

    CommunityManager(member -> member.getRoles().contains(Roles.CommunityManager.get())),
    G_Comm(member -> member.getRoles().contains(Roles.G_Comm.get())),
    G_Anim(member -> member.getRoles().contains(Roles.G_Anim.get())),
    Comm(member -> member.getRoles().contains(Roles.Comm.get())),
    Anim(member -> member.getRoles().contains(Roles.Anim.get())),
    Journalimse(member -> member.getRoles().contains(Roles.Journalimse.get())),
    Graphisme(member -> member.getRoles().contains(Roles.Graphisme.get())),
    WebTV(member -> member.getRoles().contains(Roles.WebTV.get())),
    Partenariat(member -> member.getRoles().contains(Roles.Partenariat.get())),
    CM(member -> member.getRoles().contains(Roles.CM.get())),
    CM_T(member -> member.getRoles().contains(Roles.CM_T.get())),

    Staff(member -> member.getRoles().contains(Roles.Staff.get()));

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

    public enum Roles {
        Admin(r("568043750949912586")),
        Gerant(r("759696581401772062")),
        GM(r("614125560217468951")),
        G_CS(r("727173716261339228")),
        G_DEV(r("673533005867778068")),
        G_CM(r("759778116964450314")),

        Moderation(r("613998838977396746")),
        SM(r("614125182780440616")),
        M_C(r("774740327981514793")),
        M(r("774740265885761536")),
        MT(r("759779334600523856")),

        ModerationCS(r("727173484052217969")),
        MCS_C(r("727217638421299280")),
        MCS(r("727217615008956463")),
        MCS_T(r("727217096638988398")),

        Developpeur(r("759762951027818506")),
        DevC(r("641652775591346177")),
        Dev(r("759778117962956822")),
        DevT(r("766736836562255872")),
        DevJava(r("759782245887115304")),
        DevJS(r("759782264056971266")),
        DevPy(r("759782608531619841")),

        CommunityManager(r("759768776475934740")),
        G_Comm(r("782288096053166100")),
        G_Anim(r("782287659371724801")),
        Comm(r("782286477203603456")),
        Anim(r("782286429056139294")),
        Journalimse(r("782286614851747840")),
        Graphisme(r("782287152746201098")),
        WebTV(r("782286556316041216")),
        Partenariat(r("782286672544530433")),
        CM(r("759778118684246047")),
        CM_T(r("759778117861900318")),

        Staff(r("669586919960739840"));

        private final Role r;

        Roles(Role r) {
            this.r = r;
        }

        private static Role r(String id) {
            return BotInstance.getJda().getRoleById(id);
        }

        public Role get() {
            return r;
        }
    }
}
