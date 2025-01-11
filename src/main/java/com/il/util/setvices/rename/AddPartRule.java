package com.il.util.setvices.rename;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AddPartRule implements Rule {
    private final String partToAdd;

    public AddPartRule(String partToAdd) {
        this.partToAdd = partToAdd;
    }

    @Override
    public List<String> getPreview(List<String> files) {
        return files.stream()
            .map(fileName -> partToAdd + fileName)
            .collect(Collectors.toList());
    }

    @Override
    public void doRename(List<String> files) {
        for (String filePath : files) {
            File file = new File(filePath);
            if (file.exists()) {
                String newFileName = partToAdd + file.getName();
                File newFile = new File(file.getParent(), newFileName);
                file.renameTo(newFile);
            }
        }
    }
}

