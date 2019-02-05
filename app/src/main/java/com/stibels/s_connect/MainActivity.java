package com.stibels.s_connect;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private ViewPager mViewPager;

    private FirebaseAuth mAuth;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        BindViews();
        ToolbarFun();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //tabs
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void ToolbarFun() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Expo Chat");

    }

    private void BindViews() {

        mToolbar = findViewById(R.id.main_page_toolbar);
        mViewPager = findViewById(R.id.main_tabPager);
        mTabLayout = findViewById(R.id.main_tabs);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        }
    }

    private void sendToStart() {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.main_logout_btn :
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
            case R.id.main_setting_btn :
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
            case R.id.main_allusers_btn :
                startActivity(new Intent(MainActivity.this,UsersActivity.class));
                break;
        }


        return true;
    }

}
