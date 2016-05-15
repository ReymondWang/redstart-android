package com.purplelight.redstar.web.result;

import com.purplelight.redstar.web.entity.OutterSystemBindInfo;

import java.util.List;

/**
 * 外部系统Result
 * Created by wangyn on 16/5/15.
 */
public class OutterSystemResult extends Result {

    private List<OutterSystemBindInfo> systemList;

    public List<OutterSystemBindInfo> getSystemList() {
        return systemList;
    }

    public void setSystemList(List<OutterSystemBindInfo> systemList) {
        this.systemList = systemList;
    }
}
