package com.il.util.service.rename;

import lombok.Data;

import java.util.List;

@Data
public class Res {
    private List<String> actual;
    private List<String> expect;
    private List<RenameInfo> renameInfoList;
}
