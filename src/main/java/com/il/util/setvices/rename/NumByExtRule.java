package com.il.util.setvices.rename;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NumByExtRule implements Rule {

    @Override
    public List<String> getPreview(List<String> files) {
        // Створюємо попередній перегляд, де файли перейменовані в порядкові номери по розширенню
        return files.stream()
            .collect(Collectors.groupingBy(file -> getFileExtension(file))) // Групуємо за розширенням
            .entrySet().stream()
            .flatMap(entry -> {
                List<String> renamedFiles = renameFiles(entry.getKey(), entry.getValue());
                return renamedFiles.stream();
            })
            .collect(Collectors.toList());
    }

    @Override
    public void doRename(List<String> files) {
        // Перейменовуємо файли, присвоюючи порядковий номер, групуючи по розширенню
        files.stream()
            .collect(Collectors.groupingBy(file -> getFileExtension(file))) // Групуємо за розширенням
            .forEach((extension, fileList) -> {
                List<String> renamedFiles = renameFiles(extension, fileList);
                for (int i = 0; i < renamedFiles.size(); i++) {
                    File file = new File(fileList.get(i));
                    File renamedFile = new File(file.getParent(), renamedFiles.get(i));
                    file.renameTo(renamedFile);
                }
            });
    }

    // Допоміжний метод для отримання розширення файлу
    private String getFileExtension(String filePath) {
        int lastIndexOfDot = filePath.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // Якщо немає розширення
        }
        return filePath.substring(lastIndexOfDot + 1).toLowerCase();
    }

    // Допоміжний метод для перейменування файлів з порядковими номерами
    private List<String> renameFiles(String extension, List<String> files) {
        List<String> renamedFiles = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String newName = (i + 1) + "." + extension; // Формуємо нове ім'я: порядковий номер + розширення
            renamedFiles.add(newName);
        }
        return renamedFiles;
    }
}
