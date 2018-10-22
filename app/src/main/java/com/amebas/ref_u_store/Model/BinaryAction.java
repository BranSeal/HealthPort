package com.amebas.ref_u_store.Model;

/**
 * Interface to provide actions for when there are two choices.
 */
public interface BinaryAction
{
    /**
     * Action to perform given "Yes".
     */
    void confirmAction();

    /**
     * Action to perform given "No".
     */
    void denyAction();
}
