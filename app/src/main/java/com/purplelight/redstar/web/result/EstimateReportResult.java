package com.purplelight.redstar.web.result;

import com.purplelight.redstar.provider.entity.EstimateReport;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三方评估报告Result
 * Created by wangyn on 16/5/18.
 */
public class EstimateReportResult extends Result {
    private List<EstimateReport> reports = new ArrayList<>();

    public List<EstimateReport> getReports() {
        return reports;
    }

    public void setReports(List<EstimateReport> reports) {
        this.reports = reports;
    }
}
