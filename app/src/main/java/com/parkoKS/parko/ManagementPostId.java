package com.parkoKS.parko;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ManagementPostId {
    @Exclude
    public String ManagementPostId;

    public <T extends ManagementPostId> T withId(@NonNull final String id) {
        this.ManagementPostId = id;

        return (T) this;
    }
}
