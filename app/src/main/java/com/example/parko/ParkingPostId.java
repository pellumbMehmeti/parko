package com.example.parko;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ParkingPostId {

    @Exclude
    public String ParkingPostId;

    public <T extends ParkingPostId> T withId(@NonNull final String id) {
        this.ParkingPostId = id;

        return (T) this;
    }
}
