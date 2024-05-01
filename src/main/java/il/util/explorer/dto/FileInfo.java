package il.util.explorer.dto;

import java.util.ArrayList;
import java.util.List;

public class FileInfo {
    private String name;
    private final String path;
    private long size;
    private List<FileInfo> children;

    public FileInfo(String path, long size) {
        this.path = path;
        this.size = size;
    }
    public FileInfo(String path, String name, long size) {
        this.path = path;
        this.name = name;
        this.size = size;
    }
    public FileInfo(String path) {
        this.path = path;
    }
    public FileInfo(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    public List<FileInfo> getChildren() {
        return children;
    }

    public void setChildren(List<FileInfo> children) {
        this.children = children;
    }

    public void addChild(FileInfo child) {
        if (children == null) children = new ArrayList<>();
        children.add(child);
    }

    public void setSize(long megabytes) {
        this.size = megabytes;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
