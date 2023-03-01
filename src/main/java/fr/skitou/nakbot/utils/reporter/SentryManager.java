package fr.skitou.nakbot.utils.reporter;

import fr.skitou.nakbot.core.Config;
import io.sentry.Sentry;

public class SentryManager {
    private static SentryManager instance = null;

    private SentryManager() {
        Sentry.init(opt -> opt.setDsn(Config.CONFIG.getPropertyOrDefault("sentryDns")));
    }

    public static SentryManager getInstance() {
        if (instance == null) instance = new SentryManager();
        return instance;
    }
}
