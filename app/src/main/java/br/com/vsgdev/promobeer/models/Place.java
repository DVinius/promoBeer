package br.com.vsgdev.promobeer.models;

import android.location.Location;

public class Place {
    private Integer id;
    private String name;
    private Location location;

    public Place() {
    }

    public Place(String name) {
        this.name = name;
    }

    public Place(String name, Location location) {
        this.name = name;
        this.location = location;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
