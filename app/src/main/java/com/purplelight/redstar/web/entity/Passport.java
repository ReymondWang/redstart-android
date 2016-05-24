package com.purplelight.redstar.web.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 证照
 * Created by wangyn on 16/5/4.
 */
public class Passport implements Parcelable {
    private int id;

    private int systemId;

    private String category;

    private String projectName;

    private String name;

    private String licenseDate;

    private String expireDate;

    private String resourceName;

    private String remark;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseDate() {
        return licenseDate;
    }

    public void setLicenseDate(String licenseDate) {
        this.licenseDate = licenseDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Passport(){}

    public Passport(Parcel src){
        id = src.readInt();
        systemId = src.readInt();
        category = src.readString();
        projectName = src.readString();
        name = src.readString();
        licenseDate = src.readString();
        expireDate = src.readString();
        resourceName = src.readString();
        remark = src.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(systemId);
        dest.writeString(category);
        dest.writeString(projectName);
        dest.writeString(name);
        dest.writeString(licenseDate);
        dest.writeString(expireDate);
        dest.writeString(resourceName);
        dest.writeString(remark);
    }

    public static Creator<Passport> CREATOR = new Creator<Passport>() {
        @Override
        public Passport createFromParcel(Parcel source) {
            return new Passport(source);
        }

        @Override
        public Passport[] newArray(int size) {
            return new Passport[size];
        }
    };
}
