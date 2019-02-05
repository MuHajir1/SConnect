package com.stibels.s_connect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button mRegisterBtn, mBack;
    private EditText mName, mEmail, mPassword;

    private ProgressDialog mProgress;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public final static String DEFAULT_IMAGE = "https://firebasestorage.googleapis.com/v0/b/sconnect-f12fc.appspot.com/o/profile_images%2Fdefault_avatar.png?alt=media&token=bdccdff7-edef-4f76-9550-7a26cda0256d";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //instansiasi firebase
        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);
        BindViews();
        ButtonFun();

    }

    private void ButtonFun() {
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = mName.getText().toString();
                String display_email = mEmail.getText().toString();
                String display_password = mPassword.getText().toString();
                if(!TextUtils.isEmpty(display_name)&& !TextUtils.isEmpty(display_email)&& !TextUtils.isEmpty(display_password)) {

                    mProgress.setTitle("Mendaftarkan Akun Anda");
                    mProgress.setMessage("Mohon bersabar yaa :)");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();

                    register_user(display_name, display_email, display_password);
                }else{
                    Toast.makeText(RegisterActivity.this,"Semua harus terisi",Toast.LENGTH_SHORT).show();
                }

            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,StartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void register_user(final String display_name, String display_email, String display_password) {
        mAuth.createUserWithEmailAndPassword(display_email, display_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name",display_name);
                            userMap.put("status","Hi there, saya menggunakan aplikasi chat");
                            userMap.put("image",DEFAULT_IMAGE);
                            userMap.put("thumb_image",DEFAULT_IMAGE);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgress.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                            /*

                            */
                        } else {
                            // If sign in fails, display a message to the user.
                            mProgress.hide();

                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }

    private void BindViews() {
        mRegisterBtn = findViewById(R.id.regiter_btn);
        mBack = findViewById(R.id.back_reg);
        mName = findViewById(R.id.et_display_name);
        mEmail = findViewById(R.id.et_display_email);
        mPassword = findViewById(R.id.et_display_password);
    }
}
