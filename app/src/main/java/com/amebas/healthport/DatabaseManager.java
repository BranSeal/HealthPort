package com.amebas.healthport;

import android.icu.text.SimpleDateFormat;
import android.media.Rating;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.Toast;
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

import org.w3c.dom.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
                    //best way i could think of to set the active account
                    instance.setSessionAccount(acc);
                } else {
                    instance.setSessionAccount(null);
                }
            }
        });
      
        // Get Profiles per Account from FireStore
        CollectionReference profiles = account.collection("profiles");
        profiles.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                SessionManager instance = SessionManager.getInstance();
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    Profile[] listOfProfiles = new Profile[documents.size()];
                    int i = 0;
                    for(DocumentSnapshot d: documents) {
                        Profile newProf = new Profile();
                        newProf.setName(d.getString("name"));
                        newProf.setDob(d.getString("dob"));
                        newProf.setDocuments(d.getString("documents"));
                        listOfProfiles[i] = newProf;
                    }
                    Account acc = instance.getSessionAccount();
                    acc.setProfiles(listofProfiles);
                    instance.setSessionAccount(acc);
                }
            }
        });
    }

    public Task<DocumentSnapshot> doesAccountExist(String email) {
        DocumentReference t = db.collection("accounts").document(email);
        boolean doesExist = false;
        return t.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){

            // When "Get" Is Complete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    documentSnapshot.exists();
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
        final DocumentReference profileRef =
                p.getDocumentReference();
        profileRef.set(p, SetOptions.merge());
    }

    public Task<Void> addProfile(final Profile p) {
        // create reference for Profile, for use inside transaction
        final DocumentReference profileRef =
                p.getDocumentReference();

        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                transaction.set(profileRef, p);
                return null;
            }
        });
    }

    public void deleteProfile(Profile profile){

    }

    public Profile getProfile(Profile profile) {
        DocumentReference profileReference = profile.getDocumentReference();

        final Profile[] p = {null};
        p[0] = new Profile();
        // Get Account From FireStore
        profileReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){

            // When "Get" Is Complete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    p[0] = documentSnapshot.toObject(Profile.class);
                }
            }
        });

        return p[0];
    }
}
