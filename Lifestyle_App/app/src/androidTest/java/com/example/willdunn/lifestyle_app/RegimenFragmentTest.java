package com.example.willdunn.lifestyle_app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.View.LaunchActivity;
import com.example.willdunn.lifestyle_app.View.RegimenFragment;
import com.example.willdunn.lifestyle_app.ViewModel.ProfileViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.lifecycle.ViewModelProviders;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class RegimenFragmentTest {

    private ProfileViewModel mProfileViewModel = null;

    @Rule
    public ActivityTestRule<LaunchActivity> launchActivityActivityTestRule = new ActivityTestRule<>(LaunchActivity.class);

    private LaunchActivity launchActivity = null;

    private RegimenFragment regimen_frag = null;

    private Bundle fragmentBundle;

    private Profile profile;

    @Before
    public void setUp() throws Exception {
        launchActivity = launchActivityActivityTestRule.getActivity();
//        User person = new User("mary", 150, 66, 30, 2, "Sedentary", false);
//        fragmentBundle = new Bundle();
//        fragmentBundle.putParcelable("USER_PROFILE", person);

        // Query the db to grab a profile that we'll be testing on.  We'll use the default since it will always be there.
        mProfileViewModel = ViewModelProviders.of(launchActivity).get(ProfileViewModel.class);
        profile = mProfileViewModel.getProfile("default","password").getValue();

        regimen_frag = new RegimenFragment();
//        regimen_frag.setArguments(fragmentBundle);

        launchActivity.getSupportFragmentManager().beginTransaction().add(regimen_frag.getId(), regimen_frag).commit();

    }

    @Test
    public void testLaunch() {
        assertNotNull(regimen_frag);
    }

    @Test
    public void estimateBMR() {
        assertEquals(mProfileViewModel.estimateCalories(2), 4164);
        assertNotEquals(mProfileViewModel.estimateCalories(10), 4164);
    }

    @Test
    public void onClickOverzealousCheck() {
        launchActivity.runOnUiThread(new Runnable() {
            public void run() {
                EditText mGoalWeight = (EditText) regimen_frag.getView().findViewById(R.id.et_goal_weight);
                mGoalWeight.setText("3");
                View vLog = regimen_frag.getView().findViewById(R.id.bn_calculate_caloric_intake);
                regimen_frag.onClick(vLog);
            }
        });

        onView(withText("You're being overzealous!  You don't want to gain/lost more than 2 lbs. per week!")).inRoot(withDecorView(not(is(launchActivityActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}