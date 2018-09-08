package com.amebas.healthport;

import android.media.Rating;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import org.w3c.dom.Document;

public class DatabaseManager {

    private FirebaseFirestore db;

    public DatabaseManager(FirebaseFirestore db){
        this.db = db;
    }

    public Task<Void> addAccount(final Account account) {
        // create reference for Account, for use inside transaction
        final DocumentReference accountRef =
                db.collection("accounts").document(account.getEmail());

        /*
            TODO: Add logic to create profile collection with first profile
         */

        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                transaction.set(accountRef, account);
                return null;
            }
        });
    }

    public Account getAccount(String email, String password) {
        //add login validation in here

        // Reference to Account in Firestore
        DocumentReference account = db.collection("accounts").document(email);

        final Account[] acc = {null};
        acc[0] = new Account();
        // Get Account From FireStore
        account.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){

            // When "Get" Is Complete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    acc[0].setEmail(documentSnapshot.getString("email"));
                    acc[0].setPassword(documentSnapshot.getString("password"));
                    //we cant set strings in the place of the account;
                    //acc[0].setProfiles(documentSnapshot.getString("profiles"));
                }
            }
        });

        return acc[0];
    }

    public void updateAccount(Account account) {
    }

    public void deleteAccount(Account account) {

    }

    public void addProfile(Profile profile) {

    }

    public void deleteProfile(Profile profile){

    }

    public void getProfile(Profile profile) {

    }

    public void updateProfile(Profile profile) {

    }
}
