package fr.skitou.botcore.core;

import fr.skitou.botcore.commands.classic.CommandAdapter;
import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.botcore.hibernate.Database;
import fr.skitou.botcore.subsystems.SubsystemAdapter;
import fr.skitou.botcore.utils.FilesCache;
import fr.skitou.botcore.utils.reporter.SentryManager;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class BotInstance {
    public static final Logger logger = LoggerFactory.getLogger(BotInstance.class);
    public static final ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static final Set<Runnable> runnables = new HashSet<>();
    @Getter
    private static final FilesCache filesCache = new FilesCache();
    public static String classicCommandPackage;
    public static String slashCommandPackage;
    public static String subsystemPackage;
    public static String entitiesPackagePackage;
    @Getter
    private static Set<EventListener> eventListeners = null;
    @Getter
    private static JDA jda = null;

    private static String[] botArgs = null;
    @Getter
    private static String coreVersion;


    private BotInstance(BotInstanceBuilder builder) {
        classicCommandPackage = builder.classicCommandPackage;
        slashCommandPackage = builder.slashCommandPackage;
        subsystemPackage = builder.subsystemPackage;
        entitiesPackagePackage = builder.databaseEntitiesPackage;
        botArgs = builder.args;

        Database.getFactory(); // Init db BEFORE adapters
        eventListeners = Set.of(CommandAdapter.getInstance(), SubsystemAdapter.getInstance());

        logger.warn(isTestMode() ? "Bot Starting in TESTMODE" : "Bot Starting");


        // Create instances of SlashCommands when JDA is ready
        runWhenReady(() -> {
            logger.info("Updating guild commands!");
            HashSet<ISlashCommand> slashCommands = CommandAdapter.getInstance().getSlashcommands();

            jda.getGuilds().forEach(guild -> {
                Set<SlashCommandData> a = slashCommands.stream()
                        .map(iSlashCommand ->
                                Commands.slash(iSlashCommand.getName().toLowerCase(), iSlashCommand.getHelp())
                                        .addOptions(iSlashCommand.getOptionData())
                                        .addSubcommands(iSlashCommand.getSubcommandDatas())).collect(Collectors.toSet());
                guild.updateCommands().addCommands(a).queue();
            });

        });

        try {
            jda = JDABuilder.createDefault(getToken())
                    // Please stop committing tokens.
                    .addEventListeners(eventListeners.toArray())
                    .setActivity(builder.activity)
                    .enableCache(builder.enabledcacheFlags)
                    .disableCache(builder.disabledcacheFlags)
                    .enableIntents(builder.enabledintents)
                    .disableIntents(builder.disabledintents)
                    .build();
        } catch (NullPointerException e) {
            e.printStackTrace();
            e.getCause();
            logger.error("ERROR: Login failed: " + e.getMessage() + ":" + Arrays.toString(e.getStackTrace()) + "\n Check the token or retry later.");
            Runtime.getRuntime().exit(2);
        }
        SentryManager.getInstance();

        //Run once injection point
        //Only used for test purposes.
        try {
            jda.awaitReady();
            runWhenReady();
        } catch (InterruptedException e) {
            logger.error("Runnable threw a {}: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            Sentry.captureException(e);
            Thread.currentThread().interrupt();
        }

        try {
            Manifest manifest = new Manifest(ClassLoader.getSystemResourceAsStream("META-INF/MANIFEST.MF"));
            coreVersion = manifest.getMainAttributes().getValue("BotCore-Version");
        } catch (IOException e) {
            logger.error("Can't get BotCore-Version from jar manifest (normal on dev env).");
        }

    }

    /**
     * Add a {@link Runnable} to the {@link Set} of {@link Runnable Runnables} to execute when {@link JDA#awaitReady() JDA is ready}.
     *
     * @param runnable A {@link Runnable} to execute when JDA is ready.
     */
    public static void runWhenReady(Runnable runnable) {
        runnables.add(runnable);
    }

    /**
     * Run queued {@link Runnable Runnables}.
     *
     * @see #runWhenReady(Runnable)
     */
    private static void runWhenReady() {
        runnables.forEach(Runnable::run);
    }

    private static String getToken() {
        String token = "";
        if (token.isEmpty()) {
            //Config
            Optional<String> opToken = Config.CONFIG.getProperty("bot.token");
            if (opToken.isPresent() && !opToken.get().isEmpty()) {
                logger.info("Using config as the token provider.");
                return opToken.get();
            } else {
                logger.warn("No token found 😕.");
            }
        }
        return token;
    }

    /**
     * Checks whether the application is running in test mode.
     * Test mode is enabled with the command line argument "-Dtest".
     *
     * @return {@code true} if the application is running in test mode, {@code false} otherwise.
     */
    public static boolean isTestMode() {
        List<String> args = Arrays.asList(botArgs);
        Optional<String> optional = args.stream().filter(arg -> arg.replaceFirst("-", "").equalsIgnoreCase("Dtest")).findFirst();
        return optional.isPresent();
    }

    public static class BotInstanceBuilder {
        private final String[] args;
        private String classicCommandPackage;
        private String slashCommandPackage;
        private String subsystemPackage;
        private String databaseEntitiesPackage;
        private Activity activity;

        private Collection<CacheFlag> enabledcacheFlags;
        private Collection<GatewayIntent> enabledintents;
        private Collection<CacheFlag> disabledcacheFlags;
        private Collection<GatewayIntent> disabledintents;

        public BotInstanceBuilder(String[] args) {
            this.args = args;
        }


        public BotInstanceBuilder setEntitiesPackagePackage(String databaseEntitiesPackage) {
            this.databaseEntitiesPackage = databaseEntitiesPackage;
            return this;
        }

        public BotInstanceBuilder setCMDPackage(String classicCommandPackage) {
            this.classicCommandPackage = classicCommandPackage;
            return this;
        }

        public BotInstanceBuilder setSlashCMDPackage(String slashCommandPackage) {
            this.slashCommandPackage = slashCommandPackage;
            return this;
        }

        public BotInstanceBuilder setSubsystemPackage(String subsystemPackage) {
            this.subsystemPackage = subsystemPackage;
            return this;
        }

        public BotInstanceBuilder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public BotInstanceBuilder setEnabledcacheFlags(Collection<CacheFlag> enabledcacheFlags) {
            this.enabledcacheFlags = enabledcacheFlags;
            return this;
        }

        public BotInstanceBuilder setEnabledintents(Collection<GatewayIntent> enabledintents) {
            this.enabledintents = enabledintents;
            return this;
        }

        public BotInstanceBuilder setDisabledcacheFlags(Collection<CacheFlag> disabledcacheFlags) {
            this.disabledcacheFlags = disabledcacheFlags;
            return this;
        }

        public BotInstanceBuilder setDisabledintents(Collection<GatewayIntent> disabledintents) {
            this.disabledintents = disabledintents;
            return this;
        }

        public BotInstance build() {
            //noinspection InstantiationOfUtilityClass
            return new BotInstance(this);
        }
    }

}
