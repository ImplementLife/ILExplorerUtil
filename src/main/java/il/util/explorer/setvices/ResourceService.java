package il.util.explorer.setvices;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Service
public class ResourceService {
    public Image loadImage(String name) {
        Image image = null;
        InputStream in = null;
        try {
            File file = ResourceUtils.getFile("classpath:" + name);
            in = new FileInputStream(file);
            image = ImageIO.read(in);
        } catch (Exception e) {
            String executeJarFilePath = ManagementFactory.getRuntimeMXBean().getClassPath();
            try (JarFile jarFile = new JarFile(executeJarFilePath)) {
                JarEntry entry = jarFile.getJarEntry(name);
                in = jarFile.getInputStream(entry);
                image = ImageIO.read(in);
            } catch (Exception ignore) {}
        } finally {
            close(in);
        }
        return image;
    }

    private void doWithInputStream(String name, Consumer<InputStream> action) {
        InputStream in = null;
        try {
            File file = ResourceUtils.getFile("classpath:" + name);
            in = new FileInputStream(file);
            action.accept(in);
        } catch (Exception e) {
            String executeJarFilePath = ManagementFactory.getRuntimeMXBean().getClassPath();
            try (JarFile jarFile = new JarFile(executeJarFilePath)) {
                JarEntry entry = jarFile.getJarEntry(name);
                in = jarFile.getInputStream(entry);
                action.accept(in);
            } catch (Exception ignore) {}
        } finally {
            close(in);
        }
    }

    private void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) { }
        }
    }
}
