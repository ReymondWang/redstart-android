package com.purplelight.redstar.web.parameter;

/**
 * 第三方评估明细查询参数
 * Created by wangyn on 16/5/17.
 */
public class EstimateItemParameter extends Parameter {
    private int type;

    private int estimateType;

    private int systemId;

    private int reportId;

    private int itemId;

    private boolean onlyMyself;

    private String projectId;

    private String partition;

    private String inChargeName;

    private String description;

    private int pageNo;

    private int pageSize;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public boolean isOnlyMyself() {
        return onlyMyself;
    }

    public void setOnlyMyself(boolean onlyMyself) {
        this.onlyMyself = onlyMyself;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getInChargeName() {
        return inChargeName;
    }

    public void setInChargeName(String inChargeName) {
        this.inChargeName = inChargeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
