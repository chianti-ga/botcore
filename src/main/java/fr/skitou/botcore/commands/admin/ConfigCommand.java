package fr.skitou.botcore.commands.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.skitou.botcore.commands.AbstractCommand;
import fr.skitou.botcore.commands.CommandReceivedEvent;
import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.core.Config;
import fr.skitou.botcore.utils.IsSenderAllowed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * The configuration command, edit the json config file from bot command
 *
 * @author RyFax
 */
public class ConfigCommand extends AbstractCommand {

    private final Logger logger = LoggerFactory.getLogger(ConfigCommand.class);
    private final Config conf = Config.CONFIG;

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage(getHelp()).queue();
            return;
        }

        switch (event.getArgs().get(0)) {
            case "list" -> listConfig(event);
            case "edit" -> {
                if (event.getArgs().size() >= 3) editConfig(event);
                else event.getChannel().sendMessage(prefix + "config edit [key] [value]").queue();
            }
            case "add" -> {
                if (event.getArgs().size() >= 3) addConfig(event);
                else event.getChannel().sendMessage(prefix + "config add [key] [value]").queue();
            }
            case "remove" -> {
                if (event.getArgs().size() >= 3) removeConfig(event);
                else event.getChannel().sendMessage(prefix + "config remove [key] [value]").queue();
            }
            default -> event.getChannel().sendMessage(getHelp()).queue();
        }
    }

    @Override
    public @NotNull String getCommand() {
        return "config";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Collections.singletonList("conf");
    }

    @Override
    public @NotNull String getName() {
        return "Configuration";
    }

    @Override
    public @NotNull String getHelp() {
        return prefix + "config (list|edit|add|remove) [*key*] [*value*]";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }

    /**
     * Removing from a list
     */
    private void removeConfig(CommandReceivedEvent event) {
        Setting setting = new Setting(event);

        Optional<JsonElement> property = conf.getPropertyElement(setting.getKey());

        if (property.isEmpty()) {
            event.getChannel().sendMessage("Key not existing").queue();
            return;
        } else if (!property.get().isJsonArray()) {
            event.getChannel().sendMessage("To edit a single parameter please use " + prefix + "config edit [key] [value]").queue();
            return;
        }

        boolean found = false;
        JsonArray jsonArray = ((JsonArray) property.get());
        for (JsonElement el : jsonArray) {
            if (el.isJsonObject() || el.isJsonArray()) continue;

            if (el.getAsString().equals(setting.getValue())) {
                jsonArray.remove(el);
                found = true;
                break;
            }
        }

        if (found) {
            boolean saved = save(event);
            if (saved) {
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(0x1efa88)
                        .setTitle(getName())
                        .setDescription("`" + setting.getKey() + "` removed `" + setting.getValue() + "`")
                        .build();

                event.getChannel().sendMessageEmbeds(embed).queue();
            }
        } else {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(0xfc5444)
                    .setTitle(getName())
                    .setDescription("`" + setting.getKey() + "` can't removed `" + setting.getValue() + "`: not found.")
                    .build();

            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }

    /**
     * Adding to a list
     */
    private void addConfig(CommandReceivedEvent event) {
        Setting setting = new Setting(event);

        Optional<JsonElement> property = conf.getPropertyElement(setting.getKey());

        if (property.isEmpty()) {
            event.getChannel().sendMessage("Key not existing").queue();
            return;
        } else if (!property.get().isJsonArray()) {
            event.getChannel().sendMessage("To edit a single parameter please use " + prefix + "config edit [key] [value]").queue();
            return;
        }

        ((JsonArray) property.get()).add(setting.getValue());

        boolean saved = save(event);
        if (saved) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(0x1efa88)
                    .setTitle(getName())
                    .setDescription("`" + setting.getKey() + "` added `" + setting.getValue() + "`")
                    .build();

            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }

    /**
     * Edit a simple value
     */
    private void editConfig(CommandReceivedEvent event) {
        Setting setting = new Setting(event);

        Optional<JsonElement> property = conf.getPropertyElement(setting.getKey());

        if (property.isEmpty()) {
            event.getChannel().sendMessage("Key not existing").queue();
            return;
        } else if (property.get().isJsonArray()) {
            event.getChannel().sendMessage("To edit a list please use " + prefix + "config add/remove [key] [value]").queue();
            return;
        }

        conf.setProperty(setting.getKey(), setting.getValue());

        boolean saved = save(event);
        if (saved) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(0x1efa88)
                    .setTitle(getName())
                    .setDescription("`" + setting.getKey() + "` set to `" + setting.getValue() + "`")
                    .build();

            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }

    /**
     * Send to the channel the current configuration
     */
    private void listConfig(CommandReceivedEvent event) {
        String json = applySecret(conf, conf.getGson().toJson(conf.getRootJson()));

        MessageEmbed embed = new EmbedBuilder()
                .setColor(0x3783ed)
                .setTitle(getName())
                .setDescription("List of properties in config.json:\n```JSON\n" + json + "```")
                .build();

        event.getChannel().sendMessageEmbeds(embed).queue();
    }

    /**
     * Try to save the config
     *
     * @return true if file saved successfully, and false if not
     */
    private boolean save(CommandReceivedEvent event) {
        try {
            conf.saveModifications();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            event.getChannel().sendMessage("An internal failure occurred. The config was not saved.").queue();
            return false;
        }
    }

    /**
     * Security : Replacing secret words like TOKEN to [SECRET]
     */
    private String applySecret(Config config, String source) {
        // Just to be sure there is no token in string
        List<String> secrets = List.of(
                BotInstance.getJda().getToken(),
                config.getPropertyOrDefault("bot.token"),
                config.getPropertyOrDefault("loggerUrl")
        );

        for (String secret : secrets) source = source.replaceAll(secret, "[SECRET]");
        return source;
    }

    /**
     * Split arguments in command received to a key and value
     */
    private static class Setting {
        private final String key;
        private final String value;

        public Setting(CommandReceivedEvent event) {
            StringJoiner sj = new StringJoiner(" ");

            event.getArgs().stream().skip(2).forEach(sj::add);

            this.key = event.getArgs().get(1);
            this.value = sj.toString();
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }

}
