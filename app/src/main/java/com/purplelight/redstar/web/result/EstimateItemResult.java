package com.purplelight.redstar.web.result;

import com.purplelight.redstar.provider.entity.EstimateItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三方评估明细Result
 * Created by wangyn on 16/5/17.
 */
public class EstimateItemResult extends Result {
    private List<EstimateItem> items = new ArrayList<>();

    public List<EstimateItem> getItems() {
        return items;
    }

    public void setItems(List<EstimateItem> items) {
        this.items = items;
    }
}
