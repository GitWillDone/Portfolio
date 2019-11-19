package com.example.willdunn.lifestyle_app.View;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.R;
import com.example.willdunn.lifestyle_app.ViewModel.ProfileViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


/**
 *
 */
public class RegimenFragment extends Fragment implements View.OnClickListener {
    private Button mCalcCalorie;
    private EditText mGoalWeight;
    private TextView mBMROutput, mCalOutput, mGoalOutput;
    private ProfileViewModel profileViewModel;
    private Toolbar navDrawer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_regimen, container, false);

        navDrawer = getActivity().findViewById(R.id.toolbar);
        if(navDrawer != null) navDrawer.setVisibility(View.VISIBLE);

        //get stuff
        mCalcCalorie = (Button) view.findViewById(R.id.bn_calculate_caloric_intake);
        mGoalWeight = (EditText) view.findViewById(R.id.et_goal_weight);
        mBMROutput = (TextView) view.findViewById(R.id.tv_bmr_output);
        mCalOutput = (TextView) view.findViewById(R.id.tv_caloric_output);
        mGoalOutput = (TextView) view.findViewById(R.id.tv_goal_review);
        mCalcCalorie.setOnClickListener(this);

        if (savedInstanceState != null) {
            mBMROutput.setText((String) savedInstanceState.get("BMR_OUTPUT"));
            mCalOutput.setText((String) savedInstanceState.get("CAL_OUTPUT"));
        }

        // Create the view model
        profileViewModel = ViewModelProviders.of(this.getActivity()).get(ProfileViewModel.class);

        mGoalOutput.setText(profileViewModel.getGoal());

        // Set the observer, which updates the UI
        profileViewModel.getCurrentUser().observe(this.getActivity(), new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                if (profile.getGoal() < 0 || profile.getGoal() > 2)
                    mGoalOutput.setText(translateGoalIntToString(profile.getGoal()));
            }
        });

        return view;
    }

    public String translateGoalIntToString(int goalNumber) {
        switch (goalNumber) {
            case 0:
                return "Your goal is to: Maintain Weight";
            case 1:
                return "Your goal is to: Gain Weight";
            case 2:
                return "Your goal is to: Lose Weight";
            default:
                return "";
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bn_calculate_caloric_intake: {

                if (mGoalWeight.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter a goal weight", Toast.LENGTH_LONG).show();
                    break;
                }

                InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                try {
                    mBMROutput.setText("Your BMR is: " + profileViewModel.estimateBMR());

                    int caloricIntake = profileViewModel.estimateCalories(Integer.parseInt(mGoalWeight.getText().toString()));

                    mCalOutput.setText("You need to consume: \n" + caloricIntake
                            + "\ncalories per day to meet your weight goal.");

                    //because the number entered is weight change per week, alert the user if they're being unsafe
                    if (Integer.parseInt(mGoalWeight.getText().toString()) > 2) {
                        Toast.makeText(getActivity(), "You're being overzealous!  You don't want to gain/lost more than 2 lbs. per week!", Toast.LENGTH_LONG).show();
                    }

                    else if ((caloricIntake < 1200 && profileViewModel.getSex()) || (caloricIntake < 1000)) {
                        Toast.makeText(getActivity(), "You'll starve yourself!  You need to eat at least 1200 (male) or 1000 (female) calories per day!", Toast.LENGTH_LONG).show();
                    }

                    break;
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "The number entered as the goal weight or time is invalid", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save the view hierarchy
        super.onSaveInstanceState(outState);

        //save the outputs of the calculations, if the user did them, for the BMR and Caloric outputs
        if (mBMROutput != null) {
            outState.putString("BMR_OUTPUT", mBMROutput.getText().toString());
            outState.putString("CAL_OUTPUT", mCalOutput.getText().toString());
        }
    }
}
