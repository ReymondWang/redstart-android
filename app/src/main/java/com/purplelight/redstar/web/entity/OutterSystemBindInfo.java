package com.purplelight.redstar.web.entity;

import java.sql.Timestamp;

/**
 * 外部系统
 * Created by wangyn on 16/5/15.
 */
public class OutterSystemBindInfo {
    private int systemId;

    private String systemCode;

    private String systemName;

    private String systemType;

    private String systemUrl;

    private int startUsing;

    private String validationUrl;

    private String systemDescription;

    private boolean isBinded;

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getSystemUrl() {
        return systemUrl;
    }

    public void setSystemUrl(String systemUrl) {
        this.systemUrl = systemUrl;
    }

    public int getStartUsing() {
        return startUsing;
    }

    public void setStartUsing(int startUsing) {
        this.startUsing = startUsing;
    }

    public String getValidationUrl() {
        return validationUrl;
    }

    public void setValidationUrl(String validationUrl) {
        this.validationUrl = validationUrl;
    }

    public String getSystemDescription() {
        return systemDescription;
    }

    public void setSystemDescription(String systemDescription) {
        this.systemDescription = systemDescription;
    }

    public boolean isBinded() {
        return isBinded;
    }

    public void setBinded(boolean binded) {
        isBinded = binded;
    }
}
