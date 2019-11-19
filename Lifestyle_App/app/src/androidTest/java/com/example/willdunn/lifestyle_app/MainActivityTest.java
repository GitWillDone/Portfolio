package com.example.willdunn.lifestyle_app;

import android.view.MenuItem;
import android.view.View;

import com.example.willdunn.lifestyle_app.Model.User;
import com.example.willdunn.lifestyle_app.View.MainActivity;
import com.google.android.material.navigation.NavigationView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * DEPRECATED TEST CLASS.  WITH THE LIVEDATA AND MVVM ARCHTECTURE CHANGES TO THE APP, YOU WILL NO LONGER
 * BE ABLE TO SIMPLY LOGIN TO THE MAINACTIVITY.  DUE TO THE REQUIREMENT OF LIVEDATA AND ITS LACK OF
 * ABILITY TO BE INSTANTIATED WHEN @RULES ARE REQUIRED PRIOR TO SETUP (WHICH WANT LIVEDATA), YOU CANNOT
 * CREATE A MAINACTIVITY.  CHICKEN VS. EGG PROBLEM.
 */


public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mainActivity = null;

    private NavigationView navigationView = null;

    public GrantPermissionRule granted = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() throws Exception {
        User person = new User("dev", 140, 15, 16, 0, "Sedentary", null, true, 1);
        mainActivity = mainActivityActivityTestRule.getActivity();
        navigationView = mainActivity.findViewById(R.id.nav_view);
    }

    //tests if the view is launched correctly for the MainActivity
    @Test
    public void testLaunch(){
        View view = mainActivity.findViewById(R.id.drawer_layout);
        assertNotNull(view);
    }

    @Test
    public void onNavigationItemSelected() {
        MenuItem mi = navigationView.getMenu().getItem(0);

        boolean check = mainActivity.onNavigationItemSelected(mi);
        assertTrue(check);

        mi = navigationView.getMenu().getItem(1);
        check = mainActivity.onNavigationItemSelected(mi);
        assertTrue(check);

        mi = navigationView.getMenu().getItem(2);
        check = mainActivity.onNavigationItemSelected(mi);
        assertTrue(check);

        mi = navigationView.getMenu().getItem(3);
        check = mainActivity.onNavigationItemSelected(mi);
        assertTrue(check);
    }

    @Test
    public void getLocationFormatted() {

        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                String check = mainActivity.getLocationFormatted("not right");
                assertNull(check);
            }
        });

        onView(withText("Your location type is invalid")).inRoot(withDecorView(not(is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        String check = mainActivity.getLocationFormatted("hike");
        assertNull(check);

        check = mainActivity.getLocationFormatted("weather");
        assertNull(check);
    }
}