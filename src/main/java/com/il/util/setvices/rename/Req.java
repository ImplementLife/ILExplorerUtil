package com.il.util.setvices.rename;

import com.il.util.setvices.rename.rule.Rule;
import lombok.Data;

import java.util.List;

@Data
public class Req {
    private String pathSource;
    private String pathOut;
    private List<Rule> rules;

}
