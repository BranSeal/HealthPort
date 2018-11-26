package com.amebas.ref_u_store.Model;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Manages interfacing with the database.
 */
public class DatabaseManager {

    private FirebaseFirestore db;

    public DatabaseManager(FirebaseFirestore db){
        this.db = db;
    }

    /**
     * Add Account into FireBase FireStore
     *
     * Asynchronously calls database's run transaction
     * Must add listener when calling this function
     *
     * @param account  Account to add into FireBase FireStore
     * @return void asynchronous task
     */
    public Task<Void> addAccount(final Account account) {
        // create reference for Account, for use inside transaction
        final DocumentReference accountRef = db.collection("accounts")
            .document(account.getEmail());

        return db.runTransaction(transaction ->
        {
            transaction.set(accountRef, account);
            return null;
        });
    }

    /**
     * Checks an account's credentials against the database.
     *
     * @param email     the email of the account to check.
     * @param password  the password to check against the account.
     * @param av        the actions to perform based on results of the check.
     * @return the async task of calling the database.
     */
    public Task<DocumentSnapshot> checkCredentials(String email, String password, AccountValidator av)
    {
        DocumentReference account = db.collection("accounts").document(email);
        return account.get()
            .addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot account_data = task.getResult();
                    if (account_data.getString("email") == null)
                    {
                        av.invalidEmail();
                    }
                    else
                    {
                        if (account_data.getString("password").equals(password))
                        {
                            av.validCredentials();
                        }
                        else
                        {
                            av.invalidPass();
                        }
                    }
                }
            });
    }

    /**
     * Get Account from FireBase FireStore
     *
     * Asynchronously calls database's get function for account, profiles, and documents
     * May not return account immediately
     *
     * @param email     email provided by user
     * @param password  password provided by user
     */
    public void getAccount(String email, String password)
    {
        // Reference to Account in Firestore
        DocumentReference account = db.collection("accounts").document(email);
        // Get Account From FireStore
        account.get().addOnCompleteListener(task ->
        {
            SessionManager instance = SessionManager.getInstance();
            if (task.isSuccessful()) {
                Account acc = new Account();
                DocumentSnapshot documentSnapshot = task.getResult();
                String cloudEmail = documentSnapshot.getString("email");
                String cloudPassword = documentSnapshot.getString("password");
                if(!email.equals(cloudEmail) || !password.equals(cloudPassword)) {
                    acc = null;
                    instance.setSessionAccount(acc);
                    Log.d(TAG,"Anush: " + "PassEntered: " + password + ", passCloud: " + cloudPassword);
                    Log.d(TAG,"Anush: " + "EmailEntered: " + email + ", emailCloud: " + cloudEmail);
                } else {
                    acc.setEmail(cloudEmail);
                    acc.setPassword(cloudPassword);
                    instance.setSessionAccount(acc);
                    getProfiles();
                    Log.d(TAG, "Account Retrieved"
                        + SessionManager.getInstance().getSessionAccount().getEmail());
                }
            } else {
                instance.setSessionAccount(null);
            }
        });
    }

    /**
     * Retrieves profiles from database for the logged in account
     */
    private void getProfiles() {
        SessionManager instance = SessionManager.getInstance();
        Account account = instance.getSessionAccount();
        // Get Profiles per Account from FireStore
        CollectionReference profiles = db.collection("accounts")
            .document(account.getEmail())
            .collection("profiles");
        profiles.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> firebaseProfiles = querySnapshot.getDocuments();
                for (DocumentSnapshot p: firebaseProfiles) {
                    Profile newProf = new Profile();
                    newProf.setName(p.getString("name"));
                    newProf.setDob(p.getString("dob"));
                    newProf.setId(p.getId());
                    account.addProfile(newProf);
                    getDocumentsWhenGettingProfile(instance, newProf);
                }
            }
        });
    }

    /**
     * Retrieves all documents to specified profile
     * @param instance session instance
     * @param profile profile for grabbing documents
     */
    private void getDocumentsWhenGettingProfile(SessionManager instance, Profile profile) {
        // FireBase FireStore instance
        FirebaseStorage storage = FirebaseStorage.getInstance();

        CollectionReference profileDocuments = db.collection("accounts")
            .document(instance.getSessionAccount().getEmail())
            .collection("profiles")
            .document(profile.getId())
            .collection("documents");

        // Create document instance for all of profile's documents.
        profileDocuments.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> firebaseDocuments = querySnapshot.getDocuments();
                for (DocumentSnapshot p: firebaseDocuments) {
                    File path = new File(p.getString("path"));
                    if (path.exists())
                    {
                        Document d = new Document();
                        d.setName(p.getString("name"));
                        d.setId(p.getId());
                        d.setPath(path.getAbsolutePath());
                        d.setTags((ArrayList<String>) p.get("tags"));
                        profile.addDocuments(d);
                        // Create a reference with an initial file path and name
                        StorageReference pathReference = storage.getReferenceFromUrl("gs://healthport-d91a6.appspot.com/" + d.getPath());

                        try {
                            File localFile = File.createTempFile("images", "jpg");

                            pathReference.getFile(localFile)
                                    .addOnSuccessListener(snapshot -> {})
                                    .addOnFailureListener(exception -> {});
                        } catch (IOException e) {
                            Log.d(TAG, "Cannot upload to local file. Threw exception" + e.toString());
                        }
                    }

                    //Delete instance of document in temporary folder if it exists
                    //ideally this code would sit in the server, but due to FireBase constraints,
                    //we place this here
                    try {
                        StorageReference image = storage.getReference().child("temp/" + p.getString("path"));
                        Log.d(TAG,"Image Path" + p.getString("path"));
                        image.delete();
                    } catch (Exception e) {
                        Log.d(TAG,"Could not delete temporary document" + e.toString());
                    }
                }
            }
        });
    }

    /**
     * Updates database instance of document with locally changed document
     * @param document Document to update
     * @param profile Profile holding document to update
     */
    public void updateDocumentInFireStore(Document document, Profile profile) {
        //Session Instance
        SessionManager instance = SessionManager.getInstance();

        //Update Document in FireStore
        if (document.getId() == "") {
            db.collection("accounts")
                .document(instance.getSessionAccount().getEmail())
                .collection("profiles")
                .document(profile.getId())
                .collection("documents")
                .add(document)
                .addOnSuccessListener(documentReference ->
                {
                    document.setId(documentReference.getId());
                });
        } else {
            DocumentReference profileDocument = db.collection("accounts")
                .document(instance.getSessionAccount().getEmail())
                .collection("profiles")
                .document(profile.getId())
                .collection("documents")
                .document(document.getId());
            profileDocument.set(document, SetOptions.merge());
        }

        //Upload file(s) to FireBase Storage
        uploadDocumentToStorage(document.getPath());
    }

    /**
     * Uploads document to FireBase Storage
     * @param referenceID location of document in FireBase Storage, NOT FireStore
     */
    public void uploadDocumentToStorage(String referenceID) {
        StorageReference image = FirebaseStorage.getInstance()
            .getReference()
            .child("images/" + referenceID);
        Uri file = Uri.fromFile(new File(referenceID));
        UploadTask uploadTask = image.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask
            .addOnFailureListener(exception -> Log.d(TAG,"Could not upload document" + exception.toString()))
            .addOnSuccessListener(snapshot -> {
                //TODO: Confirmation that file is uploaded
            });
    }

    /**
     * Deletes a document in the database. Deletes both the reference under a profile, as well
     * as the actual file.
     *
     * @param profile_id  the id of the profile to delete from.
     * @param id          the ID of the document in Firebase.
     * @param path        the path to the document in the cloud storage.
     */
    public void deleteDocument(String profile_id, String id, String path) {
        // FireBase FireStore instance
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Delete document reference under profile
        DocumentReference cloud_doc = db.collection("accounts")
            .document(SessionManager.getInstance().getSessionAccount().getEmail())
            .collection("profiles")
            .document(profile_id)
            .collection("documents")
            .document(id);
        cloud_doc.delete();

        // Delete actual document
        StorageReference image = storage.getReference().child("images/" + path);
        image.delete();
    }

    /** Gets actual PDF from storage, not just metadata from FireStore
     *  Places image in local storage
     * @param referenceID location of document in FireBase Storage, NOT FireStore
     */
    public void getDocumentFromStorage(String referenceID) {
        StorageReference pathReference = FirebaseStorage.getInstance()
            .getReferenceFromUrl("gs://healthport-d91a6.appspot.com/" + referenceID);
        try {
            File localFile = File.createTempFile("images", "jpg");
            pathReference.getFile(localFile)
                .addOnSuccessListener(snapshot ->
                {
                    // Local temp file has been created
                })
                .addOnFailureListener(exception ->
                {
                    // Handle any errors
                });
        }
        catch (IOException e) {
            Log.d(TAG, "Cannot upload to local file. Threw exception" + e.toString());
        }
    }

    /**
     * Returns account information if exists, null if it does not exist
     *
     * @param email  Account's Email and reference tag
     * @return Asynchronous DocumentSnapshot containing account data if it exists, null if not
     */
    public Task<DocumentSnapshot> doesAccountExist(String email) {
        DocumentReference t = db.collection("accounts").document(email);
        return t.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }

    /**
     * Updates Account in FireBase FireStore
     *
     * @param account  Account to be updated
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
            .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully updated account"));
    }

    /**
     * Updates profile in FireBase FireStore
     *
     * @param p  Profile to be updated
     */
    public void updateProfile(final Profile p) {
        // create reference for Profile, for use inside transaction
        SessionManager session = SessionManager.getInstance();
        DocumentReference profileReference = db.collection("accounts")
            .document(session.getSessionAccount().getEmail())
            .collection("profiles")
            .document(p.getId());
        profileReference.set(p, SetOptions.merge());
    }

    /**
     * Add Profile to Account.
     *
     * Asynchronously calls database's run transaction
     * May not add profile immediately
     *
     * @param p        Profile to be added
     * @param account  Account to be updated
     */
    public void addProfile(final Profile p, Account account) {
        db.collection("accounts")
            .document(account.getEmail())
            .collection("profiles")
            .add(p)
            .addOnSuccessListener(documentReference -> p.setId(documentReference.getId()));
    }

    /**
     * Deletes profile from account
     *
     * @param profile   profile to be deleted
     * @param account   account to be updated
     * @param listener  listener to perform on task completion.
     */
    public void deleteProfile(Profile profile, Account account, OnCompleteListener<Void> listener) {
        // create reference for Profile, for use inside transaction
        DocumentReference profileReference = db.collection("accounts")
            .document(account.getEmail())
            .collection("profiles")
            .document(profile.getId());
        for (Document d : profile.getDocuments()) {
            deleteDocument(profile.getId(), d.getId(), d.getPath());
        }
        profileReference.delete().addOnCompleteListener(listener);
    }
}
