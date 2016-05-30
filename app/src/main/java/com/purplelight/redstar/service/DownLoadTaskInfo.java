package com.purplelight.redstar.service;

import java.io.Serializable;

/**
 * 下载任务
 * Created by wangyn on 16/5/29.
 */
public class DownLoadTaskInfo implements Serializable {
    private final static long serialVersionUID = -2810508248527772902L;

    public final static int WAITING = 0;
    public final static int RUNNING = 1;
    public final static int CANCELED = 2;

    private int taskId;
    private String taskName;
    private int progress;
    private int status;
    private String downloadUrl;
    private String fileName;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
