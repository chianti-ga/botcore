package fr.skitou.nakbot.utils.mappers;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class Mapper<K, V> {
    protected Map<K, V> map;

    @NotNull
    public abstract Map<K, V> get();
}
