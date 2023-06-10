package fr.skitou.botcore.utils;

import com.google.common.util.concurrent.AtomicDouble;
import fr.skitou.botcore.utils.mappers.LinkedMapper;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class CollectionUtils {
    public static final Logger logger = LoggerFactory.getLogger(CollectionUtils.class);

    private CollectionUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> void sendListAsMessage(Collection<T> collection, TextChannel channel) {
        if (collection.isEmpty()) {
            logger.warn("Provided map is empty");
            return;
        }
        StringBuilder sb = new StringBuilder();
        collection.forEach(object -> {
            if (sb.length() + object.toString().length() >= 2000) {
                channel.sendMessage(sb.toString()).queue();
                sb.delete(0, sb.length());
            }
            sb.append(object).append("\n");
        });
        channel.sendMessage(sb.toString()).queue();
    }

    public static <K, V> void sendListAsMessage(Map<K, V> map, TextChannel channel) {
        if (map.isEmpty()) {
            logger.warn("Provided map is empty");
            return;
        }
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> {
            String tmp = key + ": " + value;
            if (sb.length() + tmp.length() >= 2000) {
                channel.sendMessage(sb.toString()).queue();
                sb.delete(0, sb.length());
            }
            sb.append(tmp).append("\n");
        });
        channel.sendMessage(sb.toString()).queue();
    }

    public static <K, V> LinkedMapper<K, V> linkedMapper(Class<K> keys, Class<V> values) {
        return new LinkedMapper<>();
    }

    public static double doubleSum(Collection<Number> numbers) {
        AtomicDouble sum = new AtomicDouble(0.0);
        numbers.forEach(num -> sum.addAndGet(num.doubleValue()));
        return sum.get();
    }

    public static long longSum(Collection<Long> numbers) {
        AtomicLong sum = new AtomicLong(0);
        numbers.forEach(sum::addAndGet);
        return sum.get();
    }

    public static long max(Collection<Long> numbers) {
        AtomicLong max = new AtomicLong(numbers.stream().findFirst().orElseThrow());
        numbers.forEach(num -> max.set(Math.max(num, max.get())));
        return max.get();
    }

    public static boolean isMaxDuplicate(Collection<Long> numbers) {
        long max = max(numbers);
        return numbers.stream().filter(num -> num == max).count() > 1;
    }

    public static boolean isMaxDuplicate(Collection<Long> numbers, long max) {
        return numbers.stream().filter(num -> num == max).count() > 1;
    }

    public static <T> Stream<T> bounds(Collection<T> c, long skip, long limit) {
        return c.stream().skip(skip).limit(limit);
    }

    public static <T> Stream<T> skipOne(Collection<T> c) {
        return c.stream().skip(1);
    }
}
