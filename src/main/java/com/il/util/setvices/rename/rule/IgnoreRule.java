package com.il.util.setvices.rename.rule;

import com.il.util.setvices.rename.RenameInfo;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class IgnoreRule implements Rule {
    private final List<String> patterns = Arrays.asList("\\.png$", "\\.svg$", "\\.jpg$");

    @Override
    public List<RenameInfo> getRenameInfo(List<RenameInfo> renameInfoList) {
        for (RenameInfo renameInfo : renameInfoList) {
            for (String pattern : patterns) {
                if (Pattern.matches(pattern, renameInfo.getOldName())) {
                    renameInfo.setIgnore(true);
                    break;
                }
            }
        }
        return renameInfoList;
    }
}
