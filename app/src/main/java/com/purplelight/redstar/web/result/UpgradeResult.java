package com.purplelight.redstar.web.result;

import com.purplelight.redstar.web.entity.UpgradeInfo;

/**
 * 更新信息结果
 * Created by wangyn on 16/5/26.
 */
public class UpgradeResult extends Result {
    private UpgradeInfo upgradeInfo = new UpgradeInfo();

    public UpgradeInfo getUpgradeInfo() {
        return upgradeInfo;
    }

    public void setUpgradeInfo(UpgradeInfo upgradeInfo) {
        this.upgradeInfo = upgradeInfo;
    }
}
