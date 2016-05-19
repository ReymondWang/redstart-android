package com.purplelight.redstar.provider.dao;

import com.purplelight.redstar.provider.entity.EstimateItem;

import java.util.List;
import java.util.Map;

/**
 * 第三方评估明细Dao
 * Created by wangyn on 16/5/18.
 */
public interface IEstimateItemDao {
    void save(EstimateItem item);
    void saveAll(List<EstimateItem> items);
    List<EstimateItem> query(Map<String, String> conditions);
    List<EstimateItem> getByReportId(int reportId);
    EstimateItem getById(int itemId);
    void updateDownloadStatus(int downloadStatus, int id);
    void update(EstimateItem item);
    void deleteById(int itemId);
    void clear();
}
