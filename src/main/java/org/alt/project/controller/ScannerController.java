package org.alt.project.controller;

import com.sun.jdi.VoidType;
import org.alt.project.model.ScanTask;
import org.alt.project.repository.ScanResultRepository;
import org.alt.project.scanner.ScanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("task/")
public class ScannerController {
    private final ScanResultRepository repository;

    private final List<Future<ScanTask>> listOfTasks = new ArrayList<>();

    @Autowired
    public ScannerController(ScanResultRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public ScanTask GetResultById(@PathVariable("id") String id) {
        List<ScanTask> tasks = repository.findAll();
        for (var task : tasks) {
            if (Objects.equals(task.getId().toString(), id)) {
                return task;
            }
        }
        return new ScanTask();
    }

    @GetMapping("/all")
    public List<ScanTask> GetHistory() {
        return repository.findAll();
    }


    @PutMapping("/all/new")
    public ScanTask SetTask(@RequestBody() String path) {
        // check file path
        try {
            path = path.replaceAll("\"", "");
            File file = new File(path);
            if (!file.isDirectory()) {
                if (!file.exists()) {
                    return new ScanTask(-1, path, false, "Incorrect path");
                }
            }
        } catch (Exception exc) {
            System.out.println("Exception in SetTask() -- Exception message: " + exc.getMessage());
            return new ScanTask(-1, path, false, "Incorrect path");
        }

        // check existed ids
        Random random = new Random();
        var tasks = repository.findAll();
        List<Integer> ids = new ArrayList<>();
        for (var task : tasks) {
            ids.add(task.getId());
        }

        // create new id
        int newId = random.nextInt(100);
        while (ids.contains(newId)) {
            newId = random.nextInt(100);
        }


        ScanTask task = new ScanTask();
        task.setPath(path);
        task.setId(newId);
        task.setResult("Task is not already done");
        task.setStatus(false);
        repository.save(task);

        // scanning
        final int ID = newId;
        final String PATH = path;
        Executors
                .newSingleThreadExecutor()
                .execute(() -> {
                    var result = new ScanUtil().Scan(PATH);

                    try {
                        repository.deleteById(ID);
                    } catch (Exception exc) {
                        System.out.println("Cannot delete task");
                    }
                    repository.save(new ScanTask(ID, PATH, true, result));
                });

        ScanTask tmp = new ScanTask();
        tmp.setPath(path);
        tmp.setId(newId);
        tmp.setResult("Task with id \"" + newId + "\" set");
        tmp.setStatus(false);
        return tmp;
    }

    @DeleteMapping("/all/delete")
    public void ClearHistory() {
        var notes = repository.findAll().stream().filter(x -> !x.getStatus());
        for (int i = 0; i < notes.count(); i++) {
            repository.delete(notes.toList().get(i));
        }
    }
}
