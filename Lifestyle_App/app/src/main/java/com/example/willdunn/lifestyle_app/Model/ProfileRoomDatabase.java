package com.example.willdunn.lifestyle_app.Model;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Profile.class, StepTracker.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ProfileRoomDatabase extends RoomDatabase {
    public abstract ProfileDao profileDao();
    public abstract StepDao stepDao();

    private static volatile ProfileRoomDatabase INSTANCE;

    public static ProfileRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ProfileRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ProfileRoomDatabase.class, "LifestyleAppProfiles.db")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final ProfileDao mProfileDao;
        private final StepDao mStepsDao;

        PopulateDbAsync(ProfileRoomDatabase db) {
            mProfileDao = db.profileDao();
            mStepsDao = db.stepDao();
        }

        // Default population for the app
        @Override
        protected Void doInBackground(final Void... params) {
            // Start with a default profile.  This makes things flow more smoothly because the db is already created and we don't need to do a lot of logic to set up the db elsewhere, if we just do it here
            Profile nuProfile = new Profile("default", 150, 70, 33, 1, "Active", "", true, "password", 0);
            mProfileDao.insert(nuProfile);

            // Tie a default step counter to the default
            StepTracker nuStepCounter = new StepTracker();
            nuStepCounter.setStepTracker_id(0);
            nuStepCounter.setMondaySteps(11);
            nuStepCounter.setTuesdaySteps(22);
            nuStepCounter.setWednesdaySteps(33);
            nuStepCounter.setThursdaySteps(44);
            nuStepCounter.setFridaySteps(55);
            nuStepCounter.setSaturdaySteps(66);
            nuStepCounter.setSundaySteps(77);
            nuStepCounter.setLastUpdated(new Date());
            mStepsDao.insert(nuStepCounter);

            return null;
        }
    }
}
