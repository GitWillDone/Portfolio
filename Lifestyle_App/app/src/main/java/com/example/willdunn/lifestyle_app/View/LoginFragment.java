package com.example.willdunn.lifestyle_app.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.willdunn.lifestyle_app.Model.DatabaseHelper;
import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.Model.StepTracker;
import com.example.willdunn.lifestyle_app.R;
import com.example.willdunn.lifestyle_app.ViewModel.ProfileViewModel;

import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private Button mLoginBtn, mSignUpBtn;
    private EditText mEtUserName, mEtPassword;
    private String mUserName, mPassword;
    DatabaseHelper mDatabaseHelper;
    ProfileViewModel profileViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login_signin, container, false);

        mSignUpBtn = (Button) view.findViewById(R.id.sign_up);
        mLoginBtn = (Button) view.findViewById(R.id.login);
        mSignUpBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);

//        mDatabaseHelper = new DatabaseHelper(this.getContext());

        mEtUserName = (EditText) view.findViewById(R.id.user_name);
        mEtPassword = (EditText) view.findViewById(R.id.password);

        if (savedInstanceState != null) {
            mEtUserName.setText((String) savedInstanceState.get("NAME_OUTPUT"));
            mEtPassword.setText((String) savedInstanceState.get("PASSWORD_OUTPUT"));
        }

        profileViewModel = ViewModelProviders.of(this.getActivity()).get(ProfileViewModel.class);
        return view;
    }

    @Override
    public void onClick(View v) {
        //check which button was clicked
        switch (v.getId()) {
            case R.id.sign_up: {
                getFragmentManager().beginTransaction().replace(R.id.frag_launch,
                        new SignupFragment()).commit();
                break;
            }
            case R.id.login: {
                //handle login logic, add check to make sure fields are not blank
                mUserName = mEtUserName.getText().toString();
                mPassword = mEtPassword.getText().toString();

                int profileID = 0;
                try {
                    profileID = profileViewModel.getProfileID(mUserName, mPassword);
                } catch (InterruptedException ie) {
                    Log.d("STATE", "Login Interrupted");
                    Toast.makeText(this.getContext(), "Login Interrupted", Toast.LENGTH_SHORT).show();
                } catch (ExecutionException ee) {
                    Log.d("EXECUTION", "Execution fail");
                    Toast.makeText(this.getContext(), "Login Execution failure", Toast.LENGTH_SHORT).show();
                }

                LiveData<Profile> profile = null;
                LiveData<StepTracker> stepTrackerLiveData = null;
                try {
                    profile = profileViewModel.getProfile(mUserName, mPassword);
                    stepTrackerLiveData = profileViewModel.getStepCounter(profileID);
                } catch (InterruptedException ie) {
                    Log.d("STATE", "Login Interrupted");
                    Toast.makeText(this.getContext(), "Login Interrupted", Toast.LENGTH_SHORT).show();
                } catch (ExecutionException ee) {
                    Log.d("EXECUTION", "Execution fail");
                    Toast.makeText(this.getContext(), "Login Execution failure", Toast.LENGTH_SHORT).show();
                }
                try{
                    if (profile.getValue() != null && stepTrackerLiveData.getValue() != null){
                        Toast.makeText(this.getContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                } catch (NullPointerException e){
                    Toast.makeText(this.getContext(), "The profile or step tracker is null. Try again.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save the view hierarchy
        super.onSaveInstanceState(outState);

        //save the outputs of the calculations, if the user did them, for the BMR and Caloric outputs
        outState.putString("NAME_OUTPUT", mEtUserName.getText().toString());
        outState.putString("PASSWORD_OUTPUT", mEtPassword.getText().toString());
    }
}
