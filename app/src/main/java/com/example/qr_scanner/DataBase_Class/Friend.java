package com.example.qr_scanner.DataBase_Class;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class Friend {
    private Messenger messenger;
    private ArrayList<String> friend_list;
    private DatabaseReference reference;
    public Friend(Messenger messenger, DatabaseReference reference) {
        this.messenger = messenger;
        this.reference = reference;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public void setReference(DatabaseReference reference) {
        this.reference = reference;
    }
}
