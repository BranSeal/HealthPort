package com.amebas.healthport;

public class Account {

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    private String email;
    private String password;
    private String profiles;

    public Account(){}

    public Account(String email, String password, String profiles){
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

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public void addProfile(String profile) {
        this.profiles += "," + profile;
    }
}
