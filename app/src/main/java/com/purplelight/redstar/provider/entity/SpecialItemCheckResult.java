package com.purplelight.redstar.provider.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 专项检查结果
 * Created by wangyn on 16/5/22.
 */
public class SpecialItemCheckResult implements Parcelable {
    private String name;

    private int result;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public SpecialItemCheckResult(){}

    public SpecialItemCheckResult(Parcel src){
        name = src.readString();
        result = src.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(result);
    }

    public static Creator<SpecialItemCheckResult> CREATOR = new Creator<SpecialItemCheckResult>() {
        @Override
        public SpecialItemCheckResult createFromParcel(Parcel source) {
            return new SpecialItemCheckResult(source);
        }

        @Override
        public SpecialItemCheckResult[] newArray(int size) {
            return new SpecialItemCheckResult[size];
        }
    };

}
