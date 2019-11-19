package com.example.willdunn.lifestyle_app.Model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface StepDao {

    /// doing this will also take care of the user updating their profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StepTracker stepTracker);

    @Query("DELETE FROM stepTracker")
    void deleteAll();

    @Query("SELECT * FROM stepTracker WHERE stepTracker_id =:profileID")
    StepTracker getStepTrackerByID(int profileID);

    @Query("SELECT * FROM profile WHERE profile_id =:profileID")
    Profile getCorrespondingProfileByID(int profileID);

    @Query("SELECT * FROM stepTracker ORDER BY stepTracker_id ASC")
    LiveData<List<StepTracker>> getAll();
}
