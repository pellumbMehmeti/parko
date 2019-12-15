package com.parkoKS.parko;

import com.google.firebase.Timestamp;

public class ReservationPost {

    public String reservation_date,parkingId,parking_owner_id,reserved_by,registration_plates,reservation_type;
    public Double price;
   // public Timestamp checkout_time,checkin_time;
    public ReservationPost() {
    }
    public ReservationPost(String reservation_date, String parkingId, String parking_owner_id, String reserved_by, String registration_plates, String reservation_type, Double price) {
        this.reservation_date = reservation_date;
        this.parkingId = parkingId;
        this.parking_owner_id = parking_owner_id;
        this.reserved_by = reserved_by;
        this.registration_plates = registration_plates;
        this.reservation_type = reservation_type;
        this.price = price;
       // this.checkout_time = checkout_time;
        //this.checkin_time = checkin_time;
    }



    public String getReservation_date() {
        return reservation_date;
    }

    public void setReservation_date(String reservation_date) {
        this.reservation_date = reservation_date;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getParking_owner_id() {
        return parking_owner_id;
    }

    public void setParking_owner_id(String parking_owner_id) {
        this.parking_owner_id = parking_owner_id;
    }

    public String getReserved_by() {
        return reserved_by;
    }

    public void setReserved_by(String reserved_by) {
        this.reserved_by = reserved_by;
    }

    public String getRegistration_plates() {
        return registration_plates;
    }

    public void setRegistration_plates(String registration_plates) {
        this.registration_plates = registration_plates;
    }

    public String getReservation_type() {
        return reservation_type;
    }

    public void setReservation_type(String reservation_type) {
        this.reservation_type = reservation_type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

 /*   public Timestamp getCheckout_time() {
        return checkout_time;
    }

    public void setCheckout_time(Timestamp checkout_time) {
        this.checkout_time = checkout_time;
    }

    public Timestamp getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(Timestamp checkin_time) {
        this.checkin_time = checkin_time;
    }*/
}
