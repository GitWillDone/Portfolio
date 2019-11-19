package com.example.willdunn.lifestyle_app.View;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.Model.StepTracker;
import com.example.willdunn.lifestyle_app.Model.User;
import com.example.willdunn.lifestyle_app.R;
import com.example.willdunn.lifestyle_app.ViewModel.ProfileViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer; //used for the navigation pane
    private RegimenFragment regimenFragment;
    private SignupFragment signupFragment;
    private Bundle fragmentBundle;
    private TextView mNameOutput, mAgeOutput, mHeightOutput, mWeightOutput, mGoalOutput;
    private ImageView mProfilePicture;
    private ProfileViewModel mProfileViewModel;

    private FusedLocationProviderClient client;
    private String userLocation;
    private static User person; //static so the data isn't lost when the orientation changes
    private Profile userProfile; //static so the data isn't lost when the orientation changes
    private static StepTracker stepTracker;

    private SensorManager mSensorManager;
    private Sensor mStepCounter, mLinearAccelerometer;
    private float previousSteps, todaySteps;
    private boolean walking, firstStep;

    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //after the user has done their login/sign up, they will be brought to the fitness regimen
        setContentView(R.layout.activity_main);

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                //Toast.makeText(getApplicationContext(), "AWSMobileClient is instantiated and you are connected to AWS!", Toast.LENGTH_SHORT).show();
            }
        }).execute(); //implies that this is an Async task

        //we are grabbing the location of the user when the app arrives at this page so we don't have to move this from the LaunchActivity
        requestLocationPermission();
        client = LocationServices.getFusedLocationProviderClient(this); //grab lat/lon from user

        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        // Comment out when not testing - grabs default user
        if (mProfileViewModel.getCurrentUser().getValue() == null) {
            try {
                userProfile = mProfileViewModel.getProfile("default", "password").getValue();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            userProfile = mProfileViewModel.getCurrentUser().getValue();
        }

        /** The profile will be updated with any changes.  This isn't done here with steps because they
         * update so frequently.  Instead, see onStop() method for the backing up of that data.
         */
        mProfileViewModel.getCurrentUser().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable final Profile profile) {
                // Update the cached copy of the userProfile
                userProfile = profile;
            }
        });

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mLinearAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        todaySteps = mProfileViewModel.getStepcount();
        firstStep = true;

        if (todaySteps > 0) {
            firstStep = false;
        }

        previousSteps = 0;
        walking = false;

        // Grab the StepTracker and see if the last updated is today. If not, set daily steps to 0
        stepTracker = mProfileViewModel.getCurrentStepTracker().getValue();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(stepTracker.getLastUpdated());
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1; // + 1 because it starts at zero
        calendar.setTime(new Date()); // Reset the date to right now
        if (simpleDateFormat.format(new Date()).equals(simpleDateFormat.format(stepTracker.getLastUpdated()))) { // Same day of week
            if (day == calendar.get(Calendar.DATE) && month == calendar.get(Calendar.MONTH)) { // same day of month login
                mProfileViewModel.setStepcount(mProfileViewModel.getStepCountByDay(simpleDateFormat.format(stepTracker.getLastUpdated())));
            } else { // Same day of week, but different week/month requires a reset for the day
                mProfileViewModel.resetStepCountByDay(simpleDateFormat.format(new Date()), 0); // 0 to reset steps
            }
        }

        regimenFragment = new RegimenFragment();
        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();

        //logic for bring the user to the fitness regimen
        if (savedInstanceState == null) {

            if (isTablet()) {
                signupFragment = new SignupFragment();
                fTrans.replace(R.id.fragment_container, regimenFragment, "frag_masterlist");
            } else {
                setupDrawerNavigation();

                fTrans.replace(R.id.fragment_container, regimenFragment, "regimen_frag");
            }
            setupNavigationFields();

            fTrans.addToBackStack(null);
            fTrans.commit();
        } else if (savedInstanceState != null) {
            if (!isTablet()) {
                setupDrawerNavigation();
            }
            setupNavigationFields();
        }
    }

    /**
     * When the app closes, the current steps for the day will be saved.
     */
    @Override
    protected void onStop() {
        super.onStop();
        // Update the steps for the day
        mProfileViewModel.resetStepCountByDay(simpleDateFormat.format(new Date()), mProfileViewModel.getDailyStepCount());

        // Update the db to save the currents steps for the day
        mProfileViewModel.insertStepTracker(stepTracker);

        // Backup the db to the aws service
        mProfileViewModel.backupToAWS();
    }

    private void setupDrawerNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigationFields() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        mNameOutput = headerView.findViewById(R.id.tv_Name_output);
        mNameOutput.setText(mProfileViewModel.getName());
        mAgeOutput = headerView.findViewById(R.id.tv_age_output);
        String textAge = String.valueOf(mProfileViewModel.getAge());
        mAgeOutput.setText("Age: " + textAge);
        mWeightOutput = headerView.findViewById(R.id.tv_weight_output);
        String textWeight = String.valueOf(mProfileViewModel.getWeight());
        mWeightOutput.setText("Weight: " + textWeight);
        mHeightOutput = headerView.findViewById(R.id.tv_height_output);
        String textHeight = String.valueOf(mProfileViewModel.getHeight());
        mHeightOutput.setText("Height: " + textHeight);
        mGoalOutput = headerView.findViewById(R.id.tv_goal_output);
        if (mProfileViewModel.getFilePath() != null) {
            mProfilePicture = headerView.findViewById(R.id.iv_profile_image);
            mProfilePicture.setImageBitmap(mProfileViewModel.getImage());
        }

        mGoalOutput.setText(mProfileViewModel.getGoal());
    }

    public boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    /**
     * The below logic currently only supports the navigation pane
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, 11);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_bmi:
                BMIFragment bmiFragment = new BMIFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        bmiFragment).commit();
                break;
            case R.id.nav_hikes:
                userLocation = getLocationFormatted("hike");

                //Get the string data of the location
                userLocation = "geo:" + userLocation + "?q=";

                if (userLocation.isEmpty()) {
                    Toast.makeText(this, "No location received", Toast.LENGTH_SHORT).show();
                } else { // we'll launch google maps if the location gathering was successful
                    //We have to search for nearby hikes and construct a URI object from it.
                    Uri searchUri = Uri.parse(userLocation + "hikes"); //hikes is a "magic number"

                    //Create the implicit intent
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);

                    //If there's an activity associated with this intent, launch it
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }

                break;
            case R.id.nav_fitness_regimen:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        regimenFragment).commit();
                break;
            case R.id.nav_weather:
                userLocation = getLocationFormatted("weather");
                break;
            case R.id.nav_edit_profile:
                signupFragment = new SignupFragment();

                FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
                fTrans.replace(R.id.fragment_container, signupFragment, "signup_frag");
                fTrans.addToBackStack(null);
                fTrans.commit();
                break;

            case R.id.nav_step_counter:
                StepcountFragment stepcountFragment = new StepcountFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        stepcountFragment).commit();
                break;
            default:
                break;
        }

        if (!isTablet()) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }


    public String getLocationFormatted(String locationType) {
        //first see if the user gave the app permission to grab their location
        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        } else if (!locationType.equals("hike") && !locationType.equals("weather")) {
            Toast.makeText(this, "Your location type is invalid", Toast.LENGTH_LONG).show();
        }

        if (locationType.equals("hike")) {

            client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        userLocation = "" + location.getLatitude() + "," + location.getLongitude();
                    } else {
                        Toast.makeText(MainActivity.this, "Hikes: Could not get your location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (locationType.equals("weather")) {
            client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {

                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        userLocation = "lat=" + (int) location.getLatitude() + "&lon=" + (int) location.getLongitude();
                        Intent messageIntent = new Intent(MainActivity.this, WeatherActivity.class);
                        messageIntent.putExtra("BTN_STRING_LOC", userLocation);
                        MainActivity.this.startActivity(messageIntent); //this is not needed here, but is helpful for readability
                    } else {
                        Toast.makeText(MainActivity.this, "Weather: Could not get your location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return userLocation; //default return - could not launch into the appropriate activity
    }

    private SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                if (Math.abs(sensorEvent.values[0]) > 5) walking = true;
                else if (Math.abs(sensorEvent.values[2]) > 5) walking = false;
            } else if (walking && sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                if (firstStep) {
                    previousSteps = sensorEvent.values[0];
                    firstStep = false;
                } else {
                    todaySteps += sensorEvent.values[0] - previousSteps;
                    previousSteps = sensorEvent.values[0];
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mStepCounter != null) {
            mSensorManager.registerListener(mListener, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mLinearAccelerometer != null) {
            mSensorManager.registerListener(mListener, mLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        setupNavigationFields();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mStepCounter != null || mLinearAccelerometer != null) {
            mSensorManager.unregisterListener(mListener);
        }
        walking = false;

        if (mProfileViewModel != null) {
            mProfileViewModel.setStepcount((int) todaySteps);
        }
        mProfileViewModel.backupToAWS();
    }
}

