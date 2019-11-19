package com.example.willdunn.lifestyle_app.View;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.example.willdunn.lifestyle_app.Model.User;
import com.example.willdunn.lifestyle_app.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

public class LaunchActivity extends AppCompatActivity implements SignupFragment.OnDataPass {
    User person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        requestStoragePermission();
        requestCameraPermission();

        if (savedInstanceState == null) { //called when app is launched and user selects signup
            LoginFragment loginFragment = new LoginFragment();
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.frag_launch, loginFragment, "login_frag");
            fTrans.commit();
        }
    }

    @Override
    public void onDataPass(String[] data) {
        //possbily remove or change to LaunchActivity
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA}, 30);
    }
}

