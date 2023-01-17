package com.saif.besocially.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.besocially.Adapters.notificationAdapter;
import com.saif.besocially.Models.Notification;
import com.saif.besocially.R;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {
RecyclerView recyclerView;
ArrayList<Notification> list;
FirebaseDatabase database;
    public NotificationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.notificationRv);
        list = new ArrayList<>();
//        list.add(new Notification(R.drawable.profile,"<b>Saifullah Khan</b> mention you in a comment","just Now"));
//        list.add(new Notification(R.drawable.profile2,"<b>Ali Haider</b> mention you in a comment","30 minutes ago"));
//        list.add(new Notification(R.drawable.profile4,"<b>Abrax Khan</b> mention you in a comment","1 hour ago"));
//        list.add(new Notification(R.drawable.profile3,"<b>Ashfaq Ahmad</b> mention you in a comment","1 day ago"));
//        list.add(new Notification(R.drawable.profile5,"<b>Abdul</b> mention you in a comment","2 days ago"));

        notificationAdapter adapter = new notificationAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            notification.setNotificationID(dataSnapshot.getKey());
                            list.add(notification);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return  view;
    }
}