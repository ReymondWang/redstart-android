package com.purplelight.redstar.provider.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 第三方评估详细
 * Created by wangyn on 16/5/9.
 */
public class EstimateItem implements Parcelable {

    private String itemId;

    private String parentId;

    private String area;

    private String project;

    private String category;

    private String operatorId;

    private String checkerId;

    private String checker;

    private String description;

    // 图片大图信息
    private String imageUrls;

    // 图片缩略图信息
    private String thumbUrls;

    // 整改信息
    private String submitInfo;

    // 整改图片大图信息
    private String submitImageUrls;

    // 整改图片缩略图信息
    private String submitThumbUrls;

    // 下载状态
    private String downloadStatus;

    // 业务单据状态
    private String status;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(String checkerId) {
        this.checkerId = checkerId;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getThumbUrls() {
        return thumbUrls;
    }

    public void setThumbUrls(String thumbUrls) {
        this.thumbUrls = thumbUrls;
    }

    public String getSubmitInfo() {
        return submitInfo;
    }

    public void setSubmitInfo(String submitInfo) {
        this.submitInfo = submitInfo;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getSubmitImageUrls() {
        return submitImageUrls;
    }

    public void setSubmitImageUrls(String submitImageUrls) {
        this.submitImageUrls = submitImageUrls;
    }

    public String getSubmitThumbUrls() {
        return submitThumbUrls;
    }

    public void setSubmitThumbUrls(String submitThumbUrls) {
        this.submitThumbUrls = submitThumbUrls;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EstimateItem(){}

    public EstimateItem(Parcel src){
        itemId = src.readString();
        parentId = src.readString();
        area = src.readString();
        project = src.readString();
        category = src.readString();
        operatorId = src.readString();
        checkerId = src.readString();
        checker = src.readString();
        description = src.readString();
        imageUrls = src.readString();
        thumbUrls = src.readString();
        submitInfo = src.readString();
        submitImageUrls = src.readString();
        submitThumbUrls = src.readString();
        downloadStatus = src.readString();
        status = src.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(parentId);
        dest.writeString(area);
        dest.writeString(project);
        dest.writeString(category);
        dest.writeString(operatorId);
        dest.writeString(checkerId);
        dest.writeString(checker);
        dest.writeString(description);
        dest.writeString(imageUrls);
        dest.writeString(thumbUrls);
        dest.writeString(submitInfo);
        dest.writeString(submitImageUrls);
        dest.writeString(submitThumbUrls);
        dest.writeString(downloadStatus);
        dest.writeString(status);
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
