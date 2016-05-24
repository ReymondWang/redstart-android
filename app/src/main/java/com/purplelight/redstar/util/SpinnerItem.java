package com.purplelight.redstar.util;

/**
 * 下拉框项目
 * Created by wangyn on 16/5/24.
 */
public class SpinnerItem {
    private String ID = "";
    private String Value = "";

    public SpinnerItem(String id, String value){
        ID = id;
        Value = value;
    }

    public String toString(){
        return Value;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
