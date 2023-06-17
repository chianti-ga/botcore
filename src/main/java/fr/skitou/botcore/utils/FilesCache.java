package fr.skitou.botcore.utils;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesCache {

    protected static final Logger logger = LoggerFactory.getLogger(FilesCache.class);
    private final ScheduledExecutorService scanExecutor = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService deleteExecutor = Executors.newScheduledThreadPool(1);

    @Getter
    private final File cacheFolder = new File("data/cache/");
    private List<File> cachedFiles = new ArrayList<>();
    @Getter
    @Setter
    private List<String> ignoreList = new ArrayList<>();


    public FilesCache() {
        if (!cacheFolder.exists()) //noinspection ResultOfMethodCallIgnored
            cacheFolder.mkdirs();
        scan();
        scanExecutor.scheduleWithFixedDelay(this::scan, 0, 5, TimeUnit.MINUTES);
        deleteExecutor.scheduleWithFixedDelay(this::deleteDisposableFiles, 0, 15, TimeUnit.MINUTES);
    }

    private void scan() {
        try (Stream<Path> walk = Files.walk(cacheFolder.toPath())) {
            cachedFiles = walk.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
            cachedFiles.forEach(File::deleteOnExit); // Delete them even if the bot crash or suddenly stop
            //noinspection ResultOfMethodCallIgnored
            ignoreList.forEach(String::toLowerCase);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all files who are in the cache at least ~10minutes
     */
    private void deleteDisposableFiles() {
        AtomicInteger fileCount = new AtomicInteger();
        logger.info("Performing cache cleanup...");
        cachedFiles.forEach(file -> {
            if (file.exists() && !ignoreList.contains(file.getName().toLowerCase())) {
                fileCount.getAndIncrement();
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        });

        logger.info(fileCount + " cached files deleted.");
        fileCount.set(0);
    }
}
