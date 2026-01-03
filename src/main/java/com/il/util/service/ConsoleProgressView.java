package com.il.util.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleProgressView implements ScannerService.ProgressView {
    public void notify(ProgressInfo info) {
        int width = 50; // Width of the progress bar

        // Calculate number of characters representing progress
        int progressChars = (int) (info.getProgress() * width);

        // Draw progress bar
        StringBuilder progressBar = new StringBuilder();
        progressBar.append("[");
        for (int i = 0; i < width; i++) {
            if (i < progressChars) {
                progressBar.append("=");
            } else {
                progressBar.append(" ");
            }
        }
        progressBar.append("] ");
        progressBar.append(String.format("%.2f", info.getProgress() * 100)).append("% ");

        log.info(progressBar.toString());
        log.info(info.getForkJoinPoolStatus());
        log.info("Progress: {} files processed", info.getFilesProcessed());
        log.info(String.format("Time %.2f s\n", info.getTime()));
    }
}
