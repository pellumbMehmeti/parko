package com.example.parko;

public class EmployeePost extends EmployeeId{

    public String profile_image_URI;
    public String user_name;

    public EmployeePost(String profile_image_URI, String user_name, String user_id) {
        this.profile_image_URI = profile_image_URI;
        this.user_name = user_name;
        this.user_id = user_id;
    }

    public String user_id;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public EmployeePost() {
    }

    public String getProfile_image_URI() {
        return profile_image_URI;
    }

    public void setProfile_image_URI(String profile_image_URI) {
        this.profile_image_URI = profile_image_URI;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
