package com.il.util.setvices.rename;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class NumRule implements Rule {

    @Override
    public List<String> getPreview(List<String> files) {
        return files.stream()
            .map(fileName -> getNewFileName(fileName, files.indexOf(fileName) + 1))
            .collect(Collectors.toList());
    }

    @Override
    public void doRename(List<String> files) {
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));
            if (file.exists()) {
                String newFileName = getNewFileName(file.getName(), i + 1);
                File newFile = new File(file.getParent(), newFileName);
                file.renameTo(newFile);
            }
        }
    }

    // Method to generate new file name while preserving extension
    private String getNewFileName(String fileName, int index) {
        String extension = getFileExtension(fileName);
        String baseName = "file_" + index;  // Base name for the file
        return extension.isEmpty() ? baseName : baseName + "." + extension;
    }

    // Method to extract file extension
    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // No extension
        }
        return fileName.substring(lastIndexOfDot + 1);
    }
}

