package com.purplelight.redstar.web.parameter;

/**
 * 专项检查明细查询参数
 * Created by wangyn on 16/5/22.
 */
public class SpecialItemParameter extends Parameter {
    private int systemId;

    private int reportId;

    private boolean onlyUnChecked;

    private int itemId;

    private int pageNo;

    private int pageSize;

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

    public boolean isOnlyUnChecked() {
        return onlyUnChecked;
    }

    public void setOnlyUnChecked(boolean onlyUnChecked) {
        this.onlyUnChecked = onlyUnChecked;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
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
