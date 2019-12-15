package com.parkoKS.parko;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ParkingGridPostId {

    @Exclude
    public String ParkingGridPostId;

    public <T extends ParkingGridPostId> T withId(@NonNull final String id) {
        this.ParkingGridPostId = id;

        return (T) this;
    }
}
