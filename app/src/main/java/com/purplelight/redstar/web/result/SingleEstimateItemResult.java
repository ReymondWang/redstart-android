package com.purplelight.redstar.web.result;

import com.purplelight.redstar.provider.entity.EstimateItem;

/**
 * 单个评估项Result
 * Created by wangyn on 16/5/19.
 */
public class SingleEstimateItemResult extends Result {
    private EstimateItem item;

    public EstimateItem getItem() {
        return item;
    }

    public void setItem(EstimateItem item) {
        this.item = item;
    }
}
