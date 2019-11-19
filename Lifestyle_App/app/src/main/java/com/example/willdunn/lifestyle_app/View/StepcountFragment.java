package com.example.willdunn.lifestyle_app.View;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.Model.StepTracker;
import com.example.willdunn.lifestyle_app.R;
import com.example.willdunn.lifestyle_app.ViewModel.ProfileViewModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class StepcountFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private Toolbar navDrawer;
    private TextView mStepOutput;
    private StepTracker stepTracker;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_stepcount, container, false);

        navDrawer = getActivity().findViewById(R.id.toolbar);
        if (navDrawer != null) navDrawer.setVisibility(View.VISIBLE);

        mStepOutput = (TextView) view.findViewById(R.id.tv_step_output);

        // Create the view model
        profileViewModel = ViewModelProviders.of(this.getActivity()).get(ProfileViewModel.class);


//        mStepOutput.setText("Your daily steps are: " + todaySteps);
        mStepOutput.setText("Your daily steps are: " + profileViewModel.getStepcount());

        // Set the observer, which updates the UI
        profileViewModel.getCurrentUser().observe(this.getActivity(), new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                mStepOutput.setText("Your daily steps are: " + profileViewModel.getStepcount());
            }
        });

        stepTracker = profileViewModel.getCurrentStepTracker().getValue();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        int currSteps = profileViewModel.getDailyStepCount();
        if (simpleDateFormat.format(new Date()).equals(simpleDateFormat.format(stepTracker.getLastUpdated()))) {
            switch (simpleDateFormat.format(new Date())) {
                case "Monday":
                    stepTracker.setMondaySteps(currSteps);
                    break;
                case "Tuesday":
                    stepTracker.setTuesdaySteps(currSteps);
                    break;
                case "Wednesday":
                    stepTracker.setWednesdaySteps(currSteps);
                    break;
                case "Thurs":
                    stepTracker.setThursdaySteps(currSteps);
                    break;
                case "Friday":
                    stepTracker.setFridaySteps(currSteps);
                    break;
                case "Saturday":
                    stepTracker.setSaturdaySteps(currSteps);
                    break;
                case "Sunday":
                    stepTracker.setSundaySteps(currSteps);
                    break;
                default:
                    break;
            }
        }

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, stepTracker.getMondaySteps()),
                new DataPoint(1, stepTracker.getTuesdaySteps()),
                new DataPoint(2, stepTracker.getWednesdaySteps()),
                new DataPoint(3, stepTracker.getThursdaySteps()),
                new DataPoint(4, stepTracker.getFridaySteps()),
                new DataPoint(5, stepTracker.getSaturdaySteps()),
                new DataPoint(6, stepTracker.getSundaySteps())
        });

        graph.addSeries(series);

        // set the x-labels of the x-axis
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getViewport().setMaxY(stepTracker.getMaxStepsForWeek() * 1.1); // Raises the max y-value so the user will see all values
        graph.getViewport().setYAxisBoundsManual(true);

        // graph styling - the override will change the data of each bar
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 3), 100);
            }
        });

        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.BLACK);

        return view;
    }
}
