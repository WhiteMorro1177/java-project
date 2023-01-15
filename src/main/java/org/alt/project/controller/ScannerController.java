package org.alt.project.controller;

import org.alt.project.model.ScanTask;
import org.alt.project.repository.ScanResultRepository;
import org.alt.project.scanner.ScanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return new ScanTask();
    }

    @GetMapping("/all")
    public List<ScanTask> GetHistory() {
        return repository.findAll();
    }


    @PutMapping("/all/new")
    public ScanTask SetTask(@RequestBody() String path) {
        Random random = new Random();
        // check existed ids
        var tasks = repository.findAll();
        List<Integer> ids = new ArrayList<>();
        for (var task : tasks) {
            ids.add(task.getId());
        }

        // new id
        int newId = random.nextInt(100);
        while (ids.contains(newId)) {
            newId = random.nextInt(100);
        }
        ScanTask task = new ScanTask();
        task.setPath(path);
        task.setId(newId);
        task.setResult("Task with id \"" + newId + "\" set");
        task.setStatus(false);
        repository.save(task);

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

        return task;
    }

    @DeleteMapping("/all/delete")
    public void ClearHistory() {
        var notes = repository.findAll().stream().filter(x -> !x.getStatus());
        for (int i = 0; i < notes.count(); i++) {
            repository.delete(notes.toList().get(i));
        }
    }

}
