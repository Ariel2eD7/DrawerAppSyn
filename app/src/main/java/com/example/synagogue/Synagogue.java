package com.example.synagogue;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Synagogue implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String ownerId;
    private String ownerEmail;

    private String name;
    private String address;
    private String phone;
    private String description;

    private double latitude;
    private double longitude;

    private Map<String, Map<String, List<String>>> prayers;

    private Map<String, Boolean> features;

    private String status;

    public Synagogue() {
        // Required empty constructor for Firestore
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<String, Map<String, List<String>>> getPrayers() {
        return prayers;
    }

    public void setPrayers(
            Map<String, Map<String, List<String>>> prayers) {
        this.prayers = prayers;
    }

    public Map<String, Boolean> getFeatures() {
        return features;
    }

    public void setFeatures(
            Map<String, Boolean> features) {
        this.features = features;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasFeature(String key) {

        if (features == null) {
            return false;
        }

        return Boolean.TRUE.equals(
                features.get(key)
        );
    }

    public int getFeatureCount() {

        if (features == null) {
            return 0;
        }

        int count = 0;

        for (Boolean value : features.values()) {

            if (Boolean.TRUE.equals(value)) {
                count++;
            }
        }

        return count;
    }

    public int getPrayerCount() {

        if (prayers == null) {
            return 0;
        }

        int count = 0;

        for (Map<String, List<String>> day :
                prayers.values()) {

            if (day == null) {
                continue;
            }

            for (List<String> times :
                    day.values()) {

                if (times != null) {
                    count += times.size();
                }
            }
        }

        return count;
    }

    public boolean hasLocation() {

        return latitude != 0 &&
                longitude != 0;
    }
}
