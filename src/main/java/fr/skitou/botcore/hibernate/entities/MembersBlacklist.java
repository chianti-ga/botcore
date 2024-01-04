package fr.skitou.botcore.hibernate.entities;

import fr.skitou.botcore.hibernate.Database;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@Entity
@Table(name = "membersBlacklist")
public class MembersBlacklist {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private long guild;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> blacklistedMembers;

    public MembersBlacklist(long guild, Set<Long> blacklistedMembers) {
        this.guild = guild;
        this.blacklistedMembers = blacklistedMembers;

        Database.saveOrUpdate(this);
    }

    protected MembersBlacklist() {
    }
}
