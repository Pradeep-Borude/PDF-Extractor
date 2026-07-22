package com.pradeep.pdfextractor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of one batch run, returned by BatchProcessingService.
 * Pure data holder — no processing logic lives here.
 */
public class ProcessingSummary {

    private int totalPdfs;
    private int processed;
    private int duplicates;
    private int failed;

    private final List<FailedFile> failedFiles = new ArrayList<>();

    public int getTotalPdfs() {
        return totalPdfs;
    }

    public void setTotalPdfs(int totalPdfs) {
        this.totalPdfs = totalPdfs;
    }

    public void incrementTotalPdfs() {
        this.totalPdfs++;
    }

    public int getProcessed() {
        return processed;
    }

    public void incrementProcessed() {
        this.processed++;
    }

    public int getDuplicates() {
        return duplicates;
    }

    public void incrementDuplicates() {
        this.duplicates++;
    }

    public int getFailed() {
        return failed;
    }

    public void addFailedFile(String fileName, String reason) {
        this.failed++;
        this.failedFiles.add(new FailedFile(fileName, reason));
    }

    public List<FailedFile> getFailedFiles() {
        return Collections.unmodifiableList(failedFiles);
    }

    public boolean hasFailures() {
        return failed > 0;
    }

    @Override
    public String toString() {
        return "ProcessingSummary{" +
                "totalPdfs=" + totalPdfs +
                ", processed=" + processed +
                ", duplicates=" + duplicates +
                ", failed=" + failed +
                ", failedFiles=" + failedFiles +
                '}';
    }

    /**
     * One failed file entry: file name + human-readable reason.
     */
    public static final class FailedFile {

        private final String fileName;
        private final String reason;

        public FailedFile(String fileName, String reason) {
            this.fileName = fileName;
            this.reason = reason;
        }

        public String getFileName() {
            return fileName;
        }

        public String getReason() {
            return reason;
        }

        @Override
        public String toString() {
            return fileName + " -> " + reason;
        }

    }

}
