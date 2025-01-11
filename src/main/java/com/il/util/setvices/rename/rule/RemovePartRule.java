package com.il.util.setvices.rename.rule;

import com.il.util.setvices.rename.RenameInfo;

import java.util.List;

public class RemovePartRule implements Rule {
    private final String partToRemove;

    public RemovePartRule(String partToRemove) {
        this.partToRemove = partToRemove;
    }

    @Override
    public List<RenameInfo> getRenameInfo(List<RenameInfo> renameInfoList) {
        for (RenameInfo renameInfo : renameInfoList) {
            renameInfo.setNewName(renameInfo.getNewName().replace(partToRemove, ""));
        }
        return renameInfoList;
    }
}
