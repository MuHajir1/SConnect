package com.stibels.s_connect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mLoginBtn;
    private Button mBackBtn;
    private EditText mEmail;
    private EditText mPassword;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        BindView();

        mProgress = new ProgressDialog(this);
        mLoginBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
    }



    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mProgress.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            mProgress.hide();
                            Toast.makeText(LoginActivity.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();


                        }

                        // ...
                    }
                });
    }

    private void BindView() {
        mLoginBtn = findViewById(R.id.login_in_btn);
        mBackBtn = findViewById(R.id.back_login);
        mEmail = findViewById(R.id.et_email_login);
        mPassword = findViewById(R.id.et_password_login);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_in_btn :

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {

                    mProgress.setTitle("Sedang Login");
                    mProgress.setMessage("tunggu sebentar");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();

                    loginUser(email,password);
                }else {
                    Toast.makeText(LoginActivity.this,"Tidak boleh ada yang kosong",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.back_login :
                startActivity(new Intent(this,StartActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                break;

        }

    }
}
