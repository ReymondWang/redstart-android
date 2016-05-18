package com.purplelight.redstar.provider.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 整改报告信息
 * Created by wangyn on 16/5/11.
 */
public class EstimateReport implements Parcelable {

    // 主键
    private int id;

    // 项目编号
    private String projectId;

    // 项目名称
    private String projectName;

    // 区域编号
    private String areaId;

    // 区域名称
    private String areaName;

    // 检查类型
    private int category;

    // 评估类型
    private int checkType;

    // 报告日期
    private String reportDate;

    // 项目负责人
    private String inChargePerson;

    // 评估单位及人员
    private String reporter;

    // 监理单位编号
    private String supervisionId;

    // 监理单位名称
    private String supervisionName;

    // 总包单位名称
    private String constractionId;

    // 总包单位名称
    private String constractionName;

    // 备注
    private String remark;

    // 实测实量合格率
    private double gradeSCSL;

    // 毛坯房户内观感合格率
    private double gradeMPFH;

    // 公共部位观感合格率
    private double gradeGGBW;

    // 外立面观感合格率
    private double gradeWLMGG;

    // 园林观感合格率
    private double gradeYLGG;

    // 项目综合合格率
    private double gradeXMZH;

    // 实测得分
    private double gradeSCDF;

    // 质量关键扣分
    private double gradeZLKF;

    // 管理行为
    private double gradeGLXW;

    // 安全文明
    private double gradeAQWM;

    // 标段综合得分
    private double gradeZHDF;

    // 下载状态
    private int downloadStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getInChargePerson() {
        return inChargePerson;
    }

    public void setInChargePerson(String inChargePerson) {
        this.inChargePerson = inChargePerson;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getSupervisionId() {
        return supervisionId;
    }

    public void setSupervisionId(String supervisionId) {
        this.supervisionId = supervisionId;
    }

    public String getSupervisionName() {
        return supervisionName;
    }

    public void setSupervisionName(String supervisionName) {
        this.supervisionName = supervisionName;
    }

    public String getConstractionId() {
        return constractionId;
    }

    public void setConstractionId(String constractionId) {
        this.constractionId = constractionId;
    }

    public String getConstractionName() {
        return constractionName;
    }

    public void setConstractionName(String constractionName) {
        this.constractionName = constractionName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getGradeSCSL() {
        return gradeSCSL;
    }

    public void setGradeSCSL(double gradeSCSL) {
        this.gradeSCSL = gradeSCSL;
    }

    public double getGradeMPFH() {
        return gradeMPFH;
    }

    public void setGradeMPFH(double gradeMPFH) {
        this.gradeMPFH = gradeMPFH;
    }

    public double getGradeGGBW() {
        return gradeGGBW;
    }

    public void setGradeGGBW(double gradeGGBW) {
        this.gradeGGBW = gradeGGBW;
    }

    public double getGradeWLMGG() {
        return gradeWLMGG;
    }

    public void setGradeWLMGG(double gradeWLMGG) {
        this.gradeWLMGG = gradeWLMGG;
    }

    public double getGradeYLGG() {
        return gradeYLGG;
    }

    public void setGradeYLGG(double gradeYLGG) {
        this.gradeYLGG = gradeYLGG;
    }

    public double getGradeXMZH() {
        return gradeXMZH;
    }

    public void setGradeXMZH(double gradeXMZH) {
        this.gradeXMZH = gradeXMZH;
    }

    public double getGradeSCDF() {
        return gradeSCDF;
    }

    public void setGradeSCDF(double gradeSCDF) {
        this.gradeSCDF = gradeSCDF;
    }

    public double getGradeZLKF() {
        return gradeZLKF;
    }

    public void setGradeZLKF(double gradeZLKF) {
        this.gradeZLKF = gradeZLKF;
    }

    public double getGradeGLXW() {
        return gradeGLXW;
    }

    public void setGradeGLXW(double gradeGLXW) {
        this.gradeGLXW = gradeGLXW;
    }

    public double getGradeAQWM() {
        return gradeAQWM;
    }

    public void setGradeAQWM(double gradeAQWM) {
        this.gradeAQWM = gradeAQWM;
    }

    public double getGradeZHDF() {
        return gradeZHDF;
    }

    public void setGradeZHDF(double gradeZHDF) {
        this.gradeZHDF = gradeZHDF;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public EstimateReport(){}

    public EstimateReport(Parcel src){
        id = src.readInt();
        projectId = src.readString();
        projectName = src.readString();
        areaId = src.readString();
        areaName = src.readString();
        category = src.readInt();
        checkType = src.readInt();
        reportDate = src.readString();
        inChargePerson = src.readString();
        reporter = src.readString();
        supervisionId = src.readString();
        supervisionName = src.readString();
        constractionId = src.readString();
        constractionName = src.readString();
        remark = src.readString();
        gradeSCSL = src.readDouble();
        gradeMPFH = src.readDouble();
        gradeGGBW = src.readDouble();
        gradeWLMGG = src.readDouble();
        gradeYLGG = src.readDouble();
        gradeXMZH = src.readDouble();
        gradeSCDF = src.readDouble();
        gradeZLKF = src.readDouble();
        gradeGLXW = src.readDouble();
        gradeAQWM = src.readDouble();
        gradeZHDF = src.readDouble();
        downloadStatus = src.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(projectId);
        dest.writeString(projectName);
        dest.writeString(areaId);
        dest.writeString(areaName);
        dest.writeInt(category);
        dest.writeInt(checkType);
        dest.writeString(reportDate);
        dest.writeString(inChargePerson);
        dest.writeString(reporter);
        dest.writeString(supervisionId);
        dest.writeString(supervisionName);
        dest.writeString(constractionId);
        dest.writeString(constractionName);
        dest.writeString(remark);
        dest.writeDouble(gradeSCSL);
        dest.writeDouble(gradeMPFH);
        dest.writeDouble(gradeGGBW);
        dest.writeDouble(gradeWLMGG);
        dest.writeDouble(gradeYLGG);
        dest.writeDouble(gradeXMZH);
        dest.writeDouble(gradeSCDF);
        dest.writeDouble(gradeZLKF);
        dest.writeDouble(gradeGLXW);
        dest.writeDouble(gradeAQWM);
        dest.writeDouble(gradeZHDF);
        dest.writeInt(downloadStatus);
    }

    public static final Parcelable.Creator<EstimateReport> CREATOR = new Parcelable.Creator<EstimateReport>(){
        @Override
        public EstimateReport createFromParcel(Parcel source) {
            return new EstimateReport(source);
        }

        @Override
        public EstimateReport[] newArray(int size) {
            return new EstimateReport[size];
        }
    };
}
