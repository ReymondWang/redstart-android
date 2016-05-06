package com.purplelight.redstar.web.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 证照类型列表
 * Created by wangyn on 16/5/5.
 */
public class PassportCategory extends PassportType {

    private List<PassportType> typeList = new ArrayList<>();

    public List<PassportType> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<PassportType> typeList) {
        this.typeList = typeList;
    }
}
