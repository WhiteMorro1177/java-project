package org.alt.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "scanner_tasks")
public class ScanTask {
    @Id
    private Integer id;
    @Column(name = "path")
    private String path;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "result")
    private String result;

    public ScanTask() {
        this.status = false;
        this.result = "Task is not ready yet";
    }

    public Integer getId() { return id; }
    public String getPath() { return path; }
    public Boolean getStatus() { return status; }
    public String getResult() { return result; }

    public void setId(Integer id) { this.id = id; }
    public void setPath(String path) { this.path = path; }
    public void setStatus(Boolean status) { this.status = status; }
    public void setResult(String result) { this.result = result; }

}
