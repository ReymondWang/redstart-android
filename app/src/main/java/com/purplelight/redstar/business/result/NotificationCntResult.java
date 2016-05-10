package com.purplelight.redstar.business.result;

import com.purplelight.redstar.web.result.Result;

/**
 * 提醒数量返回结果
 * Created by wangyn on 16/5/9.
 */
public class NotificationCntResult extends Result {

    private int count = 0;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
