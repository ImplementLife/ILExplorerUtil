package il.util.explorer.setvices;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FileRenameService {

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
}
