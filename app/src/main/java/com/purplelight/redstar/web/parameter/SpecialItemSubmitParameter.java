package com.purplelight.redstar.web.parameter;

import com.purplelight.redstar.provider.entity.SpecialItemCheckResult;

import java.util.List;

/**
 * 专项检查的参数
 * Created by wangyn on 16/5/23.
 */
public class SpecialItemSubmitParameter extends Parameter {
    private int systemId;

    private int itemId;

    private List<SpecialItemCheckResult> results;

    private List<String> images;

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public List<SpecialItemCheckResult> getResults() {
        return results;
    }

    public void setResults(List<SpecialItemCheckResult> results) {
        this.results = results;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
