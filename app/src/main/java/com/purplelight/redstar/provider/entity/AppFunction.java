package com.purplelight.redstar.provider.entity;

/**
 * App功能
 * Created by wangyn on 16/5/7.
 */
public class AppFunction {
    private String id;

    private String fragment;

    private String part;

    private String functionType;

    private String titleImgPath;

    private String title;

    private String contentUrl;

    private String statUrl;

    private int outterSystemId;

    private String callMethod;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getTitleImgPath() {
        return titleImgPath;
    }

    public void setTitleImgPath(String titleImgPath) {
        this.titleImgPath = titleImgPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getStatUrl() {
        return statUrl;
    }

    public void setStatUrl(String statUrl) {
        this.statUrl = statUrl;
    }

    public int getOutterSystemId() {
        return outterSystemId;
    }

    public void setOutterSystemId(int outterSystemId) {
        this.outterSystemId = outterSystemId;
    }

    public String getCallMethod() {
        return callMethod;
    }

    public void setCallMethod(String callMethod) {
        this.callMethod = callMethod;
    }
}
