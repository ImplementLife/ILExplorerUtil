package com.il.util.setvices.rename;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileSystemDirectory;
import com.il.util.setvices.rename.rule.Rule;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FileRenameService {
    private final SimpleDateFormat format = new SimpleDateFormat();

    private List<File> lazyLoadListFiles(File path, Collection<String> extensions) {
        ArrayList<File> files = new ArrayList<>();
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isFile()) {
                    String str = f.getName();
                    for (String exp : extensions) {
                        if (str.substring(str.lastIndexOf('.')).equals(exp)) {
                            files.add(f);
                        }
                    }
                }
            }
        }
        return files;
    }

    private Res process(Req req) {
        File file = new File(req.getPathSource());
        File[] files = file.listFiles((FilenameFilter) FileFileFilter.INSTANCE);
        if (!file.exists() || file.isFile() || files == null || files.length == 0) {
            throw new IllegalArgumentException(String.format("Folder with name: [%s] doesn't exists", req.getPathSource()));
        }
        List<String> filesNames = Arrays.stream(files)
            .map(e -> e.getName())
            .collect(Collectors.toList());
        Res res = new Res();
        res.setActual(filesNames);

        List<Rule> rules = req.getRules();
        List<RenameInfo> result = filesNames.stream()
            .map(e -> {
                RenameInfo renameInfo = new RenameInfo();
                renameInfo.setOldName(e);
                renameInfo.setNewName(e);
                return renameInfo;
            })
            .collect(Collectors.toList());

        for (Rule rule : rules) {
            result = rule.getRenameInfo(result);
        }

        res.setExpect(result.stream().map(e -> e.getNewName()).collect(Collectors.toList()));
        res.setRenameInfoList(result);
        return res;
    }

    public void doRename(Req req) {
        String pathSource = req.getPathSource();
        String pathOut = req.getPathOut();
        Res process = process(req);

        List<RenameInfo> renameInfoList = process.getRenameInfoList();

        File outDir = new File(pathOut);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        for (RenameInfo renameInfo : renameInfoList) {
            if (!renameInfo.isIgnore()) {
                File oldFile = new File(pathSource, renameInfo.getOldName());
                File newFile = new File(pathOut, renameInfo.getNewName());

                try {
                    Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Res getPreview(Req req) {
        return process(req);
    }

    private List<String> getPreview(String path) {
        File file = new File(path);
        File[] files = file.listFiles((FilenameFilter) FileFileFilter.INSTANCE);
        if (!file.exists() || file.isFile() || files == null || files.length == 0) {
            throw new IllegalArgumentException(String.format("Folder with name: [%s] doesn't exists", path));
        }

        List<String> result = new ArrayList<>();
        int countZeros = (int) Math.log10(files.length) + 1;
        int c = 0;
        for (File fileToRename : files) {
            c++;
            String name = fileToRename.getName();
            String ext = name.substring(name.indexOf('.'));
            Optional<Date> gm = gm(fileToRename.getAbsolutePath());
            if (gm.isPresent()) {
                result.add(name + " -> " + format.format(gm.get()) + ext);
            } else {
                result.add(name + " -> " + String.format("%0" + countZeros + "d", c) + ext);
            }
        }
        return result;
    }

    private Optional<Date> gm(String imagePath) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new File(imagePath));
            Date date = metadata.getFirstDirectoryOfType(FileSystemDirectory.class).getDate(3);
            return Optional.ofNullable(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
