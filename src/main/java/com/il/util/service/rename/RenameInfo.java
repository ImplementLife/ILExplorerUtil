package com.il.util.service.rename;

import lombok.Data;

@Data
public class RenameInfo {
    private String oldName;
    private String newName;
    private boolean ignore;
}
