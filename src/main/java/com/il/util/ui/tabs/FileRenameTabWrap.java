package com.il.util.ui.tabs;

import com.il.util.setvices.UIService;
import com.il.util.setvices.Util;
import com.il.util.setvices.rename.FileRenameService;
import com.il.util.setvices.rename.Req;
import com.il.util.setvices.rename.Res;
import com.il.util.setvices.rename.rule.AddPartRule;
import com.il.util.setvices.rename.rule.RemovePartRule;
import com.il.util.setvices.rename.rule.Rule;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

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
    private JPanel pActual;
    private JPanel pExpect;
    private JTextArea taA;
    private JTextArea taE;
    private JButton btnConfirm;

//    private JTextField textPath;
//    private JTextField textTemplate;
//    private JTextArea textTempFilesPreview;
//    private JButton btnSelect;
//    private JButton btnDoRename;
//    private JComboBox<CBTemplates> templateComboBox;


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
                showPreview();
            } catch (Exception ignore) {
            }
        });

        btnChooseOutDir.addActionListener(e -> {
            try {
                tfOutDir.setText(uiService.chooseFolder());
            } catch (Exception ignore) {
            }
        });

        btnConfirm.addActionListener(e -> {
            doRename();
        });
    }

    private void doRename() {
        Req req = new Req();
        req.setPathSource(tfSourceDir.getText());
        req.setPathOut(tfOutDir.getText());

        List<Rule> rules = new ArrayList<>();
        rules.add(new RemovePartRule("_opsz48"));
        rules.add(new RemovePartRule("_GRAD0"));
        rules.add(new RemovePartRule("_wght400"));
        rules.add(new RemovePartRule("_FILL0"));

        rules.add(new AddPartRule("ic_"));
        req.setRules(rules);

        fileRenameService.doRename(req);
    }

    private void showPreview() {
        Req req = new Req();
        req.setPathSource(tfSourceDir.getText());
        req.setPathOut(tfOutDir.getText());

        List<Rule> rules = new ArrayList<>();
        rules.add(new RemovePartRule("_opsz48"));
        rules.add(new RemovePartRule("_GRAD0"));
        rules.add(new RemovePartRule("_wght400"));
        rules.add(new RemovePartRule("_FILL0"));
        rules.add(new AddPartRule("ic_"));
        req.setRules(rules);

        Res preview = fileRenameService.getPreview(req);
        taA.setText(Strings.join(preview.getActual(), '\n'));
        taE.setText(Strings.join(preview.getExpect(), '\n'));
    }

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
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
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
        pActual = new JPanel();
        pActual.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        spWrapper.setLeftComponent(pActual);
        taA = new JTextArea();
        taA.setEditable(false);
        pActual.add(taA, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        pActual.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Actual");
        panel6.add(label3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel6.add(spacer3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel6.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(4, 4), null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel6.add(spacer5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(4, 4), null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel6.add(spacer6, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(4, 4), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        panel7.setBackground(new Color(-11053225));
        panel5.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 2), new Dimension(-1, 2), new Dimension(-1, 2), 0, false));
        pExpect = new JPanel();
        pExpect.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        spWrapper.setRightComponent(pExpect);
        taE = new JTextArea();
        taE.setEditable(false);
        pExpect.add(taE, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        pExpect.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Expect");
        panel9.add(label4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel9.add(spacer7, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel9.add(spacer8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(4, 4), null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel9.add(spacer9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(4, 4), null, 0, false));
        final Spacer spacer10 = new Spacer();
        panel9.add(spacer10, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(4, 4), null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new BorderLayout(0, 0));
        panel10.setBackground(new Color(-11053225));
        panel8.add(panel10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 2), new Dimension(-1, 2), new Dimension(-1, 2), 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel11, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnConfirm = new JButton();
        btnConfirm.setText("Confirm");
        panel11.add(btnConfirm, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer11 = new Spacer();
        panel11.add(spacer11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer12 = new Spacer();
        root.add(spacer12, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 4), new Dimension(-1, 4), new Dimension(-1, 4), 0, false));
        final Spacer spacer13 = new Spacer();
        root.add(spacer13, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 4), null, 0, false));
        final Spacer spacer14 = new Spacer();
        root.add(spacer14, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, 1, null, new Dimension(4, -1), null, 0, false));
        final Spacer spacer15 = new Spacer();
        root.add(spacer15, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(4, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
