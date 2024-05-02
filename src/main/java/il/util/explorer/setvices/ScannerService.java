package il.util.explorer.setvices;

import il.util.explorer.dto.FileInfo;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ScannerService {
    private long totalSize;
    private long totalCalculatedSize;
    private boolean inProcess;
    private Consumer<Double> listener;

    public void printProgressBar(double progress) {
        int width = 50; // Width of the progress bar

        // Calculate number of characters representing progress
        int progressChars = (int) (progress * width);

        // Draw progress bar
        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            if (i < progressChars) {
                progressBar.append("=");
            } else {
                progressBar.append(" ");
            }
        }
        progressBar.append("] ");
        progressBar.append(String.format("%.2f", progress * 100)).append("% ");

        // Print progress bar
        System.out.print("\r" + progressBar);
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
        totalSize = root.getTotalSpace() - root.getFreeSpace();
        totalCalculatedSize = 0;

        System.out.println("Scan for: " + path);
        inProcess = true;
        CompletableFuture.runAsync(() -> {
            while (inProcess) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double progress = (double) totalCalculatedSize / totalSize;
                if (inProcess) {
                    if (listener != null) {
                        listener.accept(progress);
                    }
                    printProgressBar(progress);
                }
            }
            System.out.println();
        });
        FileInfo treeFI = getTreeFI(file);
        treeFI.setName(file.getName());
        inProcess = false;
        System.out.println("Done");
        return treeFI;
    }

    public void cancelCurrentScan() {
        inProcess = false;
    }

    public boolean isInProcess() {
        return inProcess;
    }

    private FileInfo getTreeFI(File file) {
        if (!inProcess) {
            return null;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return null;

            long size = 0;
            FileInfo fi = new FileInfo(file.getAbsolutePath(), file.getName());
            for (File childFile : files) {
                FileInfo child = getTreeFI(childFile);
                if (child == null) continue;

                long childSize = child.getSize();
                size += childSize;
                fi.addChild(child);
            }
            if (!inProcess) {
                return null;
            }
            fi.setSize(size);
            List<FileInfo> children = fi.getChildren();
            if (children != null) {
                List<FileInfo> sortedChildren = children.stream()
                    .sorted(Comparator.comparing(FileInfo::getSize).reversed())
                    .collect(Collectors.toList());
                fi.setChildren(sortedChildren);
            }
            return fi;
        } else {
            totalCalculatedSize += file.length();
            return new FileInfo(file.getAbsolutePath(), file.getName(), file.length());
        }
    }
}
