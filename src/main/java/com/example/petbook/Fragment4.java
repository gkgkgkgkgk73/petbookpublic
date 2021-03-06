package com.example.petbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment4 extends Fragment {
    RecyclerView recyclerView;
    UploadGridAdapter adapter;
    protected Context activity;
    ProgressDialog pd;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof Activity) activity = (Activity) context;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_4, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.gridviewfragment_recycleview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        SharedPreferences sp = getContext().getSharedPreferences("sp",Context.MODE_PRIVATE);
        String userID = sp.getString("userID","");
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("/all-posts/");
        ArrayList<Post> contents = new ArrayList<>();
        ArrayList<Post> contents_get = new ArrayList<>();
        ArrayList<String> oldPost_get = new ArrayList<>();
        String[] oldestPostId = new String[1];

        dbref.limitToLast(15).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item : dataSnapshot.getChildren()){
                    for(DataSnapshot item2 : item.getChildren()){

                        contents.add(0,item2.getValue(com.example.petbook.Post.class));
                        contents_get.add(0,item2.getValue(com.example.petbook.Post.class));
                        oldPost_get.add(item2.getKey());
                    }
                }
                oldestPostId[0] = oldPost_get.get(0);
                adapter = new UploadGridAdapter(contents);
                recyclerView.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"????????????",Toast.LENGTH_SHORT).show();
            }});

//????????? ??????
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //????????? ??????
                if(!recyclerView.canScrollVertically(1)){
                    if(contents_get.size()==20){
                        pd = new ProgressDialog(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        pd.setMessage("??? ???????????? ???...");
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();
                    }
                    dbref.orderByKey().endAt(oldestPostId[0]).limitToLast(20).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            contents_get.clear(); //???????????? ??????
                            oldPost_get.clear();

                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                for(DataSnapshot item2 : item.getChildren()){
                                    contents_get.add(0,item2.getValue(com.example.petbook.Post.class));
                                    oldPost_get.add(item2.getKey());
                                }
                            }
                            //???????????? ?????????, ?????? ??????????????? if???
                            if(contents_get.size() > 1) {//1????????? ????????? ?????????
                                //????????? ???????????? ?????? ??????
                                contents_get.remove(0);
                                //contents ?????? ??????
                                contents.addAll(contents_get);
                                oldestPostId[0] = oldPost_get.get(0);
                                //????????? ?????? ??????
                                adapter.notifyDataSetChanged();
                            } else {
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });



        return rootview;
    }





//?????? fragmemt3 ?????????

}

