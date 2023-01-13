package org.alt.project.controller;

import org.alt.project.model.ScanTask;
import org.alt.project.repository.ScanResultRepository;
import org.alt.project.scanner.ScanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retrofit.http.Body;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@RestController
public class ScannerController {
    private final ScanResultRepository repository;
    private long tasksCount;

    @Autowired
    public ScannerController(ScanResultRepository repository) {
        this.repository = repository;
        this.tasksCount = this.repository.count();
    }

    @GetMapping("/task/{id}")
    public ScanTask GetResultById(Integer id) {
        List<ScanTask> tasks = repository.findAll();
        for (var task : tasks) { if (Objects.equals(task.getId(), id)) { return task; } }
        return new ScanTask(-1, "");
    }

    @GetMapping("/task/all")
    public List<ScanTask> GetHistory() { return repository.findAll(); }


    @PutMapping("/task/all/new")
    public boolean SetTask(@Body String path) {
        tasksCount = repository.count();
        Random random = new Random();
        // check existed ids
        var tasks = repository.findAll();
        List<Integer> ids = new ArrayList<Integer>();
        for (var task : tasks) { ids.add(task.getId()); }

        // new id
        int newId = random.nextInt(100);
        while (ids.contains(newId)) { newId = random.nextInt(100); }
        repository.save(new ScanTask(newId, path));

        System.out.println("Task with id {" + newId + "} set");

        ScanUtil util = ScanUtil.getInstance();
        String res = util.Scan("C:\\Users\\jackf");

        System.out.println("|" + res + "|");
        /*

        // this code or find info about triggers
        if (tasksCount > repository.count()) {
            ScanUtil scanner = ScanUtil.getInstance();

            var uncompletedTasks = repository.findAll().stream().filter(x -> !x.getStatus());

            for (var task: uncompletedTasks.toList()) {

                System.out.println("Task with id {" + task.getId() + "} started");

                repository.delete(task);
                String res = scanner.Scan(task.getPath());
                task.setResult(res);
                task.setStatus(true);

                System.out.println("Task with id {" + task.getId() + "} result: \n" + task.getResult());

                repository.save(task);
            }
            tasksCount = repository.count();
        }

        */
        return true;
    }

    @DeleteMapping("/task/all/delete")
    public void ClearHistory()
    {
        var notes = repository.findAll().stream().filter(x -> !x.getStatus());
        for (int i = 0; i < notes.count(); i++) {
            repository.delete(notes.toList().get(i));
        }
    }

}
