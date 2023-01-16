package org.alt.project.scanner;

import java.time.LocalDateTime;

public class ScanResult {
    private int totalFilesCount;
    private int js;
    private int rmrf;
    private int rundll32;
    private int errors;
    private long executionTime;

    public ScanResult() {  }

    public void AddFile() { totalFilesCount++; }
    public void AddJS() { js++; }
    public void AddRM() { rmrf++; }
    public void AddRund() { rundll32++; }
    public void AddError() { errors++; }

    public void setTime(long executionTime) { this.executionTime = executionTime; }

    @Override
    public String toString()
    {
        return "===== Scan result =====" +
                "\nProcessed files: " + totalFilesCount +
                "\nJS detects: " + js +
                "\nrm -rf detects: " + rmrf +
                "\nRundll32 detects: " + rundll32 +
                "\nErrors: " + errors +
                "\nExection time: " + executionTime +
                "\n=======================";
    }
}
