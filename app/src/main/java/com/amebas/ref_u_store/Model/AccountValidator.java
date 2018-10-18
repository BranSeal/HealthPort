package com.amebas.ref_u_store.Model;

/**
 * Interface for methods that determine what happens when an account's credentials are checked
 * against the database.
 */
public interface AccountValidator
{
    /**
     * Method to call when given email is not connected to an account in the database.
     */
    void invalidEmail();

    /**
     * Method to call when email is valid, but given password does not match the account's password.
     */
    void invalidPass();

    /**
     * Method to call when account credentials are correct.
     */
    void validCredentials();
}
