package com.lucas.go4lunch.Utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lucas.go4lunch.Models.ProfileFile.Rate;
import com.lucas.go4lunch.Models.ProfileFile.User;

import androidx.annotation.NonNull;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, String email, String dayRestaurant) {
        User userToCreate = new User(uid, username, urlPicture, email, dayRestaurant);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    public static Task<Void> addRate(String restaurantId, int rate, String uid) {
        Rate rateToCreate = new Rate(rate);
        return UserHelper.getUsersCollection().document(uid).collection("rate").document(restaurantId).set(rateToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Query getAllUser(){
        return UserHelper.getUsersCollection()
                .orderBy("dayRestaurant")
                .limit(50);
    }

    public static Query getAllUserRestaurant(String restaurantId){
        return UserHelper.getUsersCollection()
                .whereEqualTo("dayRestaurant", restaurantId)
                .limit(50);
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateDayRestaurant(String restaurantId, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("dayRestaurant", restaurantId);
    }

    public static Task<Void> updateUrlPicture(String url, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("urlPicture", url);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}