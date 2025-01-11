package com.il.util.setvices.rename;

import lombok.Data;

@Data
public class RenameInfo {
    private String oldName;
    private String newName;
    private boolean ignore;
}
