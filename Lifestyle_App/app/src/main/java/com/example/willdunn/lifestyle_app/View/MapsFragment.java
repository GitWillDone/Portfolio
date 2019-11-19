package com.example.willdunn.lifestyle_app.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.willdunn.lifestyle_app.R;

import androidx.appcompat.app.AppCompatActivity;

public class MapsFragment extends AppCompatActivity {

    private String userLocation = "geo:40.767778,-111.845205?q="; //default, but will change upon user location input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login_signin); //todo change this to be whatever ends up prompting for the user's location/button

        //Get the intent that created this activity.
        Intent receivedIntent = getIntent();

        //Get the string data of the location
        userLocation = "geo:" + receivedIntent.getStringExtra("BTN_STRING_LOC") + "?q=";

        if (userLocation.isEmpty()) {
            Toast.makeText(MapsFragment.this, "No location received", Toast.LENGTH_SHORT).show();
        } else {
            //We have to search for nearby hikes and construct a URI object from it.
            Uri searchUri = Uri.parse(userLocation + "hikes"); //hikes is a "magic number"

            //Create the implicit intent
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);

            //If there's an activity associated with this intent, launch it
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

