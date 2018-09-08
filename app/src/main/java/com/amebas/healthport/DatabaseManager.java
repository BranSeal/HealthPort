package com.amebas.healthport;

import android.media.Rating;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import org.w3c.dom.Document;

import java.util.ArrayList;

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

    public void getAccount(String email, String password) {
        // Reference to Account in Firestore
        DocumentReference account = db.collection("accounts").document(email);

        // Get Account From FireStore
        account.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){

            // When "Get" Is Complete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                SessionManager instance = SessionManager.getInstance();
                if (task.isSuccessful()) {
                    Account acc = new Account();
                    ArrayList<Profile> arrList = new ArrayList<>();

                    DocumentSnapshot documentSnapshot = task.getResult();

                    acc.setEmail(documentSnapshot.getString("email"));
                    acc.setPassword(documentSnapshot.getString("password"));
                    String profiles = documentSnapshot.getString("profiles");
                    String[] arr = profiles.split(", ");
                    for(int i = 0; i < arr.length; i++) {
                        Profile prof = new Profile("Test", "Anush", "none");
                        arrList.add(prof);
                    }
                    acc.setProfiles(arrList);
                    //best way i could think of to set the active account
                    instance.setSessionAccount(acc);
                } else {
                    instance.setSessionAccount(null);
                }
            }
        });
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
