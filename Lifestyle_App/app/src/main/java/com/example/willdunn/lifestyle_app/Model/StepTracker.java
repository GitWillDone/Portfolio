package com.example.willdunn.lifestyle_app.Model;


import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Object representing steps tracking for the user.  It is tied together by a foreign key that is the
 * profile id.  The current day's steps are not stored here, but will be stored in cache and updated
 * when the user exits the app (onStop()).
 *
 * The updating will just take place with the onConflict in insert with the process being:
 * Set the StepTracker field to the updated value -> insert the StepTracker -> onConflict will update the relevant fields
 */

@Entity(tableName = "stepTracker")
public class StepTracker {
    @PrimaryKey
    @ForeignKey(entity = Profile.class,
            parentColumns = "profile_id",
            childColumns = "stepTracker_id",
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    @NonNull
    @ColumnInfo(name = "stepTracker_id")
    private int stepTracker_id;

    @ColumnInfo(name = "monday_steps")
    private int mondaySteps;

    @ColumnInfo(name = "tuesday_steps")
    private int tuesdaySteps;

    @ColumnInfo(name = "wednesday_steps")
    private int wednesdaySteps;

    @ColumnInfo(name = "thursday_steps")
    private int thursdaySteps;

    @ColumnInfo(name = "friday_steps")
    private int fridaySteps;

    @ColumnInfo(name = "saturday_steps")
    private int saturdaySteps;

    @ColumnInfo(name = "sunday_steps")
    private int sundaySteps;

    @ColumnInfo(name = "last_updated")
    private Date lastUpdated;


    // Constructors
    public StepTracker() {
        mondaySteps = 0;
        tuesdaySteps = 0;
        wednesdaySteps = 0;
        thursdaySteps = 0;
        fridaySteps = 0;
        saturdaySteps = 0;
        sundaySteps = 0;
        lastUpdated = new Date();
    }

    // getters and setters
    public int getStepTracker_id() {
        return stepTracker_id;
    }

    public void setStepTracker_id(int stepTracker_id) {
        this.stepTracker_id = stepTracker_id;
    }

    public int getMondaySteps() {
        return mondaySteps;
    }

    public void setMondaySteps(int mondaySteps) {
        this.mondaySteps = mondaySteps;
    }

    public int getTuesdaySteps() {
        return tuesdaySteps;
    }

    public void setTuesdaySteps(int tuesdaySteps) {
        this.tuesdaySteps = tuesdaySteps;
    }

    public int getWednesdaySteps() {
        return wednesdaySteps;
    }

    public void setWednesdaySteps(int wednesdaySteps) {
        this.wednesdaySteps = wednesdaySteps;
    }

    public int getThursdaySteps() {
        return thursdaySteps;
    }

    public void setThursdaySteps(int thursdaySteps) {
        this.thursdaySteps = thursdaySteps;
    }

    public int getFridaySteps() {
        return fridaySteps;
    }

    public void setFridaySteps(int fridaySteps) {
        this.fridaySteps = fridaySteps;
    }

    public int getSaturdaySteps() {
        return saturdaySteps;
    }

    public void setSaturdaySteps(int saturdaySteps) {
        this.saturdaySteps = saturdaySteps;
    }

    public int getSundaySteps() {
        return sundaySteps;
    }

    public void setSundaySteps(int sundaySteps) {
        this.sundaySteps = sundaySteps;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getMaxStepsForWeek() {
        ArrayList<Integer> stepsByDay = new ArrayList<>();
        stepsByDay.add(mondaySteps);
        stepsByDay.add(tuesdaySteps);
        stepsByDay.add(wednesdaySteps);
        stepsByDay.add(thursdaySteps);
        stepsByDay.add(fridaySteps);
        stepsByDay.add(saturdaySteps);
        stepsByDay.add(sundaySteps);

        return Collections.max(stepsByDay);
    }
}
