package il.util.explorer.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import il.util.explorer.dto.FileInfo;
import il.util.explorer.setvices.ScannerService;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static il.util.explorer.setvices.Util.bytesToMegabytes;

public class ScannerTab {
    private JTree tree;
    private JPanel root;
    private int maxMb = 100;

    public void setMaxMb(int maxMb) {
        this.maxMb = maxMb;
    }

    private static class TreeNodeFileInfo {
        private FileInfo fi;

        public TreeNodeFileInfo(FileInfo fi) {
            this.fi = fi;
        }

        @Override
        public String toString() {
            return fi.getName() + ": " + bytesToMegabytes(fi.getSize());
        }
    }

    public void fill(FileInfo rootFI) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("{root}");

        fillTree(rootFI, rootNode);
        tree.setModel(new DefaultTreeModel(rootNode, false));
        addContextMenu();
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
                    if (bytesToMegabytes(scan.getSize()) > maxMb) {
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
                if (bytesToMegabytes(child.getSize()) > maxMb) {
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
        root.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.setForeground(new Color(-12828863));
        tree = new JTree();
        tree.setEditable(true);
        root.add(tree, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
