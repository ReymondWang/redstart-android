package com.purplelight.redstar.web.parameter;

/**
 * 第三方评估报告参数
 * Created by wangyn on 16/5/18.
 */
public class EstimateReportParameter extends Parameter {
    private int estimateType;

    private int systemId;

    private int reportId;

    private int pageNo;

    private int pageSize;

    public int getEstimateType() {
        return estimateType;
    }

    public void setEstimateType(int estimateType) {
        this.estimateType = estimateType;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
