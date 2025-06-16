package com.cloudpos.injectkey.demo;

import android.support.annotation.NonNull;

import com.cloudpos.injectkey.demo.util.CommonUtils;


public class RKey {
    private String key;
    private boolean aes;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public boolean isAes() {
        return aes;
    }
    public RKey setAes(boolean aes) {
        this.aes = aes;
        return this;
    }
    public byte[] getKeyData() {
        return CommonUtils.toBytes(key);
    }
    public RKey setKeyData(byte[] data) {
        this.key = CommonUtils.toHex(data);
        return this;
    }
    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("aes:").append(aes).append(", ");
        sb.append("key:").append(key);
        return sb.toString();
    }
}
