package com.deadk.halo.models;

import java.io.Serializable;
import java.util.Date;

public class FriendRequest implements Serializable {

    private String uid;
    private String date;

    public FriendRequest() {
    }

    public FriendRequest(String uid, String date) {
        this.uid = uid;
        this.date = date;
    }

    public FriendRequest(String uid, Date date){

        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();

        String dateStr = day + "/" + month +"/" +year;

        this.uid = uid;
        this.date = dateStr;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
