package com.saif.besocially.Fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.saif.besocially.Adapters.StoryAdapter;
import com.saif.besocially.Adapters.postAdapter;
import com.saif.besocially.Models.Post;
import com.saif.besocially.Models.Story;
import com.saif.besocially.Models.User;
import com.saif.besocially.Models.UserStories;
import com.saif.besocially.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {
    RecyclerView storyRV;
    ShimmerRecyclerView dashboardRV;
    ArrayList<Story> storylist;
    ArrayList<Post> postList;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    RoundedImageView addStoryImage;
    ActivityResultLauncher<String> galleryLauncher;
    ProgressDialog dialog;
    ImageView profileImage;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();


        //profile image code
        profileImage = view.findViewById(R.id.profileImage);
        database.getReference().child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.placeholder)
                                    .into(profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





        // post or dashboard Rv
        dashboardRV = view.findViewById(R.id.dashboardRv);
        dashboardRV.showShimmerAdapter();



        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);


        // story Rv
        storyRV = view.findViewById(R.id.storyRV);
        storylist = new ArrayList<>();

        StoryAdapter storyAdapter = new StoryAdapter(storylist, getContext());
        LinearLayoutManager storyLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRV.setLayoutManager(storyLayoutManager);
        storyRV.setNestedScrollingEnabled(false);
        storyRV.setAdapter(storyAdapter);

        database.getReference()
                .child("stories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            storylist.clear();
                            for(DataSnapshot storySnapshot: snapshot.getChildren()){
                                Story story = new Story();
                                story.setStoryBy(storySnapshot.getKey());
                                story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));

                                ArrayList<UserStories> stories = new ArrayList<>();
                                for(DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()){
                                    UserStories userStories = snapshot1.getValue(UserStories.class);
                                    stories.add(userStories);
                                }
                                 story.setStories(stories);
                                storylist.add(story);
                            }
                            storyAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        postList = new ArrayList<>();

        postAdapter dashboardAdapters = new postAdapter(postList, getContext());
        LinearLayoutManager dashboardlayoutManager = new LinearLayoutManager(getContext());
        dashboardRV.setLayoutManager(dashboardlayoutManager);
        dashboardRV.setNestedScrollingEnabled(false);

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostID(dataSnapshot.getKey());
                    postList.add(post);
                }
                dashboardRV.setAdapter(dashboardAdapters);
                dashboardRV.hideShimmerAdapter();
                dashboardAdapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addStoryImage = view.findViewById(R.id.storyImage);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        addStoryImage.setImageURI(result);
                        dialog.show();
                        final StorageReference reference = storage.getReference()
                                .child("stories")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(new Date().getTime() + "");
                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Story story = new Story();
                                        story.setStoryAt(new Date().getTime());
                                        database.getReference()
                                                .child("stories")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("postedBy")
                                                .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        UserStories stories = new UserStories(uri.toString(), story.getStoryAt());
                                                        database.getReference()
                                                                .child("stories")
                                                                .child(FirebaseAuth.getInstance().getUid())
                                                                .child("userStories")
                                                                .push()
                                                                .setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        dialog.dismiss();
                                                                        Toast.makeText(getContext(), "Story Uploaded", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                });

        return view;
    }
}