package com.il.util.setvices.rename.rule;

import com.il.util.setvices.rename.RenameInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NumByExtRule implements Rule {

    @Override
    public List<RenameInfo> getRenameInfo(List<RenameInfo> renameInfoList) {
        return renameInfoList.stream()
            .collect(Collectors.groupingBy(renameInfo -> getFileExtension(renameInfo.getNewName())))
            .entrySet().stream()
            .flatMap(entry -> {
                List<RenameInfo> renamedFiles = renameFiles(entry.getKey(), entry.getValue());
                return renamedFiles.stream();
            })
            .collect(Collectors.toList());
    }

    private String getFileExtension(String filePath) {
        int lastIndexOfDot = filePath.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // Якщо немає розширення
        }
        return filePath.substring(lastIndexOfDot + 1).toLowerCase();
    }

    private List<RenameInfo> renameFiles(String extension, List<RenameInfo> renameInfos) {
        List<RenameInfo> renamedFiles = new ArrayList<>();
        for (int i = 0; i < renameInfos.size(); i++) {
            RenameInfo renameInfo = renameInfos.get(i);
            renameInfo.setNewName((i + 1) + "." + extension);
            renamedFiles.add(renameInfo);
        }
        return renamedFiles;
    }
}
