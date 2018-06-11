package com.deadk.halo.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable,Comparable {

    private String uid;
    private String username;
    private String displayName;
    private String emailAddress;
    private String phoneNo;
    private String dateOfBirth;
    private String gender;

    public User(){

    }

    public User(String uid, String username, String displayName, String emailAddress, String phoneNo, String dateOfBirth, String gender) {
        this.uid = uid;
        this.username = username;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.phoneNo = phoneNo;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    @Override
    public int compareTo(@NonNull Object o) {
        User compareUser = (User)o;
        return this.getDisplayName().compareTo(((User) o).getDisplayName());
    }
}
