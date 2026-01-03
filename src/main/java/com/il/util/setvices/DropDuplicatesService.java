package com.il.util.setvices;

import com.il.util.dto.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@Slf4j
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DropDuplicatesService {
    private static final Set<String> MEDIA_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "mp4", "mp3", "wav"));

    private final ForkJoinPool forkJoinPool = new ForkJoinPool(3);
    private final int filesPerThreadMaxCount = 15;
    private class FilesInfoTask extends RecursiveTask<List<FileInfo>> {
        @Override
        protected List<FileInfo> compute() {

            return null;
        }
    }

    public List<List<FileInfo>> deepSearchDuplicates(String pathRootFolder) {
        Map<String, List<FileInfo>> hashMap = new HashMap<>();
        findDuplicates(new File(pathRootFolder), hashMap);

        List<List<FileInfo>> duplicates = new ArrayList<>();

        Iterator<Map.Entry<String, List<FileInfo>>> it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<FileInfo>> entry = it.next();
            List<FileInfo> group = new ArrayList<>(entry.getValue());
            if (group.size() <= 1) {
                it.remove();
                continue;
            }

            List<FileInfo> confirmedDuplicates = new ArrayList<>();
            while (!group.isEmpty()) {
                FileInfo fi = group.remove(0);
                File file = new File(fi.getPath());
                confirmedDuplicates.add(fi);

                Iterator<FileInfo> innerIt = group.iterator();
                while (innerIt.hasNext()) {
                    FileInfo otherFi = innerIt.next();
                    File otherFile = new File(otherFi.getPath());
                    try {
                        if (filesAreEqual(file, otherFile)) {
                            confirmedDuplicates.add(otherFi);
                            innerIt.remove();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (confirmedDuplicates.size() > 1) {
                duplicates.add(confirmedDuplicates);
            } else {
                it.remove();
            }
        }

        return duplicates;
    }

    private void findDuplicates(File folder, Map<String, List<FileInfo>> hashMap) {
        if (folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isDirectory()) {
                    findDuplicates(file, hashMap);
                } else if (isMediaFile(file)) {
                    try {
                        String fileHash = getFileHash(file);
                        FileInfo fileInfo = new FileInfo(file.getAbsolutePath());
                        fileInfo.setName(file.getName());
                        fileInfo.setHash(fileHash);
                        fileInfo.setSize(file.length());
                        hashMap.computeIfAbsent(fileHash, k -> new ArrayList<>()).add(fileInfo);
                    } catch (IOException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean isMediaFile(File file) {
        String extension = getFileExtension(file.getName());
        return MEDIA_EXTENSIONS.contains(extension.toLowerCase());
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    private boolean filesAreEqual(File f1, File f2) throws IOException {
        if (f1.length() != f2.length()) return false;

        try (InputStream is1 = new BufferedInputStream(new FileInputStream(f1));
             InputStream is2 = new BufferedInputStream(new FileInputStream(f2))) {

            int b1, b2;
            while ((b1 = is1.read()) != -1) {
                b2 = is2.read();
                if (b1 != b2) return false;
            }
            return true;
        }
    }


    private String getFileHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (
            InputStream is = Files.newInputStream(file.toPath());
            DigestInputStream dis = new DigestInputStream(is, digest)
        ) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) { }
        }
        byte[] hashBytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public void findDuplicates(List<String> baseFolders) {
        List<File> baseFoldersAsFiles =
            baseFolders.stream().map(File::new)
            .collect(Collectors.toList());

    }

    public void dropDuplicates(String rootFolder, String cleanupFolder) {
        File root = new File(rootFolder);
        File cleanup = new File(cleanupFolder);

        File[] rootFiles = root.listFiles();
        File[] cleanupFiles = cleanup.listFiles();

        List<String> rootFilesNames = Arrays.stream(rootFiles).map(File::getName).collect(Collectors.toList());

        int countDuplicates = 0;
        for (File file : cleanupFiles) {
            String name = file.getName();
            if (rootFilesNames.contains(name)) {
                countDuplicates++;
                file.renameTo(new File("C:\\Users\\ImplementLife\\Desktop\\\u0421 \u041D\u043E\u0443\u0442\u0430\\from telephone\\temp\\" + name));
            }
        }
        log.info("countDuplicates = {}", countDuplicates);
    }
}
