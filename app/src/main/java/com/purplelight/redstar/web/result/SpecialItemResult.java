package com.purplelight.redstar.web.result;

import com.purplelight.redstar.provider.entity.SpecialItem;

import java.util.List;

/**
 * 专项检查明细结果
 * Created by wangyn on 16/5/22.
 */
public class SpecialItemResult extends Result {
    private List<SpecialItem> items;

    public List<SpecialItem> getItems() {
        return items;
    }

    public void setItems(List<SpecialItem> items) {
        this.items = items;
    }
}
