package com.example.willdunn.lifestyle_app.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.willdunn.lifestyle_app.Model.DatabaseHelper;
import com.example.willdunn.lifestyle_app.Model.Dto.Activity;
import com.example.willdunn.lifestyle_app.Model.Dto.Age;
import com.example.willdunn.lifestyle_app.Model.Dto.Gender;
import com.example.willdunn.lifestyle_app.Model.Dto.Goal;
import com.example.willdunn.lifestyle_app.Model.Dto.Height;
import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.Model.StepTracker;
import com.example.willdunn.lifestyle_app.Model.User;
import com.example.willdunn.lifestyle_app.R;
import com.example.willdunn.lifestyle_app.ViewModel.ProfileViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import static android.app.Activity.RESULT_OK;

public class SignupFragment extends Fragment implements View.OnClickListener {

    private EditText mFullName, mWeight, mPassword;
    private Spinner mAge, mHeight, mSex, mGoal, mActivity;
    private ImageView mCameraBtn;
    private Button btnAdd;
    private Toolbar navDrawer;

    DatabaseHelper mDatabaseHelper;
    ProfileViewModel profileViewModel;

    private final int REQUEST_IMAGE_CAPTURE = 1;
    String filePathString = "";

    //Define a bitmap
    Bitmap mThumbnailImage;
    private static User user;
    private static Profile profile;
    private static StepTracker stepTracker;

    //Callback interface - nested class
    public interface OnDataPass {
        public void onDataPass(String[] data);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        navDrawer = getActivity().findViewById(R.id.toolbar);
        if (navDrawer != null) navDrawer.setVisibility(View.GONE);

        mFullName = (EditText) view.findViewById(R.id.et_Name);
        mAge = (Spinner) view.findViewById(R.id.spinner_Age);
        mHeight = (Spinner) view.findViewById(R.id.spinner_height);
        mWeight = (EditText) view.findViewById(R.id.et_weight);
        mSex = (Spinner) view.findViewById(R.id.spinner_sex);
        mPassword = (EditText) view.findViewById(R.id.et_password);
        mActivity = (Spinner) view.findViewById(R.id.spinner_activity_level);
        mGoal = (Spinner) view.findViewById(R.id.spinner_fitness_goal);

        mCameraBtn = view.findViewById(R.id.iv_camera);
        btnAdd = (Button) view.findViewById(R.id.btn_submit_profile);
        mDatabaseHelper = new DatabaseHelper(this.getContext());

        btnAdd.setOnClickListener(this);
        mCameraBtn.setOnClickListener(this);

        Age ages = new Age();
        Height heights = new Height();
        Gender gender = new Gender();
        Activity activity = new Activity();
        Goal goal = new Goal();

        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter<Integer>(this.getContext(), android.R.layout.simple_spinner_item, ages.ageSet);
        ArrayAdapter<Integer> heightAdapter = new ArrayAdapter<Integer>(this.getContext(), android.R.layout.simple_spinner_item, heights.heights);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, gender.generType);
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, activity.activities);
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, goal.goals);

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAge.setAdapter(ageAdapter);
        mHeight.setAdapter(heightAdapter);
        mSex.setAdapter(genderAdapter);
        mActivity.setAdapter(activityAdapter);
        mGoal.setAdapter(goalAdapter);

        profileViewModel = ViewModelProviders.of(this.getActivity()).get(ProfileViewModel.class);

        try {
            if (profileViewModel.getName() != null) {
                setExistingProfile();
                profile = profileViewModel.getCurrentUser().getValue(); // stops from default being overridden

                // Set the observer, which updates the UI
                profileViewModel.getCurrentUser().observe(this.getActivity(), new Observer<Profile>() {
                    @Override
                    public void onChanged(Profile profile) {
                        if (profile.getUsername() != null) mFullName.setText(profile.getUsername());
                        if (profile.getWeight() != 0) mWeight.setText(profile.getWeight() + "");
                        if (profile.getPassword() != null) mPassword.setText(profile.getPassword());
                    }
                });
            }


        } catch (NullPointerException e) {
            ; // Continue on because this is the first time we are creating a profile
        }

        if (savedInstanceState != null) {
            mFullName.setText((String) savedInstanceState.get("ET_NAME"));
            mWeight.setText((String) savedInstanceState.get("ET_WEIGHT"));
            mPassword.setText((String) savedInstanceState.get("ET_PASSWORD"));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //handle camera click
            case R.id.iv_camera: {
                dispatchTakePictureIntent();
                break;
            }
            //handle submit click
            case R.id.btn_submit_profile: {
                boolean sex = false;
                String password = null, activityLevel = null;
                int age = 0, height = 0, goal = 0;
                double weight = 0;
                String username = mFullName.getText().toString();

                if (username.isEmpty()) {
                    Toast.makeText(this.getContext(), "Your name is the only required field.", Toast.LENGTH_LONG).show();
                    break;
                }
                if (mAge.getSelectedItem() != null) {
                    age = Integer.parseInt(mAge.getSelectedItem().toString());
                }
                if (mHeight.getSelectedItem() != null) {
                    height = Integer.parseInt(mHeight.getSelectedItem().toString());
                }
                if (!mWeight.getText().toString().equals("")) {
                    weight = Double.parseDouble(mWeight.getText().toString());
                }
                if (mSex.getSelectedItem() != null) {
                    if (mSex.getSelectedItem().toString().equals("M")) {
                        sex = true;
                    } else {
                        sex = false;
                    }
                }
                if (!mPassword.getText().toString().equals("")) {
                    password = mPassword.getText().toString();
                }
                if (mActivity.getSelectedItem() != null) {
                    activityLevel = mActivity.getSelectedItem().toString();
                }
                if (mGoal.getSelectedItem() != null) {
                    switch (mGoal.getSelectedItem().toString()) {
                        case "Maintain Weight":
                            goal = 0;
                            break;
                        case "Gain Weight":
                            goal = 1;
                            break;
                        case "Lose Weight":
                            goal = 2;
                            break;
                        default:
                            goal = 0; //default is to maintain weight
                            break;
                    }
                }

                // if the profile doesn't exist, create one
                if (profile == null) {
                    int profileID = 1;
                    try {
                        profileID = profileViewModel.getHighestProfileID() + 1;
                    } catch (Exception e) {
                        Toast.makeText(this.getContext(), "NPE when getting profile ID!", Toast.LENGTH_LONG).show();
                        Log.d("LaunchActivity", "DB profileID retrieval failure due to NPE");
                        break;
                    }

                    // First create the Profile object

                    profile = new Profile(username, weight, height, age, goal, activityLevel, filePathString, sex, password, profileID);
                    stepTracker = new StepTracker();
                    stepTracker.setStepTracker_id(profileID);

                    // Then add the Profile to the db
                    try {
                        profileViewModel.insertProfile(profile);
                        profileViewModel.insertStepTracker(stepTracker);
                        Toast.makeText(this.getContext(), "Profile successfully created!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this.getContext(), "Failure to insert into DB!", Toast.LENGTH_LONG).show();
                        Log.d("LaunchActivity", "DB failed to insert profile");
                    }
                } else { // The user already has a Profile and is editing their information
                    try {
                        try {
                            if (profileViewModel.getFilePath() != null && !profileViewModel.getFilePath().equals(""))
                                filePathString = profileViewModel.getFilePath();
                        } catch (NullPointerException ne) {
                            ; // Do nothing because taking the picture sets the file path for the image
                        }
                        profile = new Profile(username, weight, height, age, goal, activityLevel, filePathString, sex, password, profileViewModel.getProfileID());
                        profileViewModel.insertProfile(profile); // Will update any fields that changed
                        Toast.makeText(this.getContext(), "Profile Edited!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this.getContext(), "Profile failed to update", Toast.LENGTH_LONG).show();
                        break; // Gives the user the chance to try the alteration again
                    }
                }

                Intent intent = new Intent(this.getContext(), MainActivity.class);
                startActivity(intent);

                break;
            }
            default:
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //Get the bitmap
            Bundle extras = data.getExtras();
            mThumbnailImage = (Bitmap) extras.get("data");

            //Open a file and write to it
            if (isExternalStorageWritable()) {
                filePathString = saveImage(mThumbnailImage);
            } else {
                Toast.makeText(this.getContext(), "External storage not writable.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImage(Bitmap finalBitmap) {
        File myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Thumbnail_" + timeStamp + ".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(this.getContext(), "file saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void setExistingProfile() {
        mFullName.setText(profileViewModel.getName());

        ArrayAdapter ageAdap = (ArrayAdapter) mAge.getAdapter();
        int spinnerPosition = ageAdap.getPosition(profileViewModel.getAge());
        if (spinnerPosition >= 0) mAge.setSelection(spinnerPosition);

        ArrayAdapter heightAdap = (ArrayAdapter) mHeight.getAdapter();
        spinnerPosition = heightAdap.getPosition(profileViewModel.getHeight());
        if (spinnerPosition >= 0) mHeight.setSelection(spinnerPosition);

        mWeight.setText(String.valueOf(profileViewModel.getWeight()));

        ArrayAdapter sexAdap = (ArrayAdapter) mSex.getAdapter();
        String sexString = null;
        if (profileViewModel.getSex()) sexString = "M";
        else sexString = "F";
        spinnerPosition = sexAdap.getPosition(sexString);
        if (spinnerPosition >= 0) mSex.setSelection(spinnerPosition);

        ArrayAdapter activityAdap = (ArrayAdapter) mActivity.getAdapter();
        spinnerPosition = activityAdap.getPosition(profileViewModel.getActivityLevel());
        if (spinnerPosition >= 0) mActivity.setSelection(spinnerPosition);

        ArrayAdapter goalAdap = (ArrayAdapter) mGoal.getAdapter();
        String goalString = null;
        if (profileViewModel.getGoalOutputInt() == 0) goalString = "Maintain Weight";
        else if (profileViewModel.getGoalOutputInt() == 1) goalString = "Gain Weight";
        else goalString = "Lose Weight";
        spinnerPosition = goalAdap.getPosition(goalString);
        if (spinnerPosition >= 0) mGoal.setSelection(spinnerPosition);
    }
}
