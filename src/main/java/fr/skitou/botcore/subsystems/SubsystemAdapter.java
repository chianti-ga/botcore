package fr.skitou.botcore.subsystems;

import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.utils.ReflectionUtils;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.StringJoiner;

public class SubsystemAdapter implements EventListener {
    @Getter
    private static final HashSet<ISubsystem> subsystems = new HashSet<>();
    private static final Logger logger = LoggerFactory.getLogger(SubsystemAdapter.class);
    private static SubsystemAdapter instance;

    public SubsystemAdapter() {

        subsystems.addAll(ReflectionUtils.getSubTypesInstance(ISubsystem.class));
        subsystems.addAll(ReflectionUtils.getSubTypesInstance(ISubsystem.class, BotInstance.subsystemPackage));

        StringJoiner subsystemStringJoiner = new StringJoiner("\n");
        subsystems.forEach(subsystem -> subsystemStringJoiner.add(subsystem.getName()));
        logger.info("Detected subsystems: \n" + subsystemStringJoiner);
        StringJoiner enabledSubsystemStringJoiner = new StringJoiner("\n");
        subsystems.stream().filter(ISubsystem::isEnabled).forEach(subsystem -> enabledSubsystemStringJoiner.add(subsystem.getName()));
        logger.info("Enabled subsystems: \n" + enabledSubsystemStringJoiner);
    }

    public static SubsystemAdapter getInstance() {
        if (instance == null) instance = new SubsystemAdapter();
        return instance;
    }

    /**
     * Dispatch {@link GenericEvent GenericEvents} to all {@link ISubsystem#isEnabled() enabled} {@link ISubsystem ISubsystems}.
     */
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        subsystems.stream().filter(ISubsystem::isEnabled).forEach(subsystem -> {
            try {
                subsystem.onEvent(event);
            } catch (Exception exception) {
                logger.error("Subsystem {} threw a {}: {}", subsystem.getName(),
                        exception.getClass().getSimpleName(), exception.getMessage());

                exception.printStackTrace();

                Sentry.captureException(exception);
            }
        });
    }
}
