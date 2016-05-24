package com.purplelight.redstar.web.result;

import com.purplelight.redstar.web.entity.Passport;

import java.util.ArrayList;
import java.util.List;

/**
 * 证照查询结果
 * Created by wangyn on 16/5/24.
 */
public class PassportResult extends Result {
    private List<Passport> passports = new ArrayList<>();

    public List<Passport> getPassports() {
        return passports;
    }

    public void setPassports(List<Passport> passports) {
        this.passports = passports;
    }
}
