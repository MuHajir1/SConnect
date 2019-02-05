package com.stibels.s_connect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private Button mSignIn;
    private Button mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        BindView();
        ButtonFun();
    }

    private void ButtonFun() {
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_log = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(intent_log);
                finish();
            }
        });
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_reg = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intent_reg);
                finish();
            }
        });
    }

    private void BindView() {
        mSignIn = findViewById(R.id.sign_masuk_btn);
        mSignUp = findViewById(R.id.sign_up_btn);
    }
}
