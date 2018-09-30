package com.amebas.healthport.Model;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Simple singleton to hold a global variable for who is currently logged in.
 * Author: Brandon
 */
public class SessionManager {

    private static SessionManager instance = null;
    private Account account;
    private Profile currentProfile;
    private DatabaseManager dbMgr = new DatabaseManager(FirebaseFirestore.getInstance());


    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();
        return instance;
    }

    /**
     * Gets database manager.
     *
     * @return database manager.
     */
    public DatabaseManager getDatabase() {
        return this.dbMgr;
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

    public void addProfile(Profile p) {
        this.account.addProfile(p);
        dbMgr.addProfile(p, this.account);
    }

    public void deleteProfile(Profile p) {
        this.account.deleteProfile(p);
        dbMgr.deleteProfile(p, this.account);
    }
}