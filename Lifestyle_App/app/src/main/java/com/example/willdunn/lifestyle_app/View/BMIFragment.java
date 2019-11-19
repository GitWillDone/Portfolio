package com.example.willdunn.lifestyle_app.View;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.R;
import com.example.willdunn.lifestyle_app.ViewModel.ProfileViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class BMIFragment extends Fragment {
    private TextView mBMIOutput;
    private ProfileViewModel profileViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bmi, container, false);

        mBMIOutput = (TextView) view.findViewById(R.id.tv_bmi_output);

        // Create the view model
        profileViewModel = ViewModelProviders.of(this.getActivity()).get(ProfileViewModel.class);

        // Set the observer, which updates the UI
        profileViewModel.getCurrentUser().observe(this.getActivity(), new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                mBMIOutput.setText(profileViewModel.getBMI());
            }
        });

        mBMIOutput.setText(profileViewModel.getBMI());

        if(savedInstanceState != null){
            mBMIOutput.setText((String) savedInstanceState.get("BMI_OUTPUT"));
        }



        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save the view hierarchy
        super.onSaveInstanceState(outState);

        //save the outputs of the calculations, if the user did them, for the BMR and Caloric outputs
        outState.putString("BMI_OUTPUT", mBMIOutput.getText().toString());
    }
}
