package com.purplelight.redstar.web.result;

import com.purplelight.redstar.web.entity.PassportFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 证照份文件结果
 * Created by wangyn on 16/5/24.
 */
public class PassportFileResult extends Result {
    private List<PassportFile> fileList = new ArrayList<>();

    public List<PassportFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<PassportFile> fileList) {
        this.fileList = fileList;
    }
}
