package com.purplelight.redstar.web.result;

import com.purplelight.redstar.provider.entity.EstimateReport;

/**
 * 单个评估报告Result
 * Created by wangyn on 16/5/19.
 */
public class SingleEstimateReportResult extends Result {
    private EstimateReport report;

    public EstimateReport getReport() {
        return report;
    }

    public void setReport(EstimateReport report) {
        this.report = report;
    }
}
