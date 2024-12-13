package il.util.explorer.setvices;

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
}
