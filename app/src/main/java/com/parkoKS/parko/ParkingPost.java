package com.parkoKS.parko;

public class ParkingPost extends  ParkingPostId {

    public String owner_id, location_info,profile_image_URI,parking_name,parking_image_URI;
    public Integer capacity_number;

    public Double latitude,longitude,price2hrs,price4hrs,price8hrs,pricemt8hrs,current_free_places;

    public ParkingPost(String owner_id, String location_info, String profile_image_URI, String parking_name, Integer capacity_number,Double current_free_places,
                       Double latitude, Double longitude, Double price2hrs, Double price4hrs, Double price8hrs, Double pricemt8hrs,String parking_image_URI) {
        this.owner_id = owner_id;
        this.location_info = location_info;
        this.profile_image_URI = profile_image_URI;
        this.parking_image_URI = parking_image_URI;
        this.parking_name = parking_name;
        this.capacity_number = capacity_number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price2hrs = price2hrs;
        this.price4hrs = price4hrs;
        this.price8hrs = price8hrs;
        this.pricemt8hrs = pricemt8hrs;
        this.current_free_places = current_free_places;
    }
    public ParkingPost(){}

    public String getParking_image_URI() {
        return parking_image_URI;
    }

    public Double getCurrent_free_places() {
        return current_free_places;
    }

    public void setCurrent_free_places(Double current_free_places) {
        this.current_free_places = current_free_places;
    }

    public void setParking_image_URI(String parking_image_URI) {
        this.parking_image_URI = parking_image_URI;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getLocation_info() {
        return location_info;
    }

    public void setLocation_info(String location_info) {
        this.location_info = location_info;
    }

    public String getProfile_image_URI() {
        return profile_image_URI;
    }

    public void setProfile_image_URI(String profile_image_URI) {
        this.profile_image_URI = profile_image_URI;
    }

    public String getParking_name() {
        return parking_name;
    }

    public void setParking_name(String parking_name) {
        this.parking_name = parking_name;
    }

    public Integer getCapacity_number() {
        return capacity_number;
    }

    public void setCapacity_number(Integer capacity_number) {
        this.capacity_number = capacity_number;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getPrice2hrs() {
        return price2hrs;
    }

    public void setPrice2hrs(Double price2hrs) {
        this.price2hrs = price2hrs;
    }

    public Double getPrice4hrs() {
        return price4hrs;
    }

    public void setPrice4hrs(Double price4hrs) {
        this.price4hrs = price4hrs;
    }

    public Double getPrice8hrs() {
        return price8hrs;
    }

    public void setPrice8hrs(Double price8hrs) {
        this.price8hrs = price8hrs;
    }

    public Double getPricemt8hrs() {
        return pricemt8hrs;
    }

    public void setPricemt8hrs(Double pricemt8hrs) {
        this.pricemt8hrs = pricemt8hrs;
    }
}
