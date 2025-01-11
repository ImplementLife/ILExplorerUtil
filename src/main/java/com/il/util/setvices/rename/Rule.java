package com.il.util.setvices.rename;

import java.util.List;

public interface Rule {
    List<String> getPreview(List<String> files);
    void doRename(List<String> files);
}
