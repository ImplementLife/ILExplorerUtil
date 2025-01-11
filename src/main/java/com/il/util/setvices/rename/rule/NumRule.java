package com.il.util.setvices.rename.rule;

import com.il.util.setvices.rename.RenameInfo;

import java.util.List;

public class NumRule implements Rule {
    private String getNewFileName(String fileName, int index) {
        String extension = getFileExtension(fileName);
        String baseName = "file_" + index;
        return extension.isEmpty() ? baseName : baseName + "." + extension;
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    @Override
    public List<RenameInfo> getRenameInfo(List<RenameInfo> renameInfoList) {
        for (int i = 0; i < renameInfoList.size(); i++) {
            RenameInfo renameInfo = renameInfoList.get(i);
            String newFileName = getNewFileName(renameInfo.getNewName(), i + 1);
            renameInfo.setNewName(newFileName);
        }
        return renameInfoList;
    }
}

