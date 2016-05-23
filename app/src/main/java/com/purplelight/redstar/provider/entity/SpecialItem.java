package com.purplelight.redstar.provider.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三方检查明细项目
 * Created by wangyn on 16/5/22.
 */
public class SpecialItem implements Parcelable {

    private int id;

    private int systemId;

    private String category;

    private String projectName;

    private String areaName;

    private String createDate;

    private List<String> places = new ArrayList<>();

    private String buildingId;

    private String code;

    private List<String> names = new ArrayList<>();

    private String personName;

    private String remark;

    private String checkDate;

    private int passPercent;

    private List<SpecialItemCheckResult> resultItems = new ArrayList<>();

    private String building;

    private List<String> thumbnail = new ArrayList<>();

    private List<String> images = new ArrayList<>();

    private int downloadStatus;

    private int uploadStatus;

    private long createTime;

    private long updateTime;

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

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<String> getPlaces() {
        return places;
    }

    public void setPlaces(List<String> places) {
        this.places = places;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public int getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(int passPercent) {
        this.passPercent = passPercent;
    }

    public List<SpecialItemCheckResult> getResultItems() {
        return resultItems;
    }

    public void setResultItems(List<SpecialItemCheckResult> resultItems) {
        this.resultItems = resultItems;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public List<String> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(List<String> thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public SpecialItem(){}

    public SpecialItem(Parcel src){
        id = src.readInt();
        systemId = src.readInt();
        category = src.readString();
        projectName = src.readString();
        areaName = src.readString();
        createDate = src.readString();
        src.readStringList(places);
        buildingId = src.readString();
        code = src.readString();
        src.readStringList(names);
        personName = src.readString();
        remark = src.readString();
        checkDate = src.readString();
        passPercent = src.readInt();
        src.readTypedList(resultItems, SpecialItemCheckResult.CREATOR);
        building = src.readString();
        src.readStringList(thumbnail);
        src.readStringList(images);
        downloadStatus = src.readInt();
        uploadStatus = src.readInt();
        createTime = src.readLong();
        updateTime = src.readLong();
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
        dest.writeString(areaName);
        dest.writeString(createDate);
        dest.writeStringList(places);
        dest.writeString(buildingId);
        dest.writeString(code);
        dest.writeStringList(names);
        dest.writeString(personName);
        dest.writeString(remark);
        dest.writeString(checkDate);
        dest.writeInt(passPercent);
        dest.writeTypedList(resultItems);
        dest.writeString(building);
        dest.writeStringList(thumbnail);
        dest.writeStringList(images);
        dest.writeInt(downloadStatus);
        dest.writeInt(uploadStatus);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
    }

    public static Creator<SpecialItem> CREATOR = new Creator<SpecialItem>() {
        @Override
        public SpecialItem createFromParcel(Parcel source) {
            return new SpecialItem(source);
        }

        @Override
        public SpecialItem[] newArray(int size) {
            return new SpecialItem[size];
        }
    };
}
