package com.purplelight.redstar.web.parameter;

/**
 * 证照文件参数
 * Created by wangyn on 16/5/24.
 */
public class PassportFileParameter extends Parameter {
    private int systemId;

    private int itemId;

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
