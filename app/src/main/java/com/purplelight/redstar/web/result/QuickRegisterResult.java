package com.purplelight.redstar.web.result;

/**
 * 快速注册结果
 * Created by wangyn on 16/5/15.
 */
public class QuickRegisterResult extends Result {
    private boolean quickRegister = false;

    public boolean isQuickRegister() {
        return quickRegister;
    }

    public void setQuickRegister(boolean quickRegister) {
        this.quickRegister = quickRegister;
    }
}
