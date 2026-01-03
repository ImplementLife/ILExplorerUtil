package com.il.util.rnd;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import lombok.Data;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TestMeta {
    @Data
    private static class MetaContainer {
        private File file;
        private Metadata metadata;

        private String fName;
        private String metaTime;
    }
    @Test
    public void t1() {
        String path = "D:\\..il-archive\\СНК\\root\\сама не скромность";
        File dir = new File(path);

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmm");

        File[] files = dir.listFiles();
        List<MetaContainer> metaContainers = new ArrayList<>(files.length);
        for (File file : files) {
            try {
                Metadata metadata = ImageMetadataReader.readMetadata(file);
                MetaContainer metaContainer = new MetaContainer();
                metaContainer.setFile(file);
                metaContainer.setMetadata(metadata);

                metaContainer.setFName(file.getName());


                Date date = null;

                ExifSubIFDDirectory exif = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                if (exif != null) date = exif.getDateOriginal();

                if (date == null) {
                    QuickTimeDirectory qt = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
                    if (qt != null) date = qt.getDate(QuickTimeDirectory.TAG_CREATION_TIME);
                }

                if (date == null) {
                    Mp4Directory mp4 = metadata.getFirstDirectoryOfType(Mp4Directory.class);
                    if (mp4 != null) date = mp4.getDate(Mp4Directory.TAG_CREATION_TIME);
                }

                if (date == null) {
                    FileSystemDirectory fs = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
                    if (fs != null) date = fs.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
                }

                String metaTime = date != null ? new SimpleDateFormat("ddMMyyyy_HHmmss").format(date) : "unknown";
                metaContainer.setMetaTime(metaTime);

                metaContainers.add(metaContainer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("done");
    }

    @Test
    public void t2() {
        File file = new File("C:\\Users");
        List<File> files = Arrays.stream(file.listFiles())
            .filter(f -> !Files.isSymbolicLink(f.toPath()))
            .collect(Collectors.toList());
        System.out.println(files.size());
    }
}
