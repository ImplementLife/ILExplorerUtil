package com.il.util.ui.tabs;

import com.il.util.setvices.FileRenameService;
import com.il.util.setvices.UIService;
import com.il.util.setvices.Util;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FileRenameTabWrap {
    @Autowired
    private UIService uiService;
    @Autowired
    private FileRenameService fileRenameService;

    private JPanel root;

    private JTextField tfSourceDir;
    private JButton btnChooseSourceDir;

    private JTextField tfOutDir;
    private JButton btnChooseOutDir;

    private JSplitPane spWrapper;
    private JScrollPane sc;

//    private JTextField textPath;
//    private JTextField textTemplate;
//    private JTextArea textTempFilesPreview;
//    private JButton btnSelect;
//    private JButton btnDoRename;
//    private JComboBox<CBTemplates> templateComboBox;
//    private JScrollPane spActual;
//    private JScrollPane spPreview;


    private enum CBTemplates {
        T1("enum 1  2  3  ... 9  10"),
        T2("enum 01 02 03 ... 09 10"),
        T3("by date"),
        ;

        private final String name;

        CBTemplates(String s) {
            name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @PostConstruct
    private void init() {
        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                rootResized();
            }
        });

        sc.getVerticalScrollBar().setUnitIncrement(16);
//        for (CBTemplates value : CBTemplates.values()) {
//            templateComboBox.addItem(value);
//        }


//        btnSelect.addActionListener(event -> {
//            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//            jfc.setDialogTitle("Choose a folder");
//            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//
//            int returnValue = jfc.showDialog(null, "Select");
//            if (returnValue == JFileChooser.APPROVE_OPTION) {
//                File selectedFile = jfc.getSelectedFile();
//                textPath.setText(selectedFile.getAbsolutePath());
//                showPreview();
//            }
//        });
//        btnDoRename.addActionListener(event -> {
//
//        });


        btnChooseSourceDir.addActionListener(e -> {
            try {
                String folder = uiService.chooseFolder();
                tfSourceDir.setText(folder);
                tfOutDir.setText(folder + Util.getFileSeparator() + "out");
            } catch (Exception ignore) {
            }
        });

        btnChooseOutDir.addActionListener(e -> {
            try {
                tfOutDir.setText(uiService.chooseFolder());
            } catch (Exception ignore) {
            }
        });
    }

//    private void showPreview() {
//        List<String> preview = fileRenameService.getPreview(textPath.getText());
//        StringBuilder builder = new StringBuilder();
//        for (String s : preview) {
//            builder.append(s).append('\n');
//        }
//        textTempFilesPreview.setText(builder.toString());
//    }

    private void rootResized() {
        {
            double width = spWrapper.getSize().getWidth() - 4; // 4 is the divider size
            double dividerLocation = width / 2;
            spWrapper.setDividerLocation((int) dividerLocation);
        }
    }

    public JPanel getRoot() {
        return root;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tfSourceDir = new JTextField();
        panel3.add(tfSourceDir, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnChooseSourceDir = new JButton();
        btnChooseSourceDir.setText("Choose");
        panel3.add(btnChooseSourceDir, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Source Dir");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tfOutDir = new JTextField();
        panel4.add(tfOutDir, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnChooseOutDir = new JButton();
        btnChooseOutDir.setText("Choose");
        panel4.add(btnChooseOutDir, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Out Dir");
        panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        sc = new JScrollPane();
        panel1.add(sc, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 200), null, null, 0, false));
        spWrapper = new JSplitPane();
        spWrapper.setDividerLocation(200);
        spWrapper.setDividerSize(4);
        sc.setViewportView(spWrapper);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        spWrapper.setLeftComponent(panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        spWrapper.setRightComponent(panel6);
        final Spacer spacer3 = new Spacer();
        root.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 4), new Dimension(-1, 4), new Dimension(-1, 4), 0, false));
        final Spacer spacer4 = new Spacer();
        root.add(spacer4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 4), null, 0, false));
        final Spacer spacer5 = new Spacer();
        root.add(spacer5, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, 1, null, new Dimension(4, -1), null, 0, false));
        final Spacer spacer6 = new Spacer();
        root.add(spacer6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(4, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
