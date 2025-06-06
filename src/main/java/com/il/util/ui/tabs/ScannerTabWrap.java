package com.il.util.ui.tabs;

import com.il.util.dto.FileInfo;
import com.il.util.setvices.ScannerService;
import com.il.util.setvices.UIService;
import com.il.util.setvices.Util;
import com.il.util.ui.components.ProgressWindowWrap;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ScannerTabWrap {
    @Autowired
    private ProgressWindowWrap progressWindow;
    @Autowired
    private UIService uiService;
    @Autowired
    private ScannerService scannerService;

    private JTree tree;
    private JPanel root;
    private JScrollPane sc;
    private JButton btnScan;
    private JTextField textStartPath;
    private JTextField textMaxMb;
    private JButton btnChoose;
    private int maxMb = 100;

    @PostConstruct
    private void init() {
        scannerService.addProgressListener(progressWindow::updateProgress);
        progressWindow.setOnCancelAction(() -> {
            scannerService.cancelCurrentScan();
            progressWindow.setVisible(false);
        });
        tree.setFocusable(false);
        tree.setVisible(false);
        sc.getVerticalScrollBar().setUnitIncrement(16);

        btnScan.addActionListener(event -> {
            if (!scannerService.isInProcess()) {
                CompletableFuture.runAsync(() -> {
                    String path = textStartPath.getText();
                    File file = new File(path);
                    if (file.exists() && file.isDirectory()) {
                        try {
                            maxMb = Integer.parseInt(textMaxMb.getText());
                        } catch (NumberFormatException e) {
                            uiService.showErrDialog(e);
                        }
                        progressWindow.setVisible(true);
                        FileInfo scan = scannerService.scan(path);

                        fill(scan);

                        progressWindow.setVisible(false);
                    } else {
                        uiService.showErrDialog(new IllegalArgumentException(String.format("%s is not valid", path)));
                        log.info("Path doesn't valid!");
                    }
                });
            }
        });
        btnChoose.addActionListener(event -> {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Choose a folder");
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnValue = jfc.showDialog(null, "Select");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                textStartPath.setText(selectedFile.getAbsolutePath());
            }
        });
    }

    private static class TreeNodeFileInfo {
        private final FileInfo fi;

        public TreeNodeFileInfo(FileInfo fi) {
            this.fi = fi;
        }

        @Override
        public String toString() {
            return fi.getName() + ": " + Util.formatNumberWithSpaces(String.valueOf(Util.bytesToMegabytes(fi.getSize())));
        }
    }

    public void fill(FileInfo rootFI) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("{root}");

        fillTree(rootFI, rootNode);
        tree.setVisible(true);
        tree.setModel(new DefaultTreeModel(rootNode, false));
        addContextMenu();
        tree.updateUI();
    }

    private void addContextMenu() {
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getRowForLocation(e.getX(), e.getY());
                    if (row != -1) {
                        tree.setSelectionRow(row);
                        JPopupMenu menu = new JPopupMenu();

                        JMenuItem showInExplorerItem = new JMenuItem("Show in Explorer");
                        setShowInExplorerAction(showInExplorerItem);
                        menu.add(showInExplorerItem);

                        JMenuItem refresh = new JMenuItem("Refresh");
                        setRefreshAction(refresh);
                        menu.add(refresh);

                        menu.addSeparator();
                        JMenuItem delete = new JMenuItem("Delete");
                        setDeleteAction(delete);
                        menu.add(delete);

                        menu.show(tree, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void setDeleteAction(JMenuItem item) {
        item.addActionListener(e1 -> {
            int result = JOptionPane.showConfirmDialog(root, "Do you Confirm DELETE?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    TreeNodeFileInfo node = (TreeNodeFileInfo) selectedNode.getUserObject();
                    String nodePath = node.fi.getPath();
                    if (nodePath != null) {
                        File fileToDelete = new File(nodePath);
                        if (fileToDelete.isDirectory()) {
                            File[] files = fileToDelete.listFiles();
                            if (files != null && files.length > 0) {
                                delete(files);
                            }
                        }
                        boolean delete = fileToDelete.delete();

                        if (delete) {
                            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
                            parent.remove(selectedNode);
                            selectedNode.setParent(null);
                            tree.updateUI();
                        }
                    }
                }
            }
        });
    }

    private void delete(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                File[] files1 = file.listFiles();
                if (files1 != null && files1.length > 0) {
                    delete(files1);
                }
            } else {
                file.delete();
            }
        }
    }

    private void setShowInExplorerAction(JMenuItem item) {
        item.addActionListener(e1 -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                TreeNodeFileInfo node = (TreeNodeFileInfo) selectedNode.getUserObject();
                String nodePath = node.fi.getPath();
                if (nodePath != null) {
                    try {
                        Runtime.getRuntime().exec("explorer.exe /select," + nodePath);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void setRefreshAction(JMenuItem item) {
        item.addActionListener(e1 -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                TreeNodeFileInfo node = (TreeNodeFileInfo) selectedNode.getUserObject();
                String nodePath = node.fi.getPath();

                File file = new File(nodePath);
                if (file.exists()) {
                    selectedNode.removeAllChildren();
                    ScannerService scannerService = new ScannerService();
                    FileInfo scan = scannerService.scan(nodePath);
                    if (Util.bytesToMegabytes(scan.getSize()) > maxMb) {
                        fillTree(scan, selectedNode);
                        tree.updateUI();
                    }
                } else {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
                    parent.remove(selectedNode);
                    selectedNode.setParent(null);
                    tree.updateUI();
                }
            }
        });
    }

    private void fillTree(FileInfo rootFI, DefaultMutableTreeNode rootNode) {
        rootNode.setUserObject(new TreeNodeFileInfo(rootFI));

        List<FileInfo> children = rootFI.getChildren();
        if (children != null && children.size() > 0) {
            for (FileInfo child : children) {
                if (Util.bytesToMegabytes(child.getSize()) > maxMb) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TreeNodeFileInfo(child));
                    rootNode.add(node);
                    fillTree(child, node);
                }
            }
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
        root.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.setForeground(new Color(-12828863));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 8, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        btnScan = new JButton();
        btnScan.setText("Scan");
        panel1.add(btnScan, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textStartPath = new JTextField();
        textStartPath.setText("C:\\");
        panel1.add(textStartPath, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textMaxMb = new JTextField();
        textMaxMb.setText("100");
        panel1.add(textMaxMb, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(10, -1), new Dimension(10, -1), new Dimension(10, -1), 0, false));
        btnChoose = new JButton();
        btnChoose.setText("Choose");
        panel1.add(btnChoose, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sc = new JScrollPane();
        sc.setForeground(new Color(-12828863));
        root.add(sc, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tree = new JTree();
        tree.setEditable(false);
        tree.setRootVisible(true);
        sc.setViewportView(tree);
        final Spacer spacer5 = new Spacer();
        root.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 5), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
