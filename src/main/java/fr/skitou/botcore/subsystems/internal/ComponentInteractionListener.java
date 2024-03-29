/*
 * Copyright (C) 2021-2024 Ruben Rouvière, Chianti Gally, uku3lig, Rayan Malloul, and Maxandre Rochefort.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package fr.skitou.botcore.subsystems.internal;

import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.subsystems.AbstractSubsystem;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * A subsystem that allows interaction with buttons and string select menus.
 * It handles button and string select interaction events and executes corresponding registered functions.
 *
 * @author Skitou
 */

@SuppressWarnings("unused")
public class ComponentInteractionListener extends AbstractSubsystem {

    /**
     * A map that holds the registered String Select Menu interactions.
     */
    @Getter
    private static final ConcurrentMap<String, Consumer<StringSelectInteractionEvent>> selectMenuInteraction = new ConcurrentHashMap<>();

    /**
     * A map that holds the registered Button interactions.
     */
    @Getter
    private static final ConcurrentMap<String, Runnable> buttonInteraction = new ConcurrentHashMap<>();

    /**
     * Creates a String Select Menu interaction and registers it.
     *
     * @param selectMenu The String Select Menu to create the interaction for.
     * @param function   The function to be executed when the interaction occurs.
     * @return The created String Select Menu interaction.
     */
    public static StringSelectMenu createStringSelectMenuInteraction(StringSelectMenu selectMenu, Consumer<StringSelectInteractionEvent> function) {
        String id = UUID.randomUUID().toString();
        selectMenuInteraction.put(id, function);
        return selectMenu.createCopy().setId(id).build();
    }

    /**
     * Creates a Button interaction and registers it.
     *
     * @param button   The Button to create the interaction for.
     * @param runnable The runnable to be executed when the interaction occurs.
     * @return The created Button interaction.
     */
    @SuppressWarnings("unused")
    public static Button createButtonInteraction(Button button, Runnable runnable) {
        String id = UUID.randomUUID().toString();
        buttonInteraction.put(id, runnable);
        return button.withId(id);
    }

    /**
     * Creates an Entity Select Menu interaction and registers it.
     *
     * @param selectMenu The Entity Select Menu to create the interaction for.
     * @param function   The function to be executed when the interaction occurs.
     * @return The created Entity Select Menu interaction.
     */
    @SuppressWarnings("unused")
    public static EntitySelectMenu createEntitySelectMenuInteraction(EntitySelectMenu selectMenu, Consumer<StringSelectInteractionEvent> function) {
        String id = UUID.randomUUID().toString();
        selectMenuInteraction.put(id, function);
        return selectMenu.createCopy().setId(id).build();
    }

    /**
     * Indicates whether the subsystem is enabled by default.
     *
     * @return True if the subsystem is enabled by default, false otherwise.
     */
    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    /**
     * Retrieves the name of the subsystem.
     *
     * @return The name of the subsystem.
     */
    @Override
    public @NotNull String getName() {
        return "ButtonInteractionListener";
    }

    /**
     * Retrieves the description of the subsystem.
     *
     * @return The description of the subsystem.
     */
    @Override
    public @NotNull String getDescription() {
        return "Allow interaction with buttons.";
    }

    /**
     * Handles the String Select Interaction event.
     *
     * @param event The String Select Interaction event.
     */
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (selectMenuInteraction.containsKey(event.getComponentId())) {
            selectMenuInteraction.get(event.getComponentId()).accept(event);
            BotInstance.logger.info("Trigger StringSelectInteractionEvent(" + event.getComponentId() + ").");

            selectMenuInteraction.remove(event.getComponentId());
        }
    }

    /**
     * Handles the Button Interaction event.
     *
     * @param event The Button Interaction event.
     */
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (buttonInteraction.containsKey(event.getComponentId())) {
            buttonInteraction.get(event.getComponentId()).run();
            BotInstance.logger.info("Trigger ButtonInteraction(" + event.getComponentId() + ").");
            buttonInteraction.remove(event.getComponentId());
        }
    }
}