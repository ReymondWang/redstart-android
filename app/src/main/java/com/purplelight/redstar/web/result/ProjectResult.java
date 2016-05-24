package com.purplelight.redstar.web.result;

import com.purplelight.redstar.web.entity.ProjectInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目信息结果
 * Created by wangyn on 16/5/24.
 */
public class ProjectResult extends Result {
    private List<ProjectInfo> projects = new ArrayList<>();

    public List<ProjectInfo> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectInfo> projects) {
        this.projects = projects;
    }
}
