package fr.skitou.botcore.commands;

import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.core.Config;
import fr.skitou.botcore.slashcommand.ISlashCommand;
import fr.skitou.botcore.utils.IsSenderAllowed;
import fr.skitou.botcore.utils.ReflectionUtils;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CommandAdapter extends ListenerAdapter {
    private static CommandAdapter instance;
    public final String prefix = ICommand.prefix;
    private final Logger logger = LoggerFactory.getLogger(CommandAdapter.class);
    @Getter
    private final HashSet<ISlashCommand> slashcommands = new HashSet<>();
    @Getter
    private HashSet<ICommand> commands = new HashSet<>();

    /**
     * Automatically add all detected commands as commands.
     */
    public CommandAdapter() {
        commands.addAll(ReflectionUtils.getSubTypesInstance(ICommand.class));
        commands.forEach(iCommand -> System.out.println(iCommand.getCommand()));
        commands.addAll(ReflectionUtils.getSubTypesInstance(ICommand.class, BotInstance.classicCommandPackage));
        commands.forEach(iCommand -> System.out.println(iCommand.getCommand()));
        commands.addAll(ReflectionUtils.getSubTypesInstance(ICommand.class, BotInstance.subsystemPackage));
        commands.forEach(iCommand -> System.out.println(iCommand.getCommand()));

        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("Detected commands: ");
        long total = commands.stream().peek(command -> infoBuilder.append("\n").append(command.getName())).count();
        infoBuilder.append("Total: ").append(total);
        logger.info(infoBuilder + "\n");

        slashcommands.addAll(ReflectionUtils.getSubTypesInstance(ISlashCommand.class, BotInstance.slashCommandPackage));

        infoBuilder.setLength(0);
        infoBuilder.append("Detected Slash commands: ");
        infoBuilder.append("Total: ").append(slashcommands.size());
        logger.info(infoBuilder + "\n");
    }

    /**
     * Create an instance of CommandAdapter without automatically populating it.
     *
     * @param listeners The commands to be registered.
     */
    public CommandAdapter(ICommand... listeners) {
        this.commands = new HashSet<>();
        this.commands.addAll(Set.of(listeners));
    }

    public static CommandAdapter getInstance() {
        if (instance == null) instance = new CommandAdapter();
        return instance;
    }

    /**
     * Detect commands from {@link MessageReceivedEvent messages}, and create {@link MessageReceivedEvent} if needed.
     *
     * @param event A raw {@link MessageReceivedEvent}.
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        //PERF: PLEASE CONSIDER THE ORDER OF EXECUTION OF THOSE CHECKS AS TO RETURN AS QUICKLY AS POSSIBLE FOR THE MOST COMMON CASES.

        // Since CommandReceivedEvent is a subtype of GuildMessageReceivedEvent,
        // we unfortunately receive our own generated event.
        // This is bad and WILL create an infinite loop if we don't catch it.
        if (event instanceof CommandReceivedEvent) {
            return;
        }

        // Only consider messages starting with the prefix.
        if (!(event.getMessage().getContentDisplay().startsWith(prefix))) return;

        // Avoid self-loops
        if (event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) return;

        // Avoid bots loops.
        if (event.getAuthor().isBot()
                && Objects.equals(Config.CONFIG.getProperty("commands.allowNonUser").orElse("false"), "true")) {
            return;
        }

        logger.debug("Received command.");
        onCommandReceivedEvent(new CommandReceivedEvent(event)); //Generate our custom event.
    }

    public void onCommandReceivedEvent(CommandReceivedEvent event) {
        //PERF: Can be replaced with a for loop by IntelliJ IDEA if needed.
        //Please avoid to do so unless we really need it to preserve readability.
        commands.stream()
                .filter(ICommand::isEnabled)
                .filter(command -> doesCommandMatchString(command, event.getCommand()))
                .filter(matchedCommand ->
                        matchedCommand.isSenderAllowed().test(event.getMember()) ||
                                IsSenderAllowed.BotAdmin.test(event.getMember())) //Hardcoded bypass for Bot admins.
                .limit(1) //TODO: Find a way to log an error if multiple commands match.
                .forEach(command -> dispatchEvent(command, event));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        slashcommands.stream()
                .filter(iSlashCommand -> iSlashCommand.getName().equalsIgnoreCase(event.getName()))
                .forEach(iSlashCommand -> iSlashCommand.onCommandReceived(event));
    }

    private boolean doesCommandMatchString(ICommand commandToTest, String stringToTest) {
        try {
            boolean matchesCommand = commandToTest.getCommand().equalsIgnoreCase(stringToTest);
            if (!matchesCommand)
                return commandToTest.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(stringToTest));
            else return true;
        } catch (NullPointerException npe) {
            logger.error("Error: " + commandToTest.getClass() + " #getCommand or #getAliases returns null ! THIS IS A CONTRACT VIOLATION!!!");
            return false;
        }
    }

    public void dispatchEvent(ICommand command, CommandReceivedEvent event) {
        try {
            command.onCommandReceived(event);
            logger.info(event.getAuthor().getName() + "(" + event.getAuthor().getId() + ")" + " issued the " + event.getCommand() + " command.");
        } catch (Exception exception) {
            event.getChannel().sendMessage("Command failed!\n`The error have been reported to the Java Development Team.`").queue();

            logger.error("Command {} threw a {}: {}", command.getCommand(),
                    exception.getClass().getSimpleName(), exception.getMessage());

            exception.printStackTrace();

            Sentry.captureException(exception);
        }
    }
}
