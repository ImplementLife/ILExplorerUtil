package com.il.util.setvices.rename;

import java.util.List;

public class IgnoreRule implements Rule {

    @Override
    public List<String> getPreview(List<String> files) {
        return null;
    }

    @Override
    public void doRename(List<String> files) {

    }
}
