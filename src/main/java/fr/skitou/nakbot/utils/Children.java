package fr.skitou.nakbot.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Used in {@link ReflectionUtils#getSubTypesInstance(Class)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Children {
    @NotNull
    String[] targetPackages();

}