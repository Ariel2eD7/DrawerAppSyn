package com.example.synagogue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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

    // =========================================================
    // Prayers
    //
    // Firestore may contain old and new structures.
    //
    // New:
    //
    // prayers
    //   sunday
    //      [
    //          {
    //              type: "normal",
    //              title: "...",
    //              content: "..."
    //          }
    //      ]
    //
    // Old data may contain a List instead of a Map.
    //
    // Therefore Object is used here to prevent Firestore
    // deserialization crashes.
    // =========================================================

    private Object prayers;

    private Map<String, Boolean> features;

    private String status;

    public Synagogue() {
        // Required empty constructor for Firestore
    }

    // =========================================================
    // ID
    // =========================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // =========================================================
    // Owner
    // =========================================================

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

    // =========================================================
    // Basic Details
    // =========================================================

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

    // =========================================================
    // Location
    // =========================================================

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

    // =========================================================
    // Prayers
    // =========================================================

    public Object getPrayers() {
        return prayers;
    }

    public void setPrayers(Object prayers) {
        this.prayers = prayers;
    }

    // =========================================================
    // Returns prayers in the NEW structure only
    //
    // If Firestore contains an old List structure,
    // an empty Map is returned instead of crashing.
    // =========================================================

    @SuppressWarnings("unchecked")
    public Map<String, List<Map<String, String>>> getPrayerMap() {

        Map<String, List<Map<String, String>>> result =
                new HashMap<>();

        if (!(prayers instanceof Map)) {
            return result;
        }

        Map<?, ?> rawMap = (Map<?, ?>) prayers;

        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {

            if (!(entry.getKey() instanceof String)) {
                continue;
            }

            String day = (String) entry.getKey();

            Object value = entry.getValue();

            if (!(value instanceof List)) {
                continue;
            }

            List<?> rawRows = (List<?>) value;

            List<Map<String, String>> rows =
                    new ArrayList<>();

            for (Object rawRow : rawRows) {

                if (!(rawRow instanceof Map)) {
                    continue;
                }

                Map<?, ?> rawRowMap =
                        (Map<?, ?>) rawRow;

                Map<String, String> row =
                        new HashMap<>();

                for (Map.Entry<?, ?> rowEntry :
                        rawRowMap.entrySet()) {

                    if (rowEntry.getKey() == null) {
                        continue;
                    }

                    if (rowEntry.getValue() == null) {
                        continue;
                    }

                    row.put(
                            String.valueOf(rowEntry.getKey()),
                            String.valueOf(rowEntry.getValue())
                    );
                }

                rows.add(row);
            }

            result.put(day, rows);
        }

        return result;
    }

    // =========================================================
    // Features
    // =========================================================

    public Map<String, Boolean> getFeatures() {
        return features;
    }

    public void setFeatures(
            Map<String, Boolean> features) {

        this.features = features;
    }

    // =========================================================
    // Status
    // =========================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // =========================================================
    // Has Feature
    // =========================================================

    public boolean hasFeature(String key) {

        if (features == null || key == null) {
            return false;
        }

        return Boolean.TRUE.equals(
                features.get(key)
        );
    }

    // =========================================================
    // Feature Count
    // =========================================================

    public int getFeatureCount() {

        if (features == null) {
            return 0;
        }

        int count = 0;

        for (Boolean value :
                features.values()) {

            if (Boolean.TRUE.equals(value)) {
                count++;
            }
        }

        return count;
    }

    // =========================================================
    // Prayer Count
    //
    // Counts only valid prayer/activity rows.
    // Headers are ignored.
    // Old/invalid Firestore structures are ignored safely.
    // =========================================================

    public int getPrayerCount() {

        Map<String, List<Map<String, String>>> prayerMap =
                getPrayerMap();

        if (prayerMap.isEmpty()) {
            return 0;
        }

        int count = 0;

        for (List<Map<String, String>> rows :
                prayerMap.values()) {

            if (rows == null) {
                continue;
            }

            for (Map<String, String> row :
                    rows) {

                if (row == null) {
                    continue;
                }

                String type =
                        row.get("type");

                if ("header".equalsIgnoreCase(type)) {
                    continue;
                }

                String title =
                        row.get("title");

                String content =
                        row.get("content");

                boolean hasTitle =
                        title != null &&
                                !title.trim().isEmpty();

                boolean hasContent =
                        content != null &&
                                !content.trim().isEmpty();

                if (hasTitle || hasContent) {
                    count++;
                }
            }
        }

        return count;
    }

    // =========================================================
    // Has Location
    // =========================================================

    public boolean hasLocation() {

        return latitude != 0.0 &&
                longitude != 0.0;
    }
}
