package com.il.util.setvices;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.il.util.except.NoFileChosen;
import com.il.util.ui.MainFrameWrap;
import com.il.util.ui.components.ErrorDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.runAsync;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class UIService {
    @Autowired
    private MainFrameWrap mainFrameWrap;

    public void begin() {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());

            UIManager.put("SplitPaneDivider.style", "plain");
            Color customColor = new Color(84, 84, 85);
            UIManager.put("SplitPane.background", customColor);
            UIManager.put("SplitPane.dividerSize", 1);

            printUIParams();

        } catch (Throwable e) {
            showErrDialog(new RuntimeException("Failed to initialize LaF", e));
        }
        mainFrameWrap.showFrame();
    }

    private void printUIParams() {
        List<String> res = new ArrayList<>();
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            res.add(key + ": " + UIManager.get(key));
        }
        List<String> splitPaneDivider = res.stream()
            .filter(key -> key.contains("border"))
            .collect(Collectors.toList());
        splitPaneDivider.forEach(System.out::println);
    }

    public void showErrDialog(Throwable throwable) {
        runAsync(() -> {
            throwable.printStackTrace();
            new ErrorDialog(throwable);
        });
    }

    public String chooseFolder() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose a folder");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = jfc.showDialog(null, "Select");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            throw new NoFileChosen();
        }
    }


}
