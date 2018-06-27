package com.example.dai.siritori;

public class SettingParamModel {

    private String title;
    private String param;

    SettingParamModel(String title, String param){
        this.title = title;
        this.param = param;
    }

    public String getTitle() {
        return title;
    }

    public String getParam() {
        return param;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
