package com.example.petbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment3 extends Fragment {
    //TODO 인스타 피드 1
    RecyclerView recyclerView;
    UploadSNSViewAdapter adapter;
    protected Context activity;
    ProgressDialog pd;

    @Override
            public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof Activity) activity = (Activity) context;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_3, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.detailviewfragment_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false));
        SharedPreferences sp = getContext().getSharedPreferences("sp",Context.MODE_PRIVATE);
        String userID = sp.getString("userID","");
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("/all-posts/");
        ArrayList<Post> contents = new ArrayList<>();
        ArrayList<Post> contents_get = new ArrayList<>();
        ArrayList<String> oldPost_get = new ArrayList<>();
        String[] oldestPostId = new String[1];

        dbref.limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener(){
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
                adapter = new UploadSNSViewAdapter(contents);
                recyclerView.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"오류발생",Toast.LENGTH_SHORT).show();
            }});

//스크롤 관련
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //마지막 체크
                if(!recyclerView.canScrollVertically(1)){
                    if(contents_get.size()==20){
                        pd = new ProgressDialog(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        pd.setMessage("더 불러오는 중...");
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();
                    }
                    dbref.orderByKey().endAt(oldestPostId[0]).limitToLast(20).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            contents_get.clear(); //임시저장 위치
                            oldPost_get.clear();

                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                for(DataSnapshot item2 : item.getChildren()){
                                    contents_get.add(0,item2.getValue(com.example.petbook.Post.class));
                                    oldPost_get.add(item2.getKey());
                                }
                            }
                            //불러오는 중인지, 전부 불러왔는지 if문
                            if(contents_get.size() > 1) {//1개라도 있으면 불러옴
                                //마지막 중복되는 부분 삭제
                                contents_get.remove(0);
                                //contents 뒤에 추가
                                contents.addAll(contents_get);
                                oldestPostId[0] = oldPost_get.get(0);
                                //메시지 갱신 위치
                                adapter.notifyDataSetChanged();
                            } else {
                               Toast.makeText(getActivity(),"끝",Toast.LENGTH_SHORT);
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





//이거 fragmemt3 닫는거

}