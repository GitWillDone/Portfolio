package com.example.willdunn.lifestyle_app.ViewModel;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.Model.StepTracker;
import com.example.willdunn.lifestyle_app.Repository.ProfileRepository;
import com.example.willdunn.lifestyle_app.Repository.StepRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ProfileViewModel extends AndroidViewModel {

    private ProfileRepository mProfileRepository;
    private StepRepository mStepRepository;

    // Fields relating to the Profile
    private int mMaxProfileID;
    private LiveData<Integer> mCurrentProfileID;
    private LiveData<Profile> mProfile;
    private LiveData<List<Profile>> mAllProfiles;

    // Fields for the RegimentFragment
    private MutableLiveData<String> mGoalWeight;
    private MutableLiveData<String> mBMROutput;
    private MutableLiveData<String> mCalOutput;

    // Fields for the BMIFragment
    private MutableLiveData<String> mBMI;

    // Fields for the StepcountFragment
    private int mDailyStepCount;


    public ProfileViewModel(Application application) throws ExecutionException, InterruptedException {
        super(application);
        mProfileRepository = new ProfileRepository(application);
        mStepRepository = new StepRepository(application);
        mMaxProfileID = mProfileRepository.getHighestProfileID();
        mAllProfiles = mProfileRepository.getAll();
        mProfile = getCurrentUser();
        if (mProfile.getValue() != null) setDailyStepCount(); // Only set the step counter if the user has logged in
    }

    public int getProfileID(String user, String pass) throws ExecutionException, InterruptedException {
        return mProfileRepository.getProfileID(user, pass);
    }

    public LiveData<Profile> getProfile(String user, String pass) throws ExecutionException, InterruptedException {
        mProfileRepository.getProfile(user, pass);
        mProfile = getCurrentUser();
        return mProfile;
    }

    public LiveData<Profile> getProfile(int profileID) {
        mProfile = mProfileRepository.getProfile(profileID);
        return mProfile;
    }

    public LiveData<List<Profile>> getAll() {
        return mProfileRepository.getAll();
    }

    public void insertProfile(Profile profile) {
        mProfileRepository.insert(profile);
    }

    public void insertStepTracker(StepTracker stepTracker) {
        mStepRepository.insert(stepTracker);
    }

    public int getHighestProfileID() {
        return mMaxProfileID;
    }

    public LiveData<Profile> getCurrentUser() {
        return mProfileRepository.getCurrentUser();
    }

    public int getDailyStepCount() {
        return mDailyStepCount;
    }

    public void setDailyStepCount() {
        // Get today's date and get the step count using the getStepCount() method
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        mDailyStepCount = getStepCountByDay(simpleDateFormat.format(new Date()));
    }


    /***********************************  HELPER METHODS FOR THE FRAGMENTS ***************************** */


    /* FOR GENERAL USE */
    public boolean getSex() {
        return mProfile.getValue().isSex();
    }

    public String getName() {
        return mProfile.getValue().getUsername();
    }

    public int getProfileID() {
        return mProfile.getValue().getProfile_id();
    }

    public int getHeight() {
        return mProfile.getValue().getHeight();
    }

    public int getAge() {
        return mProfile.getValue().getAge();
    }

    public String getActivityLevel() {
        return mProfile.getValue().getActivity_level();
    }

    public String getFilePath() {
        return mProfile.getValue().getFile_path();
    }

    public double getWeight() {
        return mProfile.getValue().getWeight();
    }

    public Bitmap getImage() {
        return mProfile.getValue().getProfilePicture();
    }

    public void backupToAWS(){
        String name = getName();
        Context ctx = this.getApplication().getApplicationContext();
        com.example.willdunn.lifestyle_app.RemoteDataSource.awsCloudStorage.uploadWithTransferUtility(ctx, name);
    }




    /* FOR REGIMEN FRAGMENT */
    public int getGoalOutputInt() {
        return mProfile.getValue().getGoal();
    }


    public String getGoal() {
        Profile profile = mProfile.getValue();

        switch (profile.getGoal()) {
            case 0:
                return "Your goal is to: \nMaintain Weight";
            case 1:
                return "Your goal is to: \nGain Weight";
            case 2:
                return "Your goal is to: \nLose Weight";
            default:
                break;
        }
        return null; // An invalid value was used in the Profile
    }


    /**
     * Estimates the user's Basal Metabolic Rate - the rate needed to survive.
     * The calculations were adapted from: https://www.everydayhealth.com/weight/boost-weight-loss-by-knowing-your-bmr.aspx
     * <p>
     * Men: 66 + (6.23 x weight in pounds) + (12.7 x height in inches) - (6.8 x age in years). Example, if you’re 170 pounds, 5’11”, and 43, your BMR is 66 + (6.23 x 170) + (12.7 x 71) – (6.8 x 43) = 1,734.4 calories
     * Women: 655 + (4.35 x weight in pounds) + (4.7 x height in inches) - (4.7 x age in years). Example, if you’re 130 pounds, 5’3”, and 36, your BMR is 665 + (4.35 x 130) + (4.7 x 63) – (4.7 x 36) = 1,357.4 calories
     */
    public int estimateBMR() {
        Profile profile = mProfile.getValue();
        double activityLevel = 0;
        double BMR = 0;
        if (profile.getActivity_level().equals("Sedentary")) {
            activityLevel = 1.2;
        } else if (profile.getActivity_level().equals("Moderate")) {
            activityLevel = 1.55;
        } else if (profile.getActivity_level().equals("Active")) {
            activityLevel = 1.9;
        }

        //calculates BMR based on gender differences - true is male
        if (profile.isSex()) {
            BMR = 66 + (6.23 * profile.getWeight()) + (12.7 * profile.getHeight()) - (6.8 * profile.getAge());
        } else {
            BMR = 665 + (4.35 * profile.getWeight()) + (4.7 * profile.getHeight()) - (4.7 * profile.getAge());
        }

        return (int) Math.round(activityLevel * BMR);
    }

    public int estimateCalories(int goalWeight) {
        int caloriesNeeded = estimateBMR();
        int caloricIntake;
        int DAYS_PER_WEEK = 7;
        int CALORIES_PER_POUND = 3500;

        if (mProfile.getValue().getGoal() == 0) { //logic for maintaining weight
            caloricIntake = caloriesNeeded;
        } else if (mProfile.getValue().getGoal() == 1) { //logic for gaining weight
            caloricIntake = (caloriesNeeded + ((CALORIES_PER_POUND * goalWeight)) /
                    (DAYS_PER_WEEK));
        } else { //logic for losing weight
            caloricIntake = (caloriesNeeded * 7) - (CALORIES_PER_POUND * goalWeight) /
                    (DAYS_PER_WEEK);
        }
        return caloricIntake;
    }




    /* FOR BMI FRAGMENT */

    /**
     * Calculates the user's Body Mass Index, per the Center for Disease Control's standards.
     * Formula: 703 x weight (lbs) / [height (in)]^2
     */
    public String getBMI() {
        double bodyMassIndex = (703 * mProfile.getValue().getWeight()) / Math.pow(mProfile.getValue().getHeight(), 2);
        return (Math.round(bodyMassIndex * 100) / 100 + "%");
    }

    /* FOR STEPCOUNT FRAGMENT */
    public int getStepcount() {
        return mDailyStepCount;
    }

    public void setStepcount(int dailySteps) {
        mDailyStepCount = dailySteps;
    }

    public LiveData<StepTracker> getStepCounter(int profileID) throws ExecutionException, InterruptedException {
        mStepRepository.getStepTrackerByID(profileID);
        return mStepRepository.getCurrentStepTracker();
    }

    public LiveData<StepTracker> getCurrentStepTracker() {
        return mStepRepository.getCurrentStepTracker();
    }

    public int getStepCountByDay(String day) {
        switch (day) {
            case "Monday":
                return mStepRepository.getCurrentStepTracker().getValue().getMondaySteps();
            case "Tuesday":
                return mStepRepository.getCurrentStepTracker().getValue().getTuesdaySteps();
            case "Wednesday":
                return mStepRepository.getCurrentStepTracker().getValue().getWednesdaySteps();
            case "Thurs":
                return mStepRepository.getCurrentStepTracker().getValue().getThursdaySteps();
            case "Friday":
                return mStepRepository.getCurrentStepTracker().getValue().getFridaySteps();
            case "Saturday":
                return mStepRepository.getCurrentStepTracker().getValue().getSaturdaySteps();
            case "Sunday":
                return mStepRepository.getCurrentStepTracker().getValue().getSundaySteps();
            default:
                return 0;
        }
    }

    public void resetStepCountByDay(String day, int steps) {
        switch (day) {
            case "Monday":
                mStepRepository.getCurrentStepTracker().getValue().setMondaySteps(steps);
                break;
            case "Tuesday":
                mStepRepository.getCurrentStepTracker().getValue().setTuesdaySteps(steps);
                break;
            case "Wednesday":
                mStepRepository.getCurrentStepTracker().getValue().setWednesdaySteps(steps);
                break;
            case "Thurs":
                mStepRepository.getCurrentStepTracker().getValue().setThursdaySteps(steps);
                break;
            case "Friday":
                mStepRepository.getCurrentStepTracker().getValue().setFridaySteps(steps);
                break;
            case "Saturday":
                mStepRepository.getCurrentStepTracker().getValue().setSaturdaySteps(steps);
                break;
            case "Sunday":
                mStepRepository.getCurrentStepTracker().getValue().setSundaySteps(steps);
                break;
            default:
                break;
        }
    }
}

