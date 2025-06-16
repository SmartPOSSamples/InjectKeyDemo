package com.cloudpos.injectkey.demo;

import android.support.annotation.NonNull;


import java.util.ArrayList;
import java.util.List;

public class Result {
    public static final int SUCCESS = 1;
    public static final int NO_AVAILABLE_KEY = 2;

    private int status;
    private String desc;
    private List<RKey> keys = new ArrayList<>();

    public Result(int status) {
        this.status = status;
    }
    public Result(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public List<RKey> getKeys() {
        return keys;
    }
    public void setKeys(List<RKey> keys) {
        this.keys = keys;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("status:").append(status).append(", ");
        sb.append("desc:").append(desc).append(", ");
        sb.append("keys:").append(keys);
        return sb.toString();
    }
}