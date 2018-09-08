package com.amebas.healthport;

/**
 * Simple singleton to hold a global variable for who is currently logged in.
 * Author: Brandon
 */
public class SessionManager {

    private static SessionManager instance = null;
    private Account account;
    private Profile currentProfile;


    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();
        return instance;
    }

    /**
     * Set who is currently logged in.
     * @param acc Account that will be tied to the logged in session
     */
    public void setSessionAccount(Account acc) {
        account = acc;
    }

    /**
     * Getter method of currently logged in account
     * @return Currently logged in account
     */
    public Account getSessionAccount() {
        return account;
    }

    /**
     * Logs current account out.
     */
    public void logOut(){
        instance = null;
    }

    /**
     * Sets the current profile being used inside of the app
     * @param profile profile to switch to
     */
    public void setCurrentProfile(Profile profile) {
        currentProfile = profile;
    }

    /**
     * Returns the profile that is currently being used.
     * @return current profile
     */
    public Profile getCurrentProfile() {
        return currentProfile;
    }
}