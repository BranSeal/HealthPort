package com.amebas.healthport.Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static android.content.ContentValues.TAG;

public class DatabaseManager {

    private FirebaseFirestore db;

    public DatabaseManager(FirebaseFirestore db){
        this.db = db;
    }

    /**Add Account into FireBase FireStore
     * Asynchronously calls database's run transation
     * Must add listener when calling this function
     * @param account Account to add into FireBase FireStore
     * @return void asynchronous task
     */
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

    /**Get Account from FireBase FireStore
     * Asynchronously calls database's get function for account, profiles, and documents
     * May not return account immediately
     * @param email email provided by user
     * @param password password provided by user
     */
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
                    String cloudEmail = documentSnapshot.getString("email");
                    String cloudPassword = documentSnapshot.getString("password");
                    if(!cloudEmail.equals(email) || !cloudPassword.equals(password)) {
                        acc = null;
                        instance.setSessionAccount(acc);
                        Log.d(TAG,"Anush: " + "PassEntered: " + password + ", passCloud: " + cloudPassword);
                        Log.d(TAG,"Anush: " + "EmailEntered: " + email + ", emailCloud: " + cloudEmail);
                    } else {
                        acc.setEmail(cloudEmail);
                        acc.setPassword(cloudPassword);
                        instance.setSessionAccount(acc);
                        getProfiles();
                        Log.d(TAG,
                                "Account Retrieved" +
                                        SessionManager.getInstance().getSessionAccount().getEmail());
                    }
                } else {
                    instance.setSessionAccount(null);
                }
            }
        });
    }

    private void getProfiles(){
        SessionManager instance = SessionManager.getInstance();
        Account account = instance.getSessionAccount();
        // Get Profiles per Account from FireStore
        CollectionReference profiles =
                db.collection("accounts").document(account.getEmail()).collection("profiles");
        profiles.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> firebaseProfiles = querySnapshot.getDocuments();
                    for(DocumentSnapshot p: firebaseProfiles) {
                        Profile newProf = new Profile();
                        newProf.setName(p.getString("name"));
                        newProf.setDob(p.getString("dob"));
                        account.addProfile(newProf);
                        getDocuments(instance, newProf);
                    }
                }
            }
        });
    }

    public void getDocuments(SessionManager instance, Profile profile) {
        // FireBase FireStore instance
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // FireBase CloudStorage Instance
        StorageReference storageRef = storage.getReference();

        CollectionReference profileDocuments =
                db.collection("accounts").document(
                        instance.getSessionAccount().getEmail()).collection(
                                "profiles").document(
                                        profile.getName()).collection("documents");

        profileDocuments.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> firebaseDocuments = querySnapshot.getDocuments();
                    for(DocumentSnapshot p: firebaseDocuments) {
                        Document d = new Document();
                        d.setName(p.getString("name"));
                        d.setReferenceIDs(p.getString("referenceIDs").split(","));
                        d.setTags(p.getString("tags").split(","));
                        profile.addDocuments(d);
                        for (String referenceID: d.getReferenceIDs()) {
                            // Create a reference with an initial file path and name
                            StorageReference pathReference = storage.getReferenceFromUrl("gs://healthport-d91a6.appspot.com/" + referenceID);

                            final long ONE_MEGABYTE = 1024 * 1024;
                            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Data for ref is returns, use this as needed
                                    instance.getSessionAccount().updateProfile(profile);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /** Returns account information if exists, null if it does not exist
     * @param email Account's Email and reference tag
     * @return Asynchronous DocumentSnapshot containing account data if it exists, null if not
     */
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

    /** Updates Account in FireBase FireStore
     * Must use listener when calling this function, utlizes database's asynchronous update function
     * @param account Account to be updated
     * @return void asynchronous task
     */
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
                        Log.d(TAG, "Successfully updated account");
                    }
                });
    }

    /** Updates profile in FireBase FireStore
     * @param p Profile to be updated
     */
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

    /** Add Profile to Account
     * Asynchronously calls database's run transaction
     * May not add profile immediately
     * @param p Profile to be added
     * @param account Account to be updated
     * @return
     */
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

    /** Deletes profile from account
     * @param profile profile to be deleted
     * @param account account to be updated
     */
    public void deleteProfile(Profile profile, Account account){
        // create reference for Profile, for use inside transaction
        DocumentReference profileReference =
                db.collection("accounts").document(
                        account.getEmail()).collection(
                        "profiles").document(
                        profile.getName());
        profileReference.delete().addOnCompleteListener(new OnCompleteListener<Void>(){

            // When "Get" Is Complete
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Deleted profile");
                } else {
                    Log.d(TAG, "Unable to delete profile");
                }
            }
        });
    }
}
