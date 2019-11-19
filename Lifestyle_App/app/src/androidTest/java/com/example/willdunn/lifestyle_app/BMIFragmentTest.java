package com.example.willdunn.lifestyle_app;

import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.Model.User;
import com.example.willdunn.lifestyle_app.View.BMIFragment;
import com.example.willdunn.lifestyle_app.View.LaunchActivity;
import com.example.willdunn.lifestyle_app.ViewModel.ProfileViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import androidx.lifecycle.ViewModelProviders;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertNotNull;

public class BMIFragmentTest {

    private ProfileViewModel mProfileViewModel = null;

    @Rule
    public ActivityTestRule<LaunchActivity> launchActivityActivityTestRule = new ActivityTestRule<>(LaunchActivity.class);

    private Profile profile = null;
    private BMIFragment bmi_frag = null;
    private User person;

    private LaunchActivity launchActivity = null;

    public BMIFragmentTest() throws ExecutionException, InterruptedException {
    }

    @Before
    public void setUp() throws Exception {
        launchActivity = launchActivityActivityTestRule.getActivity();

        // Query the db to grab a profile that we'll be testing on.  We'll use the default since it will always be there.
        mProfileViewModel = ViewModelProviders.of(launchActivity).get(ProfileViewModel.class);
        profile = mProfileViewModel.getProfile("default","password").getValue();

        bmi_frag = new BMIFragment();
        launchActivity.getSupportFragmentManager().beginTransaction().add(bmi_frag.getId(), bmi_frag).commit();
    }

    @Test
    public void testLaunch() {
        assertNotNull(bmi_frag);
    }

    @Test
    public void calculateBMI() {
        int result = mProfileViewModel.estimateCalories(1);
        assert(result == 3664); // Assertion based on default profile which has a height and weight of 70 and 150 respectively
    }
}