package com.il.util.service;

import com.il.util.dto.FileInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ScannerService {
    private boolean inProcess;
    private final List<ProgressView> progressListeners = new ArrayList<>();

    private final ForkJoinPool forkJoinPool = new ForkJoinPool(3);
    private final AtomicLong progressCounter = new AtomicLong(0);
    private AtomicLong totalCalculatedSize = new AtomicLong(0);
    private final int filesPerThreadMaxCount = 15;

    public ScannerService() {
        addProgressListener(new ConsoleProgressView());
    }

    public void addProgressListener(ProgressView listener) {
        this.progressListeners.add(listener);
    }

    public void cancelCurrentScan() {
        inProcess = false;
    }

    public boolean isInProcess() {
        return inProcess;
    }


    public FileInfo scan(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("Path [" + path + "] is not valid");
        }
        String[] split = path.split("\\\\");
        File root = new File(split[0]);
        long totalSize = root.getTotalSpace() - root.getFreeSpace();
        totalCalculatedSize = new AtomicLong(0);

        final long timeStart = System.nanoTime();
        log.info("Scan for: {}", path);
        inProcess = true;
        CompletableFuture.runAsync(() -> {
            while (inProcess) {
                Util.threadSleep(500);
                double progress = (double) totalCalculatedSize.get() / totalSize;
                if (inProcess) {
                    ProgressView.ProgressInfo progressInfo = new ProgressView.ProgressInfo();
                    progressInfo.setProgress(progress);
                    progressInfo.setTime((int) ((System.nanoTime() - timeStart) / 1_000_000_000.0));
                    progressInfo.setFilesProcessed(progressCounter.get());
                    progressInfo.setForkJoinPoolStatus(forkJoinPool.toString());

                    for (ProgressView listener : progressListeners) {
                        listener.notify(progressInfo);
                    }
                }
            }
        });
        FileInfo treeFI = getTreeFI(file);
        Util.threadSleep(100);
        forkJoinPool.shutdown();
        Util.threadSleep(100);
        forkJoinPool.shutdownNow();
        treeFI.setName(file.getName());
        inProcess = false;
        log.info(String.format("Time %.2f s\n", (System.nanoTime() - timeStart) / 1_000_000_000.0));
        return treeFI;
    }

    private FileInfo getTreeFI(File file) {
        ArrayList<File> files = new ArrayList<>();
        files.add(file);
        return forkJoinPool.invoke(new FilesInfoTask(files)).get(0);
    }


    private class FilesInfoTask extends RecursiveTask<List<FileInfo>> {
        private final List<File> files;

        public FilesInfoTask(List<File> files) {
            this.files = files;
        }

        @Override
        protected List<FileInfo> compute() {
            return files.stream()
                .map(this::calculateAndGet)
                .collect(Collectors.toList());
        }

        protected FileInfo calculateAndGet(File file) {
            if (!inProcess) {
                return null;
            }

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null) return null;

                // filter symlink
                files = Arrays.stream(files).filter(f -> !Files.isSymbolicLink(f.toPath())).collect(Collectors.toList()).toArray(new File[]{});

                long size = 0;
                FileInfo fileInfo = new FileInfo(file.getAbsolutePath(), file.getName());

                List<FileInfo> children;
                if (files.length > filesPerThreadMaxCount) {
                    //TODO: Make normalize threads load
                    int threadsCount = (files.length / filesPerThreadMaxCount) + 1;
                    List<FilesInfoTask> tasks = new ArrayList<>(threadsCount + 10);
                    for (int i = 0; i < threadsCount; i++) {
                        List<File> list = new ArrayList<>();
                        for (int j = filesPerThreadMaxCount * i; j < filesPerThreadMaxCount * (i+1) && j < files.length; j++) {
                            list.add(files[j]);
                        }
                        FilesInfoTask task = new FilesInfoTask(list);
                        tasks.add(task);
                    }

                    tasks.forEach(ForkJoinTask::fork);
                    children = tasks.stream()
                        .map(ForkJoinTask::join)
                        .flatMap(List::stream)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                } else {
                    children = Arrays.stream(files)
                        .map(this::calculateAndGet)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                }

                for (FileInfo child : children) {
                    size += child.getSize();
                    fileInfo.addChild(child);
                }

                if (!inProcess) {
                    return null;
                }

                List<FileInfo> sortedChildren = children.stream()
                    .sorted(Comparator.comparing(FileInfo::getSize).reversed())
                    .collect(Collectors.toList());
                fileInfo.setSize(size);
                fileInfo.setChildren(sortedChildren);
                return fileInfo;
            } else {
                totalCalculatedSize.addAndGet(file.length());
                progressCounter.incrementAndGet();
                return new FileInfo(file.getAbsolutePath(), file.getName(), file.length());
            }
        }
    }


    public interface ProgressView {
        void notify(ProgressInfo info);

        @Data
        class ProgressInfo {
            private double progress;
            private int time;
            private String forkJoinPoolStatus;
            private long filesProcessed;
        }
    }
}