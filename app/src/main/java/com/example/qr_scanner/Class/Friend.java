package com.example.qr_scanner.Class;

import java.util.ArrayList;

public class Friend {
    private Messenger messenger;
    private ArrayList<String> friend_list;

    public Friend(Messenger messenger, ArrayList<String> friend_list) {
        this.messenger = messenger;
        this.friend_list = friend_list;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    public ArrayList<String> getFriend_list() {
        return friend_list;
    }

    public void setFriend_list(ArrayList<String> friend_list) {
        this.friend_list = friend_list;
    }
}
