package com.purplelight.redstar.provider.entity;

/**
 * 整改报告信息
 * Created by wangyn on 16/5/11.
 */
public class EstimateReport {

    private String reportId;

    private String projectName;

    private String stageName;

    private String supplierName;

    private String monitorName;

    private String estimateType;

    private String estimateDate;

    // 实测得分
    private String measureScore;

    // 关键质量扣分
    private String keyDeductingScore;

    // 管理行为得分
    private String manageScore;

    // 文明安全得分
    private String safeScore;

    // 综合得分
    private String totalScore;

    // 下载状态
    private String downloadStatus;

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }

    public String getEstimateType() {
        return estimateType;
    }

    public void setEstimateType(String estimateType) {
        this.estimateType = estimateType;
    }

    public String getEstimateDate() {
        return estimateDate;
    }

    public void setEstimateDate(String estimateDate) {
        this.estimateDate = estimateDate;
    }

    public String getMeasureScore() {
        return measureScore;
    }

    public void setMeasureScore(String measureScore) {
        this.measureScore = measureScore;
    }

    public String getKeyDeductingScore() {
        return keyDeductingScore;
    }

    public void setKeyDeductingScore(String keyDeductingScore) {
        this.keyDeductingScore = keyDeductingScore;
    }

    public String getManageScore() {
        return manageScore;
    }

    public void setManageScore(String manageScore) {
        this.manageScore = manageScore;
    }

    public String getSafeScore() {
        return safeScore;
    }

    public void setSafeScore(String safeScore) {
        this.safeScore = safeScore;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
