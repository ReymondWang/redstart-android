package com.purplelight.redstar.provider.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三方评估详细
 * Created by wangyn on 16/5/9.
 */
public class EstimateItem implements Parcelable {

    // 主键
    private int id;

    // 所属报告编号
    private int reportId;

    // 项目编号
    private String projectId;

    // 项目名称
    private String projectName;

    // 区域名称
    private String areaName;

    // 问题分类
    private String category;

    // 问题性质
    private String character;

    // 是否重大
    private String level;

    // 所属工程
    private String partition;

    // 问题描述
    private String description;

    // 缩略图
    private List<String> thumbs = new ArrayList<>();

    // 原始图片
    private List<String> images = new ArrayList<>();

    // 整改责任人编号
    private String inChargePersonId;

    // 整改责任人姓名
    private String inChargePersonName;

    // 验收责任人编号
    private String checkPersonId;

    // 验收责任人姓名
    private String checkPersonName;

    // 计划完成日期
    private String planDate;

    // 开始日期
    private String beginDate;

    // 实际完成日期
    private String endDate;

    // 整改措施
    private String improvmentAction;

    // 整改缩略图
    private List<String> fixedThumbs = new ArrayList<>();

    // 整改原始图片
    private List<String> fixedImages = new ArrayList<>();

    // 本地图片状态，只影响整改后的图片
    private int localImage;

    // 业务状态
    private int status;

    // 本地已修改
    private int hasModified;

    // 下载状态
    private int downloadStatus;

    // 上传状态
    private int uploadStatus;

    // 外部系统主键
    private int outterSystemId;

    // 创建日期
    private long createDate;

    // 更新日期
    private long updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getThumbs() {
        return thumbs;
    }

    public void setThumbs(List<String> thumbs) {
        this.thumbs = thumbs;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getInChargePersonId() {
        return inChargePersonId;
    }

    public void setInChargePersonId(String inChargePersonId) {
        this.inChargePersonId = inChargePersonId;
    }

    public String getInChargePersonName() {
        return inChargePersonName;
    }

    public void setInChargePersonName(String inChargePersonName) {
        this.inChargePersonName = inChargePersonName;
    }

    public String getCheckPersonId() {
        return checkPersonId;
    }

    public void setCheckPersonId(String checkPersonId) {
        this.checkPersonId = checkPersonId;
    }

    public String getCheckPersonName() {
        return checkPersonName;
    }

    public void setCheckPersonName(String checkPersonName) {
        this.checkPersonName = checkPersonName;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImprovmentAction() {
        return improvmentAction;
    }

    public void setImprovmentAction(String improvmentAction) {
        this.improvmentAction = improvmentAction;
    }

    public List<String> getFixedThumbs() {
        return fixedThumbs;
    }

    public void setFixedThumbs(List<String> fixedThumbs) {
        this.fixedThumbs = fixedThumbs;
    }

    public List<String> getFixedImages() {
        return fixedImages;
    }

    public void setFixedImages(List<String> fixedImages) {
        this.fixedImages = fixedImages;
    }

    public int getLocalImage() {
        return localImage;
    }

    public void setLocalImage(int localImage) {
        this.localImage = localImage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHasModified() {
        return hasModified;
    }

    public void setHasModified(int hasModified) {
        this.hasModified = hasModified;
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

    public int getOutterSystemId() {
        return outterSystemId;
    }

    public void setOutterSystemId(int outterSystemId) {
        this.outterSystemId = outterSystemId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public EstimateItem(){}

    public EstimateItem(Parcel src){
        id = src.readInt();
        reportId = src.readInt();
        projectId = src.readString();
        projectName = src.readString();
        areaName = src.readString();
        category = src.readString();
        character = src.readString();
        description = src.readString();
        src.readStringList(thumbs);
        src.readStringList(images);
        inChargePersonId = src.readString();
        inChargePersonName = src.readString();
        checkPersonId = src.readString();
        checkPersonName = src.readString();
        planDate = src.readString();
        beginDate = src.readString();
        endDate = src.readString();
        improvmentAction = src.readString();
        src.readStringList(fixedThumbs);
        src.readStringList(fixedImages);
        localImage = src.readInt();
        status = src.readInt();
        hasModified = src.readInt();
        downloadStatus = src.readInt();
        uploadStatus = src.readInt();
        outterSystemId = src.readInt();
        createDate = src.readLong();
        updateDate = src.readLong();
        level = src.readString();
        partition = src.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(reportId);
        dest.writeString(projectId);
        dest.writeString(projectName);
        dest.writeString(areaName);
        dest.writeString(category);
        dest.writeString(character);
        dest.writeString(description);
        dest.writeStringList(thumbs);
        dest.writeStringList(images);
        dest.writeString(inChargePersonId);
        dest.writeString(inChargePersonName);
        dest.writeString(checkPersonId);
        dest.writeString(checkPersonName);
        dest.writeString(planDate);
        dest.writeString(beginDate);
        dest.writeString(endDate);
        dest.writeString(improvmentAction);
        dest.writeStringList(fixedThumbs);
        dest.writeStringList(fixedImages);
        dest.writeInt(localImage);
        dest.writeInt(status);
        dest.writeInt(hasModified);
        dest.writeInt(downloadStatus);
        dest.writeInt(uploadStatus);
        dest.writeInt(outterSystemId);
        dest.writeLong(createDate);
        dest.writeLong(updateDate);
        dest.writeString(level);
        dest.writeString(partition);
    }

    public static final Parcelable.Creator<EstimateItem> CREATOR = new Parcelable.Creator<EstimateItem>(){
        @Override
        public EstimateItem createFromParcel(Parcel source) {
            return new EstimateItem(source);
        }

        @Override
        public EstimateItem[] newArray(int size) {
            return new EstimateItem[size];
        }
    };
}
