package com.example.synagogue;

import java.util.ArrayList;
import java.util.HashMap;

public class Synagogue {
    private String id, name, address, phone, username;
    private double latitude, longitude;
    private ArrayList<HashMap<String, String>> openingHours;

    public Synagogue() {}

    public Synagogue(String id, String name, String address, String phone,
                     String username, double latitude, double longitude,
                     ArrayList<HashMap<String, String>> openingHours) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openingHours = openingHours;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public ArrayList<HashMap<String, String>> getOpeningHours() { return openingHours; }
    public void setOpeningHours(ArrayList<HashMap<String, String>> openingHours) {
        this.openingHours = openingHours;
    }
}
