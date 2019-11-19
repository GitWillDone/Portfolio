package com.example.willdunn.lifestyle_app;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.View.LaunchActivity;
import com.example.willdunn.lifestyle_app.View.SignupFragment;
import com.example.willdunn.lifestyle_app.ViewModel.ProfileViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.lifecycle.ViewModelProviders;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

public class SignupFragmentTest {

    private ProfileViewModel mProfileViewModel = null;

    @Rule
    public ActivityTestRule<LaunchActivity> launchActivityActivityTestRule = new ActivityTestRule<>(LaunchActivity.class);

    private Profile profile = null;
    private LaunchActivity launchActivity = null;
    private SignupFragment signup_frag = null;

    private View v = null;

    @Before
    public void setUp() throws Exception {
        launchActivity = launchActivityActivityTestRule.getActivity();

        // Query the db to grab a profile that we'll be testing on.  We'll use the default since it will always be there.
        mProfileViewModel = ViewModelProviders.of(launchActivity).get(ProfileViewModel.class);
        profile = mProfileViewModel.getProfile("default", "password").getValue();

        signup_frag = new SignupFragment();
        launchActivity.getSupportFragmentManager().beginTransaction().add(signup_frag.getId(), signup_frag).commit();

    }

    @Test
    public void testLaunch() {
        assertNotNull(signup_frag);
    }

    @Test
    public void onClickFieldsFilled() {

        launchActivity.runOnUiThread(new Runnable() {
            public void run() {
                EditText mFullName = (EditText) signup_frag.getView().findViewById(R.id.et_Name);
                Spinner mAge = (Spinner) signup_frag.getView().findViewById(R.id.spinner_Age);
                Spinner mHeight = (Spinner) signup_frag.getView().findViewById(R.id.spinner_height);
                EditText mWeight = (EditText) signup_frag.getView().findViewById(R.id.et_weight);
                Spinner mSex = (Spinner) signup_frag.getView().findViewById(R.id.spinner_sex);
                EditText mPassword = (EditText) signup_frag.getView().findViewById(R.id.et_password);
                Spinner mActivity = (Spinner) signup_frag.getView().findViewById(R.id.spinner_activity_level);
                Spinner mGoal = (Spinner) signup_frag.getView().findViewById(R.id.spinner_fitness_goal);

                mFullName.setText("Frank");
                mAge.setSelection(5);
                mHeight.setSelection(5);
                mWeight.setText("150");
                mSex.setSelection(1);
                mActivity.setSelection(1);
                mGoal.setSelection(1);

                View vLog = signup_frag.getView().findViewById(R.id.btn_submit_profile);
                signup_frag.onClick(vLog);
            }
        });

        onView(withId(R.id.frag_regimen)).check(matches((isDisplayed())));
    }
}