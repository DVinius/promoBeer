package br.com.vsgdev.promobeer.models;

import android.graphics.Bitmap;

import java.util.Calendar;

public class Item {
    private Integer id;
    private String name;
    private Double price;
    private Calendar createdAt;
    private Place place;
    private String obs;
    private Bitmap picture;
    private ItemType itemType;

    public Item(Integer id) {
        this.id = id;
    }

    public Item(String name, Double price) {
        this.name = name;
        this.price = price;
        this.createdAt = Calendar.getInstance();
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof  Item){
            return getId() == ((Item) object).getId();
        }
        return super.equals(object);
    }
}