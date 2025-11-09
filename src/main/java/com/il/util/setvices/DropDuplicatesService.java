package com.il.util.setvices;

import com.il.util.dto.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DropDuplicatesService {
    private static final Set<String> MEDIA_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "mp4", "mp3", "wav"));

    public List<List<FileInfo>> deepSearchDuplicates(String pathRootFolder) {
        Map<String, List<FileInfo>> hashMap = new HashMap<>();
        findDuplicates(new File(pathRootFolder), hashMap);

        List<List<FileInfo>> duplicates = new ArrayList<>();
        for (Map.Entry<String, List<FileInfo>> entry : hashMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicates.add(entry.getValue());
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

    private String getFileHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        byte[] hashBytes = digest.digest(fileBytes);
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
