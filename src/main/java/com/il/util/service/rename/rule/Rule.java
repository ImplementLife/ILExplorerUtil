package com.il.util.service.rename.rule;

import com.il.util.service.rename.RenameInfo;

import java.util.List;

public interface Rule {
    List<RenameInfo> getRenameInfo(List<RenameInfo> renameInfoList);
}
