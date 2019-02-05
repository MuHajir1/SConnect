package com.stibels.s_connect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mDisplayName;
    private TextView mDisplayStatus;
    private ImageView mDisplayImage;
    private Button mSendRequestBtn;
    private Button mDeclineReqBtn;
    private TextView mTotalFriends;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mFriendReqDatabase;

    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BindView();
        final String UserKeyId = getIntent().getStringExtra("user_id");


        mCurrent_state = "not_friends";

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please Wait, sedang mengambil data user");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UserKeyId);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mDisplayName.setText(name);
                mDisplayStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                //----------- FRIENDS LIST / REQUEST FEATURE ----------
                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(UserKeyId)){
                            String req_type = dataSnapshot.child(UserKeyId).child("request_type").getValue().toString();

                            if(req_type.equals("received")){
                                mCurrent_state = "req_received";
                                mSendRequestBtn.setText("Accep Friend Request");
                            }
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDeclineReqBtn.setOnClickListener(this);
        mSendRequestBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        final String UserKeyId = getIntent().getStringExtra("user_id");
        switch (v.getId()) {
            case R.id.profile_send_req_btn:
                mSendRequestBtn.setEnabled(false);
                //----------------NOT FRIENDS STATE---------------
                if (mCurrent_state.equals("not_friends")) {
                    mFriendReqDatabase.child(mCurrent_user.getUid())
                            .child(UserKeyId)
                            .child("request_type")
                            .setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mFriendReqDatabase.child(UserKeyId)
                                                .child(mCurrent_user.getUid())
                                                .child("request_type")
                                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                mCurrent_state = "req_sent";
                                                mSendRequestBtn.setText("BATALKAN PERMINTAAN");
                                                mSendRequestBtn.setEnabled(true);

                                            }
                                        });
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Failed To Req", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                //-------------CANCEL REQUEST STATE -------------------
                if(mCurrent_state.equals("req_sent")){
                    mFriendReqDatabase.child(mCurrent_user.getUid())
                            .child(UserKeyId)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendReqDatabase.child(UserKeyId).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mSendRequestBtn.setEnabled(true);
                                        mCurrent_state = "not_friends";
                                        mSendRequestBtn.setText("MINTA PERTEMANAN");
                                        Toast.makeText(ProfileActivity.this, "Berhasil Menghapus", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed To Deleted", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
            case R.id.profile_decline_btn:

                break;
        }
    }

    private void BindView() {
        mDisplayImage = findViewById(R.id.profile_displayImage);
        mDisplayName = findViewById(R.id.profile_displayName);
        mDisplayStatus = findViewById(R.id.profile_displayStatus);
        mSendRequestBtn = findViewById(R.id.profile_send_req_btn);
        mTotalFriends = findViewById(R.id.profile_total_friends);
        mDeclineReqBtn = findViewById(R.id.profile_decline_btn);
    }
}
