package org.alt.project.scanner;


import org.springframework.util.StopWatch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ScanUtil {
    private final ScanResult result;

    public ScanUtil() {
        result = new ScanResult();
    }

    public String Scan(String path) {
        // start stopwatch
        StopWatch sw = new StopWatch();
        if (!sw.isRunning()) {
            sw.start();
        }

        File dir = new File(path);
        if (!dir.exists()) {
            return "Invalid path";
        }
        if (dir.isFile()) {
            try {
                String file_data = Files.readAllLines(Paths.get(dir.toURI())).toString();
                if (file_data.contains("<script>evil_script()</script>")) {
                    result.AddJS();
                }
                if (file_data.contains("rm -rf %userprofile%\\Documents")) {
                    result.AddRM();
                }
                if (file_data.contains("Rundll32 sus.dll SusEntry")) {
                    result.AddRund();
                }
                result.AddFile();
            } catch (Exception e) {
                result.AddError();
            }
        }
        if (dir.isDirectory()) {
            for (var nestedDir : Objects.requireNonNull(dir.listFiles())) {
                Scan(nestedDir.getAbsolutePath());
            }
        }
        // write sw value
        if (sw.isRunning()) {
            sw.stop();
        }
        result.setTime(sw.getLastTaskTimeMillis());
        return result.toString();
    }
}
