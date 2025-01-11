package com.il.util.setvices.rename.rule;

import com.il.util.setvices.rename.RenameInfo;

import java.util.List;

public class AddPartRule implements Rule {
    private final String partToAdd;

    public AddPartRule(String partToAdd) {
        this.partToAdd = partToAdd;
    }

    @Override
    public List<RenameInfo> getRenameInfo(List<RenameInfo> renameInfoList) {
        for (RenameInfo renameInfo : renameInfoList) {
            renameInfo.setNewName(partToAdd + renameInfo.getNewName());
        }
        return renameInfoList;
    }
}

