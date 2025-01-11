package com.il.util.setvices.rename;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class RemovePartRule implements Rule {
    private final String partToRemove;

    public RemovePartRule(String partToRemove) {
        this.partToRemove = partToRemove;
    }

    @Override
    public List<String> getPreview(List<String> files) {
        return files.stream()
            .map(fileName -> fileName.replace(partToRemove, ""))
            .collect(Collectors.toList());
    }

    @Override
    public void doRename(List<String> files) {
        for (String filePath : files) {
            File file = new File(filePath);
            if (file.exists()) {
                String newFileName = file.getName().replace(partToRemove, "");
                File newFile = new File(file.getParent(), newFileName);
                file.renameTo(newFile);
            }
        }
    }
}

