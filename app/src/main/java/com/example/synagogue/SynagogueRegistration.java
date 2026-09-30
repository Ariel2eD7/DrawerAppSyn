package com.example.synagogue;

import java.util.ArrayList;
import java.util.List;

public class SynagogueRegistration {

    // =========================
    // Basic information
    // =========================

    private String name;
    private String phone;
    private String description;

    // =========================
    // Location
    // =========================

    private String city;
    private String street;
    private String houseNumber;
    private double latitude;
    private double longitude;

    // =========================
    // Prayer times
    // =========================

    private List<PrayerTime> weekdayPrayerTimes = new ArrayList<>();
    private List<PrayerTime> fridayPrayerTimes = new ArrayList<>();
    private List<PrayerTime> saturdayPrayerTimes = new ArrayList<>();

    // =========================
    // Features
    // =========================

    private boolean wheelchairAccessible;
    private boolean accessibleRestrooms;
    private boolean accessibleParking;
    private boolean elevator;

    private boolean womenSection;
    private boolean childrenArea;
    private boolean childrenActivities;

    private boolean barMitzvah;
    private boolean batMitzvah;
    private boolean eventHall;

    private boolean torahClasses;
    private boolean youngMinyan;
    private boolean kollel;
    private boolean communityActivities;

    // =========================
    // Account
    // =========================

    private String ownerPhone;
    private boolean phoneVerified;

    // =========================
    // Getters & Setters
    // =========================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
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

    public List<PrayerTime> getWeekdayPrayerTimes() {
        return weekdayPrayerTimes;
    }

    public List<PrayerTime> getFridayPrayerTimes() {
        return fridayPrayerTimes;
    }

    public List<PrayerTime> getSaturdayPrayerTimes() {
        return saturdayPrayerTimes;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public boolean isAccessibleRestrooms() {
        return accessibleRestrooms;
    }

    public void setAccessibleRestrooms(boolean accessibleRestrooms) {
        this.accessibleRestrooms = accessibleRestrooms;
    }

    public boolean isAccessibleParking() {
        return accessibleParking;
    }

    public void setAccessibleParking(boolean accessibleParking) {
        this.accessibleParking = accessibleParking;
    }

    public boolean isElevator() {
        return elevator;
    }

    public void setElevator(boolean elevator) {
        this.elevator = elevator;
    }

    public boolean isWomenSection() {
        return womenSection;
    }

    public void setWomenSection(boolean womenSection) {
        this.womenSection = womenSection;
    }

    public boolean isChildrenArea() {
        return childrenArea;
    }

    public void setChildrenArea(boolean childrenArea) {
        this.childrenArea = childrenArea;
    }

    public boolean isChildrenActivities() {
        return childrenActivities;
    }

    public void setChildrenActivities(boolean childrenActivities) {
        this.childrenActivities = childrenActivities;
    }

    public boolean isBarMitzvah() {
        return barMitzvah;
    }

    public void setBarMitzvah(boolean barMitzvah) {
        this.barMitzvah = barMitzvah;
    }

    public boolean isBatMitzvah() {
        return batMitzvah;
    }

    public void setBatMitzvah(boolean batMitzvah) {
        this.batMitzvah = batMitzvah;
    }

    public boolean isEventHall() {
        return eventHall;
    }

    public void setEventHall(boolean eventHall) {
        this.eventHall = eventHall;
    }

    public boolean isTorahClasses() {
        return torahClasses;
    }

    public void setTorahClasses(boolean torahClasses) {
        this.torahClasses = torahClasses;
    }

    public boolean isYoungMinyan() {
        return youngMinyan;
    }

    public void setYoungMinyan(boolean youngMinyan) {
        this.youngMinyan = youngMinyan;
    }

    public boolean isKollel() {
        return kollel;
    }

    public void setKollel(boolean kollel) {
        this.kollel = kollel;
    }

    public boolean isCommunityActivities() {
        return communityActivities;
    }

    public void setCommunityActivities(boolean communityActivities) {
        this.communityActivities = communityActivities;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    // =========================
    // PrayerTime
    // =========================

    public static class PrayerTime {

        private String prayerName;
        private String time;

        public PrayerTime() {
        }

        public PrayerTime(String prayerName, String time) {
            this.prayerName = prayerName;
            this.time = time;
        }

        public String getPrayerName() {
            return prayerName;
        }

        public void setPrayerName(String prayerName) {
            this.prayerName = prayerName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
