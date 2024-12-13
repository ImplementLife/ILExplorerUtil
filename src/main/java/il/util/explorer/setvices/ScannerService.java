package il.util.explorer.setvices;

import il.util.explorer.dto.FileInfo;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static il.util.explorer.setvices.Util.threadSleep;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ScannerService {
    private boolean inProcess;
    private Consumer<Double> listener;

    private final ForkJoinPool forkJoinPool = new ForkJoinPool(3);
    private final AtomicLong progressCounter = new AtomicLong(0);
    private AtomicLong totalCalculatedSize = new AtomicLong(0);
    private final int filesPerThreadMaxCount = 15;

    public void printProgressBar(double progress) {
        int width = 50; // Width of the progress bar

        // Calculate number of characters representing progress
        int progressChars = (int) (progress * width);

        // Draw progress bar
        StringBuilder progressBar = new StringBuilder();
        progressBar.append("[");
        for (int i = 0; i < width; i++) {
            if (i < progressChars) {
                progressBar.append("=");
            } else {
                progressBar.append(" ");
            }
        }
        progressBar.append("] ");
        progressBar.append(String.format("%.2f", progress * 100)).append("% ").append("\n");
        progressBar.append(forkJoinPool).append("\n");
        progressBar.append(String.format("Progress: %d files processed", progressCounter.get())).append("\n");

        // Print progress bar
//        System.out.print('\r');
        System.out.print(progressBar);
    }

    public void addProgressListener(Consumer<Double> listener) {
        this.listener = listener;
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
        System.out.println("Scan for: " + path);
        inProcess = true;
        CompletableFuture.runAsync(() -> {
            while (inProcess) {
                threadSleep(1000);
                double progress = (double) totalCalculatedSize.get() / totalSize;
                if (inProcess) {
                    if (listener != null) {
                        listener.accept(progress);
                    }
                    printProgressBar(progress);
                    System.out.printf("Time %.2f s\n", (System.nanoTime() - timeStart) / 1_000_000_000.0);
                }
            }
            System.out.println();
        });
        FileInfo treeFI = getTreeFI(file);
        threadSleep(100);
        forkJoinPool.shutdown();
        threadSleep(100);
        forkJoinPool.shutdownNow();
        treeFI.setName(file.getName());
        inProcess = false;
        System.out.printf("Done for %.2f seconds\n", (System.nanoTime() - timeStart) / 1_000_000_000.0);
        return treeFI;
    }

    public void cancelCurrentScan() {
        inProcess = false;
    }

    public boolean isInProcess() {
        return inProcess;
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

    private FileInfo getTreeFI(File file) {
        ArrayList<File> files = new ArrayList<>();
        files.add(file);
        return forkJoinPool.invoke(new FilesInfoTask(files)).get(0);
    }
}