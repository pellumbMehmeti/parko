package com.parkoKS.parko;

import java.sql.Timestamp;
import java.util.Date;

public class ManagementPost extends ManagementPostId{

    public String registration_plates;
    public String reservation_time;
    public  Date checkin_time;

    public ManagementPost(String registration_plates, String reservation_time,  Date checkin_time, Timestamp check_out_time) {
        this.registration_plates = registration_plates;
        this.reservation_time = reservation_time;
        this.checkin_time = checkin_time;
        this.check_out_time = check_out_time;
    }



    public   Date getCheck_in_time() {
        return checkin_time;
    }

    public void setCheck_in_time( Date check_in_time) {
        this.checkin_time = checkin_time;
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
