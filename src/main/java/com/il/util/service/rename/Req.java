package com.il.util.service.rename;

import com.il.util.service.rename.rule.Rule;
import lombok.Data;

import java.util.List;

@Data
public class Req {
    private String pathSource;
    private String pathOut;
    private List<Rule> rules;

}
