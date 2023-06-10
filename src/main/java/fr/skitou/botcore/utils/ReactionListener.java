package fr.skitou.botcore.utils;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

// FIXME: NOT WORKING ANYMORE
public class ReactionListener extends ListenerAdapter {/*
    private final Message source;
    private final Map<String, Runnable> actions;
    private final Predicate<Member> allowed;
    private final int maxUses;
    private final Duration timeout;
    private int currentUses = 0;
    private Timer timer = new Timer();

    //Protected to prevent user instantiation, but can still be used by reflections
    private ReactionListener() {
        source = null;
        actions = Collections.emptyMap();
        allowed = IsSenderAllowed.Default;
        maxUses = 0;
        timeout = Duration.ZERO;
    }

    public ReactionListener(Message source, Map<String, Runnable> actions, Predicate<Member> allowed,
                            int maxUses, Duration timeout) {
        this.source = source;
        this.actions = actions;
        this.allowed = allowed;
        this.maxUses = maxUses;
        this.timeout = timeout;
        this.timer = scheduleFromNow(timer, timeout);

        this.actions.keySet().forEach(r -> this.source.addReaction(getReactionEmote(r)).queue());

        BotInstance.getJda().addEventListener(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder yesNo(Runnable yes, Runnable no) {
        return new Builder(Map.of(
                EmojiParser.parseToUnicode(":white_check_mark:"), yes,
                EmojiParser.parseToUnicode(":negative_squared_cross_mark:"), no));
    }

    public static Builder yesNo(Runnable yes, TextChannel c) {
        return yesNo(yes, () -> c.sendMessage("Canceled").queue());
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (!event.getMessageId().equals(source.getId())) return;
        if (!actions.containsKey(event.getEmoji())) return;
        if (actions.keySet().stream()
                .noneMatch(emoji -> event.getEmoji().equals(emoji))) return;
        if (!allowed.test(event.getMember())) return;

        actions.get(event.getEmoji(). ?
                event.getReactionEmote().getEmote().getAsMention() :
                event.getReactionEmote().getEmoji()).run();
        currentUses++;
        timer = scheduleFromNow(timer, timeout);
        if (currentUses >= maxUses) BotInstance.getJda().removeEventListener(this);
    }

    private Timer scheduleFromNow(Timer t, Duration d) {
        t.cancel();
        t = new Timer();
        ReactionListener instance = this;
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                source.getChannel().sendMessage("Cancelled due to timeout").queue();
                BotInstance.getJda().removeEventListener(instance);
            }
        }, Date.from(Instant.now().plus(d)));
        return t;
    }

    private String getReactionEmote(String source) {
        return String.join(":",
                source.replace("<", "")
                        .replace(">", "")
                        .split(":"));
    }

    public static class Builder {
        private Message source = null;
        private Map<String, Runnable> actions = null;
        private Predicate<Member> allowed = IsSenderAllowed.Default;
        private int maxUses = 1;
        private Duration timeout = Duration.ofMinutes(1);

        public Builder(Map<String, Runnable> actions) {
            this.actions = actions;
        }

        public Builder() {
        }

        public Builder source(Message source) {
            this.source = source;
            return this;
        }

        /**
         * Adds the reaction emojis, along with the action for each emoji. <br>
         * <b>WARNING:</b> The emoji (discord native) MUST be in unicode, and the
         * {@link net.dv8tion.jda.api.entities.Emote emotes} (user added) must be in a mention format (<code>&lt;:name:id></code>).
         *
         * @param actions The {@link Map map} containing all the emojis and actions.
         * @return itself.
         */
       /* public Builder actions(Map<String, Runnable> actions) {
            this.actions = actions;
            return this;
        }

        public Builder allowed(Predicate<Member> allowed) {
            this.allowed = allowed;
            return this;
        }

        public Builder maxUses(int maxUses) {
            this.maxUses = maxUses;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public void build() {
            if (source == null || actions == null)
                throw new IllegalArgumentException("Source message or actions are not set!");
            new ReactionListener(source, actions, allowed, maxUses, timeout);
        }
    }*/
}