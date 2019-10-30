package com.example.parko;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class EmployeeId {
    @Exclude
    public String EmployeeId;

    public <T extends EmployeeId> T withId(@NonNull final String id) {
        this.EmployeeId = id;

        return (T) this;
    }
}
