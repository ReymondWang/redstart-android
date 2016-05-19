package com.purplelight.redstar.provider.dao;

import com.purplelight.redstar.provider.entity.EstimateReport;

import java.util.List;
import java.util.Map;

/**
 * 第三方评估报告Dao
 * Created by wangyn on 16/5/18.
 */
public interface IEstimateReportDao {
    void save(EstimateReport report);
    void saveAll(List<EstimateReport> reportList);
    List<EstimateReport> query(Map<String, String> conditions);
    EstimateReport getById(int reportId);
    void deleteById(int reportId);
    void clear();
}
