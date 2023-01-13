package org.alt.project.http;

import org.alt.project.model.ScanTask;
import retrofit.http.*;

import java.util.List;

public interface ScanService {
    @GET("/task/{id}")
    ScanTask GetTaskById(@Query("id") Integer id);

    @GET("/task/all")
    List<ScanTask> GetAll();

    @PUT("/task/all/new")
    boolean SetTask(@Body String path);

    @DELETE("task/all/delete")
    boolean DeleteHistory();
}
