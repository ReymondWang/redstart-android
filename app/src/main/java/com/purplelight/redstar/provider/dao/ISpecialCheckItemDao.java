package com.purplelight.redstar.provider.dao;

import com.purplelight.redstar.provider.entity.SpecialItem;

import java.util.List;
import java.util.Map;

/**
 * 专项检查项目Dao
 * Created by wangyn on 16/5/23.
 */
public interface ISpecialCheckItemDao {
    void save(SpecialItem item);
    void saveAll(List<SpecialItem> itemList);
    void update(SpecialItem item);
    void updateDownloadStatus(int itemId, int downloadStatus);
    void updateUploadStatus(int itemId, int uploadStatus);
    void saveOrUpdate(SpecialItem item);
    void deleteById(int itemId);
    List<SpecialItem> query(Map<String, String> map);
    List<SpecialItem> getByReportId(int reportId);
    SpecialItem getById(int itemId);
    void clear();
}
