package GUI;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.tree.*;

import gmdr.Main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class FileJTree {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame=new JFrame();
                FileTree fileTree=new FileTree();
                FileTreeModel model=new FileTreeModel(new DefaultMutableTreeNode(new FileNode("root",null,null,true)));
                fileTree.setModel(model);
                fileTree.setCellRenderer(new FileTreeRenderer());
           //     frame.getContentPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                frame.getContentPane().add(new JScrollPane(fileTree), BorderLayout.CENTER);
                

                frame.setSize(300,700);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}

class FileTree extends JTree 
{
    public TreePath mouseInPath;
    protected FileSystemView fileSystemView = FileSystemView.getFileSystemView();
  
    public FileTree(){
        setRootVisible(false);
    }
    public FileSystemView getFileSystemView()
    {
		return fileSystemView;
	}
   
}


class FileNode{
    public FileNode(String name,Icon icon,File file,boolean isDummyRoot){
        this.name=name;this.icon=icon;this.file=file;this.isDummyRoot=isDummyRoot;
    }
    public boolean isInit;
    public boolean isDummyRoot;
    public String name;
    public Icon icon;
    public File file;
}
class FileTreeRenderer extends DefaultTreeCellRenderer{
    public FileTreeRenderer(){
    }
    @Override
    public Component getTreeCellRendererComponent(javax.swing.JTree tree,
                                                  java.lang.Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus){

        FileTree fileTree=(FileTree)tree;
        JLabel label= (JLabel) super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);

        DefaultMutableTreeNode node=(DefaultMutableTreeNode)value;
        FileNode fileNode=(FileNode)node.getUserObject();
        label.setText(fileNode.name);
        label.setIcon(fileNode.icon);

        label.setOpaque(false);
        if(fileTree.mouseInPath!=null&&
                fileTree.mouseInPath.getLastPathComponent().equals(value)){
            label.setOpaque(true);
            label.setBackground(new Color(255,0,0,90));
        }
        return label;
    }
}
class FileTreeModel extends DefaultTreeModel {
    public FileTreeModel(TreeNode root) {
        super(root);
         FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            File[] files=fileSystemView.getRoots();
        for (int i = 0; i < files.length; i++) {
            FileNode childFileNode = new FileNode(fileSystemView.getSystemDisplayName(files[i]), fileSystemView.getSystemIcon(files[i]), files[i], false);
            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childFileNode);
            ((DefaultMutableTreeNode)root).add(childTreeNode);
        }
    }
    public FileTreeModel(TreeNode root,String rootdir) {
        super(root);
         FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            File[] files=fileSystemView.getFiles(new File(rootdir), false);
        for (int i = 0; i < files.length; i++) {
            FileNode childFileNode = new FileNode(fileSystemView.getSystemDisplayName(files[i]), fileSystemView.getSystemIcon(files[i]), files[i], false);
            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childFileNode);
            ((DefaultMutableTreeNode)root).add(childTreeNode);
        }
    }
    @Override
    public boolean isLeaf(Object node) {
        DefaultMutableTreeNode treeNode=(DefaultMutableTreeNode)node;
        FileNode fileNode=(FileNode)treeNode.getUserObject();
        if(fileNode.isDummyRoot)return false;
        return fileNode.file.isFile();
    }
}