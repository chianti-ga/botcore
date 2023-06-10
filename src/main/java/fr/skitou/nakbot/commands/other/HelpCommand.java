package fr.skitou.nakbot.commands.other;

import fr.skitou.nakbot.commands.AbstractCommand;
import fr.skitou.nakbot.commands.CommandAdapter;
import fr.skitou.nakbot.commands.CommandReceivedEvent;
import fr.skitou.nakbot.core.BotInstance;
import fr.skitou.nakbot.utils.IsSenderAllowed;
import fr.skitou.nakbot.utils.NotWorking;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") //Automatically discovered via reflection. See CommandAdapter.
@NotWorking
public class HelpCommand extends AbstractCommand {
    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().size() == 1) {
            BotInstance.getEventListeners().stream().filter(eventListener -> eventListener instanceof CommandAdapter)
                    .forEach(eventListener -> ((CommandAdapter) eventListener).getCommands()
                            .stream().filter(iCommand -> iCommand.getClass().getSimpleName().equalsIgnoreCase(event.getArgs().get(0)) || iCommand.getCommand().equalsIgnoreCase(event.getArgs().get(0)))
                            .findFirst().ifPresentOrElse(
                                    iCommand -> event.getChannel().sendMessage(iCommand.getHelp()).queue(),
                                    () -> event.getChannel().sendMessage("Error: Command not found").queue()));
            return;
        }
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Commands");
        Map<String, List<String>> fields = new HashMap<>();
        BotInstance.getEventListeners().stream().filter(eventListener -> eventListener instanceof CommandAdapter)
                .forEach(eventListener -> ((CommandAdapter) eventListener).getCommands()
                        .forEach(command -> {
                            if (command.isSenderAllowed().test(event.getMember())) {
                                String[] pkg = command.getClass().getPackage().getName().split("\\.");
                                String lastPkg = pkg[pkg.length - 1];
                                if (!fields.containsKey(lastPkg))
                                    fields.put(lastPkg, new ArrayList<>());
                                String commandAndHelp = command.getClass().getSimpleName() +
                                        ": " +
                                        command.getHelp() +
                                        "\n";
                                fields.get(lastPkg).add(commandAndHelp);
                            }
                        }));
        fields.forEach((commandType, commands) -> {
            StringBuilder sb = new StringBuilder();
            commands.forEach(sb::append);
            builder.addField(commandType, sb.toString(), false);
        });
        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }

    @Override
    public @NotNull String getCommand() {
        return "help";
    }

    @Override
    public @NotNull String getName() {
        return "help";
    }

    @Override
    public @NotNull String getHelp() {
        return "^help *[command]*";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.Default;
    }
}
