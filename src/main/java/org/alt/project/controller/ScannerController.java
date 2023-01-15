package org.alt.project.controller;

import org.alt.project.model.ScanTask;
import org.alt.project.repository.ScanResultRepository;
import org.alt.project.scanner.ScanUtil;
import org.apache.catalina.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retrofit.http.Body;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("task/")
public class ScannerController {
    private final ScanResultRepository repository;

    @Autowired
    public ScannerController(ScanResultRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public ScanTask GetResultById(@PathVariable("id") Integer id) {
        List<ScanTask> tasks = repository.findAll();
        for (var task : tasks) {
            if (Objects.equals(task.getId(), id)) {
                return task;
            }
        }
        return new ScanTask(-1, "");
    }

    @GetMapping("/all")
    public List<ScanTask> GetHistory() {
        return repository.findAll();
    }


    @PutMapping("/all/new")
    public String SetTask(@Body String path) {
        Random random = new Random();
        // check existed ids
        var tasks = repository.findAll();
        List<Integer> ids = new ArrayList<Integer>();
        for (var task : tasks) {
            ids.add(task.getId());
        }

        // new id
        int newId = random.nextInt(100);
        while (ids.contains(newId)) {
            newId = random.nextInt(100);
        }
        repository.save(new ScanTask(newId, path));

        // scanning
        final int ID = newId;
        Executors.newSingleThreadExecutor().execute(() -> {
            ScanUtil util = ScanUtil.getInstance();
            String res = util.Scan(path);
            var scan = repository.findById(ID).orElse(null);
            repository.deleteById(ID);
            if (scan != null) scan.setResult(res);
            repository.save(scan);
        });

        return "Task with id {\"" + newId + "\"} set";
    }

    @DeleteMapping("/all/delete")
    public void ClearHistory() {
        var notes = repository.findAll().stream().filter(x -> !x.getStatus());
        for (int i = 0; i < notes.count(); i++) {
            repository.delete(notes.toList().get(i));
        }
    }

}
