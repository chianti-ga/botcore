package fr.skitou.nakbot.utils.mappers;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedMapper<K, V> extends Mapper<K, V> {
    public LinkedMapper() {
        map = new LinkedHashMap<>();
    }

    public LinkedMapper<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    @Override
    public @NotNull Map<K, V> get() {
        return map;
    }
}
