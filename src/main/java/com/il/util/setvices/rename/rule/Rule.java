package com.il.util.setvices.rename.rule;

import com.il.util.setvices.rename.RenameInfo;

import java.util.List;

public interface Rule {
    List<RenameInfo> getRenameInfo(List<RenameInfo> renameInfoList);
}
