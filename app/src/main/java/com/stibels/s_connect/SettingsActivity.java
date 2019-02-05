package com.stibels.s_connect;

import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;

    private CircleImageView mDisplayImage;
    private TextView mDisplayName;
    private TextView mStatus;
    private Button mChangeImageButton;
    private Button mChangeStatusButton;
    private ProgressDialog mProgrsesDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BindView();
        mChangeStatusButton.setOnClickListener(this);
        mChangeImageButton.setOnClickListener(this);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mDisplayName.setText(name);
                mStatus.setText(status);
                Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void BindView() {
        mDisplayImage = findViewById(R.id.settings_image);
        mStatus = findViewById(R.id.settings_status);
        mDisplayName = findViewById(R.id.settings_display_name);
        mChangeImageButton = findViewById(R.id.settings_image_btn);
        mChangeStatusButton = findViewById(R.id.settings_status_btn);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_status_btn:
                String status_value = mStatus.getText().toString();
                Intent status_activity = new Intent(getApplicationContext(), StatusActivity.class);
                status_activity.putExtra("status_value", status_value);
                startActivity(status_activity);
                finish();
                break;
            case R.id.settings_image_btn:
                Crop.pickImage(this);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(data.getData(), destination).asSquare().start(this);
        } else if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {

                mProgrsesDialog = new ProgressDialog(SettingsActivity.this);
                mProgrsesDialog.setTitle("Uploading Image....");
                mProgrsesDialog.setMessage("Tunggu sebentar Ya :)");
                mProgrsesDialog.setCanceledOnTouchOutside(false);
                mProgrsesDialog.show();

                final String currentUid = mCurrentUser.getUid();


                Uri resultUri = Crop.getOutput(data);
                File thumb_filePath = new File(resultUri.getPath());


                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    StorageReference thumbnails = mImageStorage.child("profile_images").child("thumb").child(currentUid + ".jpg");
                    UploadTask uploadTask = thumbnails.putBytes(thumb_byte);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Task<Uri> alamat_thumb = task.getResult().getMetadata().getReference().getDownloadUrl();
                                alamat_thumb.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String thumb_link = uri.toString();
                                        mUserDatabase.child("thumb_image").setValue(thumb_link);
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Error Terjadi Bosqu", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }


                final StorageReference filepath = mImageStorage.child("profile_images").child(currentUid + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            Task<Uri> alamat = task.getResult().getMetadata().getReference().getDownloadUrl();
                            alamat.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String foto_link = uri.toString();
                                    mUserDatabase.child("image").setValue(foto_link).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mProgrsesDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Sukses mengupload", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "Error Terjadi Bosqu", Toast.LENGTH_LONG).show();
                            mProgrsesDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == Crop.RESULT_ERROR) {
                Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(9);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }*/
}
