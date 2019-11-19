package com.example.willdunn.lifestyle_app.Model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ProfileDao {

    /// doing this will also take care of the user updating their profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profile profile);

    @Query("DELETE FROM profile")
    void deleteAll();

    @Query("SELECT * FROM profile WHERE username =:user AND password =:pass")
    int getProfileID(String user, String pass);

    @Query("SELECT * FROM profile WHERE username =:user AND password =:pass")
    Profile getProfile(String user, String pass);

    @Query("SELECT * FROM profile WHERE profile_id =:profileID")
    Profile getProfile(int profileID);

    @Query("SELECT * FROM profile ORDER BY profile_id ASC")
    LiveData<List<Profile>> getAll();

    @Query("SELECT profile_id FROM profile ORDER BY profile_id DESC LIMIT 1")
    int getHighestProfileID();
}