package com.example.parko;

import java.sql.Timestamp;

public class ManagementPost extends ManagementPostId{

    public String registration_plates;

    public ManagementPost(String registration_plates, String reservation_time, Timestamp check_in_time, Timestamp check_out_time) {
        this.registration_plates = registration_plates;
        this.reservation_time = reservation_time;
        this.check_in_time = check_in_time;
        this.check_out_time = check_out_time;
    }

    public String reservation_time;
    public Timestamp check_in_time;

    public Timestamp getCheck_in_time() {
        return check_in_time;
    }

    public void setCheck_in_time(Timestamp check_in_time) {
        this.check_in_time = check_in_time;
    }

    public Timestamp getCheck_out_time() {
        return check_out_time;
    }

    public void setCheck_out_time(Timestamp check_out_time) {
        this.check_out_time = check_out_time;
    }

    public Timestamp check_out_time;

    public ManagementPost(){}


    public String getRegistration_plates() {
        return registration_plates;
    }

    public void setRegistration_plates(String registration_plates) {
        this.registration_plates = registration_plates;
    }

    public String getReservation_time() {
        return reservation_time;
    }

    public void setReservation_time(String reservation_time) {
        this.reservation_time = reservation_time;
    }



}
