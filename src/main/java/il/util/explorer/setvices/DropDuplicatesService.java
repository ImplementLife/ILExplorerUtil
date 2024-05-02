package il.util.explorer.setvices;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DropDuplicatesService {

    public void findDuplicates(List<String> baseFolders) {
        List<File> baseFoldersAsFiles = baseFolders.stream().map(File::new).collect(Collectors.toList());

    }

    public void dropDuplicates(String rootFolder, String cleanupFolder) {
        File root = new File(rootFolder);
        File cleanup = new File(cleanupFolder);

        File[] rootFiles = root.listFiles();
        File[] cleanupFiles = cleanup.listFiles();

        List<String> rootFilesNames = Arrays.stream(rootFiles).map(File::getName).collect(Collectors.toList());

        int countDuplicates = 0;
        for (File file : cleanupFiles) {
            String name = file.getName();
            if (rootFilesNames.contains(name)) {
                countDuplicates++;
                file.renameTo(new File("C:\\Users\\ImplementLife\\Desktop\\\u0421 \u041D\u043E\u0443\u0442\u0430\\from telephone\\temp\\" + name));
            }
        }
        System.out.println("countDuplicates = " + countDuplicates);
    }
}
