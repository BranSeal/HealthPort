package com.amebas.healthport.Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.List;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseManager {

    private FirebaseFirestore db;

    public DatabaseManager(FirebaseFirestore db){
        this.db = db;
    }

    public Task<Void> addAccount(final Account account) {
        // create reference for Account, for use inside transaction
        final DocumentReference accountRef =
                db.collection("accounts").document(account.getEmail());

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
                    DocumentSnapshot documentSnapshot = task.getResult();
                    acc.setEmail(documentSnapshot.getString("email"));
                    acc.setPassword(documentSnapshot.getString("password"));
                    //best way i could think of to set the active account
                    instance.setSessionAccount(acc);
                    getProfilesWhenGettingAccount(account);
                    Log.d(TAG,
                            "Account Retrieved" +
                                    SessionManager.getInstance().getSessionAccount().getEmail());
                } else {
                    instance.setSessionAccount(null);
                }
            }
        });
    }

    private void getProfilesWhenGettingAccount(DocumentReference account){
        // Get Profiles per Account from FireStore
        CollectionReference profiles = account.collection("profiles");
        profiles.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                SessionManager instance = SessionManager.getInstance();
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    ArrayList<Profile> listOfProfiles = new ArrayList<>();
                    for(DocumentSnapshot d: documents) {
                        Profile newProf = new Profile();
                        newProf.setName(d.getString("name"));
                        newProf.setDob(d.getString("dob"));
                        newProf.setDocuments(d.getString("documents"));
                        listOfProfiles.add(newProf);
                    }
                    Account acc = instance.getSessionAccount();
                    acc.setProfiles(listOfProfiles);
                    instance.setSessionAccount(acc);
                }
            }
        });
    }
    public Task<DocumentSnapshot> doesAccountExist(String email) {
        DocumentReference t = db.collection("accounts").document(email);
        return t.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){

            // When "Get" Is Complete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
            }
        });
    }

    public Task<Void> updateAccount(final Account account) {
        DocumentReference accountRef = db.collection("accounts").document(account.getEmail());
        accountRef.update("email", account.getEmail());

        for(Profile p: account.getProfiles()) {
            updateProfile(p);
        }
        // Update profiles subcollection in account

        return accountRef.update("password", account.getPassword())
                .addOnSuccessListener(new OnSuccessListener <Void> () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // TODO: Add on success and on failure code
                    }
                });
    }

    public void deleteAccount(Account account) {
    }

    public void updateProfile(final Profile p) {
        // create reference for Profile, for use inside transaction
        SessionManager session = SessionManager.getInstance();
        DocumentReference profileReference =
                db.collection("accounts").document(
                        session.getSessionAccount().getEmail()).collection(
                        "profiles").document(
                        p.getName());
        profileReference.set(p, SetOptions.merge());
    }

    public Task<Void> addProfile(final Profile p, Account account) {
        // create reference for Profile, for use inside transaction
        DocumentReference profileReference =
                db.collection("accounts").document(
                        account.getEmail()).collection(
                        "profiles").document(
                        p.getName());

        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                transaction.set(profileReference, p);
                return null;
            }
        });
    }

    public void deleteProfile(Profile profile){

    }
}
