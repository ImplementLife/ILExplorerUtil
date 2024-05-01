package il.util.explorer.dto;

import java.util.ArrayList;
import java.util.List;

public class FI {
    private String name;
    private final String path;
    private long size;
    private List<FI> children;

    public FI(String path, long size) {
        this.path = path;
        this.size = size;
    }
    public FI(String path, String name, long size) {
        this.path = path;
        this.name = name;
        this.size = size;
    }
    public FI(String path) {
        this.path = path;
    }
    public FI(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    public List<FI> getChildren() {
        return children;
    }

    public void setChildren(List<FI> children) {
        this.children = children;
    }

    public void addChild(FI child) {
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
