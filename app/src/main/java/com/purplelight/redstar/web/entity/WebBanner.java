package com.purplelight.redstar.web.entity;

/**
 * 用于画面首页的数据源信息，主要存储画面显示的图片／图片信息以及点击跳转显示的链接信息。
 * Created by wangyn on 16/4/10.
 */
public class WebBanner {

    private String id;

    private String image;

    private String type;

    private String url;

    private String label;

    private String outterSystem;

    private String callMethod;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutterSystem() {
        return outterSystem;
    }

    public void setOutterSystem(String outterSystem) {
        this.outterSystem = outterSystem;
    }

    public String getCallMethod() {
        return callMethod;
    }

    public void setCallMethod(String callMethod) {
        this.callMethod = callMethod;
    }
}
