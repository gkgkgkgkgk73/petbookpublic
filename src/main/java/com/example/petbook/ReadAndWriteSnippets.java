package com.example.petbook;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReadAndWriteSnippets {

    private static final String TAG = "ReadAndWriteSnippets";

    private static int count=0;
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    // [END declare_database_ref]

    public ReadAndWriteSnippets(DatabaseReference database) {
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
    }



    public void writeNewUserWithTaskListeners(String userID, String name, String email, Uri photo) {
        //TODO user 정보 보내기
        User user = new User(userID, name, email);
//        StorageActivity storageActivity = new StorageActivity(userId,photo, "profilePhoto");
        // [START rtdb_write_new_user_task]

        mDatabase.child("users").child(userID).setValue(user.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                    }
                });

        // [END rtdb_write_new_user_task]
    }

    public void writePetInfo(String userID, Map<String,Object> petInfo){
        mDatabase.child("users").child(userID).updateChildren(petInfo);
    }

    private void addPostEventListener(DatabaseReference mPostReference) {
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]
    }

    //TODO 다이어리 보낼때 ReadAndWriteSnippets readAndWriteSnippets = new ~~~; readAndWriteSnippets.writeNewPost(userId, date, title, body);
    // [START write_fan_out]
    public void writeNewPost(String userID, String date, String body,String userName) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        //String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userID, date, body, userName);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        //모든 post를 한번에 저장해서 sns 피드 만드는 곳
        Map<String, Object> childUpdatesEveryUser = new HashMap<>();

        childUpdates.put("/user-posts/" + userID + "/" + date, postValues);
        childUpdatesEveryUser.put("/all-posts/"+date +"/"+userID,postValues);
        mDatabase.updateChildren(childUpdatesEveryUser);
        mDatabase.updateChildren(childUpdates);

    }
    // [END write_fan_out]
    


    public void updateLike(String userID, String date, String likeUserID,int m){
        mDatabase.child("/user-posts/"+userID+"/"+date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                post.likes+=m;
                mDatabase.child("/user-posts/"+userID+"/"+date).updateChildren(post.toMap());
                mDatabase.child("/all-posts/"+date+"/"+userID).updateChildren(post.toMap());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    // [START post_stars_transaction]
//    private void onStarClicked(DatabaseReference postRef) {
//        postRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Post p = mutableData.getValue(Post.class);
//                if (p == null) {
//                    return Transaction.success(mutableData);
//                }
//
//                if (p.stars.containsKey(getUid())) {
//                    // Unstar the post and remove self from stars
//                    p.starCount = p.starCount - 1;
//                    p.stars.remove(getUid());
//                } else {
//                    // Star the post and add self to stars
//                    p.starCount = p.starCount + 1;
//                    p.stars.put(getUid(), true);
//                }
//
//                // Set value and report transaction success
//                mutableData.setValue(p);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean committed,
//                                   DataSnapshot currentData) {
//                // Transaction completed
//                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
//            }
//        });
//    }
    // [END post_stars_transaction]

    // [START post_stars_increment]
//    private void onStarClicked(String uid, String key) {
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("posts/"+key+"/stars/"+uid, true);
//        updates.put("posts/"+key+"/starCount", ServerValue.increment(1));
//        updates.put("user-posts/"+uid+"/"+key+"/stars/"+uid, true);
//        updates.put("user-posts/"+uid+"/"+key+"/starCount", ServerValue.increment(1));
//        mDatabase.updateChildren(updates);
//    }
    // [END post_stars_increment]
}
