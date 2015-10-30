package br.com.vsgdev.promobeer.models;

import android.graphics.Bitmap;

public class User {
    private Long fid;
    private String name;
    private Bitmap picture;

    public Long getFid() {
        return fid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
