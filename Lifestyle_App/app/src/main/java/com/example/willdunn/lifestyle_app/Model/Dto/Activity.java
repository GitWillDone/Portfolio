package com.example.willdunn.lifestyle_app.Model.Dto;

import java.util.ArrayList;
import java.util.List;

///the activity levels are as follows: sedentary, moderate, or active
public class Activity {

    public List<String> activities = new ArrayList<>();

    public Activity() {
        activities.add("Sedentary");
        activities.add("Moderate");
        activities.add("Active");
    }
}
