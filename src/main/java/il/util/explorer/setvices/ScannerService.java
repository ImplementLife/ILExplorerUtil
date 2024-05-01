package il.util.explorer.setvices;

import il.util.explorer.dto.FI;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScannerService {
    public FI scan(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("Path [" + path + "] is not valid");
        }

        System.out.println("Scan for: " + path);
        FI treeFI = getTreeFI(file);
        treeFI.setName(file.getName());
        System.out.println("Done");
        return treeFI;
    }

    private FI getTreeFI(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return null;

            long size = 0;
            FI fi = new FI(file.getAbsolutePath(), file.getName());

            for (File childFile : files) {
                FI child = getTreeFI(childFile);
                if (child == null) continue;

                long childSize = child.getSize();
//                long childMegabytes = bytesToMegabytes(childSize);
                size += childSize;
//                if (childMegabytes > 100) {
                    fi.addChild(child);
//                }
            }

//            long megabytes = bytesToMegabytes(size);
//            if (megabytes > 100) {
                fi.setSize(size);
                List<FI> children = fi.getChildren();
                if (children != null) {
                    List<FI> sortedChildren = children.stream()
                        .sorted(Comparator.comparing(FI::getSize).reversed())
                        .collect(Collectors.toList());
                    fi.setChildren(sortedChildren);
                }
//            }

            return fi;
        } else {
            return new FI(file.getAbsolutePath(), file.getName(), file.length());
        }
    }
}
