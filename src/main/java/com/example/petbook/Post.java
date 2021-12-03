package com.example.petbook;

import android.util.Pair;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;




@IgnoreExtraProperties
public class Post {

        public String userName;
        public int likes;
        public ArrayList<String> likeUserName;
        public ArrayList<Pair<String,String>> comment;
        public String userID;
        public String date;
        public String content;

        public Post() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }

        public Post(String userID, String date, String content, String userName) {
            this.userID = userID;
            this.date = date;
            this.content = content;
            this.comment = new ArrayList<>();
            this.likeUserName = new ArrayList<>();
            this.likes = 0;
            this.userName = userName;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("userID",userID);
            result.put("date", date);
            result.put("likeUserName",likeUserName);
            result.put("content", content);
            result.put("userName",userName);
            result.put("likes",likes);
            result.put("comment",comment);
            return result;
        }
    }


