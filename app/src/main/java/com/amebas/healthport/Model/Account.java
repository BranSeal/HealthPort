package com.amebas.healthport.Model;

import java.util.ArrayList;
import java.util.List;

public class Account {

    private String email;
    private String password;
    private List<Profile> profiles;

    public Account(){}

    public Account(String email, String password) {
        this(email, password, new ArrayList<>());
    }

    public Account(String email, String password, List<Profile> profiles){
        this.email = email;
        this.password = password;
        this.profiles = profiles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public void addProfile(Profile profile) {
        this.profiles.add(profile);
    }

    @Override
    public String toString() {
        return this.email + " " + this.password;
    }

    public void deleteProfile(Profile p) {
        profiles.remove(p);
    }
}