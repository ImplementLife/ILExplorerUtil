package il.util.explorer.setvices;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileSystemDirectory;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public Set<String> getAllExtensions() {
        if (true) throw new UnsupportedOperationException();
        return null;
    }

    public void doRename(String path) {
        List<String> extensions = new ArrayList<>();
        extensions.add(".png");
        extensions.add(".jpg");
        extensions.add(".jpeg");

        List<File> files = lazyLoadListFiles(
            new File(path), extensions
        );

        for (int i = 0; i < files.size(); i++) {
            String currentNameThisFile = files.get(i).getName();
            String expansionThisFile = currentNameThisFile.substring(currentNameThisFile.lastIndexOf('.'));
            String pathThisFile = path + "\\" + i + expansionThisFile;
            files.get(i).renameTo(new File(pathThisFile));
        }
    }

    public List<String> getPreview(String path) {
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
