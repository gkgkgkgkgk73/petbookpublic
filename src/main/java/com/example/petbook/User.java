package com.example.petbook;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageTask;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class User {

    public String petBD="";
    public String userID="";
    public String userName="";
    public String userEmail="";
    public String petSpecies="";
    public String petName="";
    public String petGender="";
    public String alarmTime="";
    Map<String, Object> petInfo = new HashMap<>();
    public String profilePhotoPath="";
    public final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DatabaseReference usersRef = ref.child("users");
    ReadAndWriteSnippets readAndWriteSnippets = new ReadAndWriteSnippets(usersRef);


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }



    public User(String userID, String userName, String email) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = email;
        this.profilePhotoPath = "images/" + userID + "/profilePhoto";
    }

    public void uploadPetInfo(String petSpecies, String petBD, String petName,String petGender,String alarmTime){

        petInfo.put("petSpecies",petSpecies);
        petInfo.put("petBD", petBD);
        petInfo.put("petName", petName);
        petInfo.put("petGender",petGender);
        petInfo.put("alarmTime",alarmTime);
        readAndWriteSnippets.writePetInfo(userID, petInfo);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("userID", userID);
        result.put("userName", userName);
        result.put("userEmail",userEmail);

        return result;
    }



}
