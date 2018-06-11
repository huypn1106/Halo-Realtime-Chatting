package com.deadk.halo.common.models;

public class Voice {
    private String url;
    private int duration;

    public Voice(String url, int duration) {
        this.url = url;
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public int getDuration() {
        return duration;
    }
}
