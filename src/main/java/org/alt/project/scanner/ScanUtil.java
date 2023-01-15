package org.alt.project.scanner;

import com.jayway.jsonpath.PathNotFoundException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class ScanUtil {
    private static ScanUtil instance;
    private final ScanResult result = new ScanResult();

    private ScanUtil() {
    }

    public static ScanUtil getInstance() {
        if (instance == null) { instance = new ScanUtil(); }
        return instance;
    }

    public String Scan(String path) {
        File dir = new File(path);
        if (!dir.exists()) { throw new PathNotFoundException("Incorrect path"); } // think about "return"
        if (dir.isDirectory()) {
            for (var nestedDir: Objects.requireNonNull(dir.listFiles())) { Scan(nestedDir.getAbsolutePath()); }
        }
        if (dir.isFile()) {
            try {
                String file_data = Files.readAllLines(Paths.get(dir.toURI())).toString();
                if (file_data.contains("<script>evil_script()</script>")) { result.AddJS(); }
                if (file_data.contains("rm -rf %userprofile%\\Documents")) { result.AddRM(); }
                if (file_data.contains("Rundll32 sus.dll SusEntry")) { result.AddRund(); }
                result.AddFile();
            } catch (Exception e) { result.AddError(); } }
        return result.toString();
    }
}
