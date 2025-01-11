package com.il.util.setvices;

import java.util.Locale;

public abstract class Util {
    private Util() {}
    public static String bytesToMegabytesString(long bytes) {
        long kilobytes = (bytes / 1024);
        long megabytes = (kilobytes / 1024);
        return String.format(Locale.US, "%,d", megabytes).replace(",", " ");
    }
    public static long bytesToMegabytes(long bytes) {
        long kilobytes = (bytes / 1024);
        long megabytes = (kilobytes / 1024);
        return megabytes;
    }
    public static void threadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static String getFileSeparator() {
        return FILE_SEPARATOR;
    }

    public static String replaceSeparators(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        return path.replace("/", FILE_SEPARATOR).replace("\\", FILE_SEPARATOR);
    }
}
