package com.saif.besocially.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.besocially.Models.Notification;
import com.saif.besocially.Models.User;
import com.saif.besocially.R;
import com.saif.besocially.commentActivity;
import com.saif.besocially.databinding.NotificationRvSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.viewHolder> {
    ArrayList<Notification> list;
    Context context;

    public notificationAdapter(ArrayList<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv_sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Notification notification = list.get(position);
        String type = notification.getType();


        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(notification.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfile())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.binding.profileImage);

                        if (type.equals("like")) {
                            holder.binding.notification.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + " liked your post"));
                        } else if (type.equals("comment")) {
                            holder.binding.notification.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + " Commented on your post"));
                        } else {
                            holder.binding.notification.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + "  start following you"));

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!type.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("notification")
                            .child(notification.getPostedBy())
                            .child(notification.getNotificationID())
                            .child("checkOpen")
                            .setValue(true);
                    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Intent intent = new Intent(context, commentActivity.class);
                    intent.putExtra("postId", notification.getPostID());
                    intent.putExtra("postedBy", notification.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
        Boolean checkOpen = notification.isCheckOpen();
        if(checkOpen == true){
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }else{

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        NotificationRvSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationRvSampleBinding.bind(itemView);
        }
    }
}
