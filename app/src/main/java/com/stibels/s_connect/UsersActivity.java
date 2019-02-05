package com.stibels.s_connect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stibels.s_connect.Models.Users;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.stibels.s_connect.RegisterActivity.DEFAULT_IMAGE;


public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUserList;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        BindView();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void BindView() {
        mToolbar = findViewById(R.id.users_appbar);
        mUserList = findViewById(R.id.users_list);
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(mUserDatabase, Users.class).build();
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                final String user_uid = getRef(position).getKey();


                holder.userName.setText(model.getName());
                holder.status.setText(model.getStatus());
                Picasso.get().load(model.getThumb_image()).into(holder.image);


                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_uid);
                        startActivity(profileIntent);
                    }
                });


            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_single_layout, viewGroup, false);
                UsersViewHolder viewHolder = new UsersViewHolder(view);

                return viewHolder;
            }
        };
        mUserList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView status;
        CircleImageView image;
        View mview;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            userName = itemView.findViewById(R.id.user_single_name);
            status = itemView.findViewById(R.id.user_single_status);
            image = itemView.findViewById(R.id.user_single_image);
        }


    }
}
