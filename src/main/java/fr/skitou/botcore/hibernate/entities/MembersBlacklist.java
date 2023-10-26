package fr.skitou.botcore.hibernate.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
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
    @ElementCollection
    private Map<Long, Integer> blacklistedMembers;

    public MembersBlacklist(long guild, Map<Long, Integer> blacklistedMembers) {
        this.guild = guild;
        this.blacklistedMembers = blacklistedMembers;
    }
}
