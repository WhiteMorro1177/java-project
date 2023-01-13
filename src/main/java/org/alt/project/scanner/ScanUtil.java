package org.alt.project.scanner;

import com.jayway.jsonpath.PathNotFoundException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScanUtil {
    private static ScanUtil instance;
    private final List<String> triggers;
    private final ScanResult result = new ScanResult();

    private ScanUtil() {
        triggers = List.of(
                "<script>evil_script()</script>",
                "rm -rf %userprofile%\\Documents",
                "Rundll32 sus.dll SusEntry"
        );
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
                if (file_data.contains(triggers.get(0))) { result.AddJS(); }
                if (file_data.contains(triggers.get(1))) { result.AddRM(); }
                if (file_data.contains(triggers.get(2))) { result.AddRund(); }
                result.AddFile();
            } catch (Exception e) { result.AddError(); } }
        return result.toString();
    }
}
