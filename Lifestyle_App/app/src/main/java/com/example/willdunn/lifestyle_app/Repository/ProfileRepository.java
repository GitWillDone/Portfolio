package com.example.willdunn.lifestyle_app.Repository;

import android.app.Application;
import android.os.AsyncTask;

import com.example.willdunn.lifestyle_app.Model.Profile;
import com.example.willdunn.lifestyle_app.Model.ProfileDao;
import com.example.willdunn.lifestyle_app.Model.ProfileRoomDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ProfileRepository {

    private ProfileDao mProfileDao;
    private int mMaxProfileID;
    private LiveData<Integer> mCurrentProfileID;
    private static final MutableLiveData<Profile> mProfile = new MutableLiveData<Profile>();
    private LiveData<List<Profile>> mAllProfiles;

    public ProfileRepository(Application application) {
        ProfileRoomDatabase db = ProfileRoomDatabase.getDatabase(application);
        mProfileDao = db.profileDao();
        mAllProfiles = mProfileDao.getAll();
    }

    public int getProfileID(String user, String pass) throws ExecutionException, InterruptedException {
        return new getProfileIDAsyncTask(mProfileDao).execute(user, pass).get();
    }

    public Profile getProfile(String user, String pass) throws ExecutionException, InterruptedException {
        return new getProfileAsyncTask(mProfileDao).execute(user, pass).get();
    }

    public int getHighestProfileID() throws ExecutionException, InterruptedException {
        return new getHighestProfileIDAsyncTask(mProfileDao).execute().get();
    }

    public static int processValue(Integer myInt) {
        return myInt;
    }

    public static void processInsertProfile(Profile profile) {
        mProfile.setValue(profile);
    }


    public static MutableLiveData<Profile> processUser(Profile profile) {
        mProfile.setValue(profile);
        return mProfile;
    }

    private static class getProfileAsyncTask extends AsyncTask<String, Void, Profile> {
        private ProfileDao mAsyncTaskDao;

        getProfileAsyncTask(ProfileDao dao) {
            try {
                mAsyncTaskDao = dao;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Profile doInBackground(String... strings) {
            return mAsyncTaskDao.getProfile(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(Profile profile) {
            processUser(profile);
        }

    }


    private static class getProfileIDAsyncTask extends AsyncTask<String, Void, Integer> {
        private ProfileDao mAsyncTaskDao;

        getProfileIDAsyncTask(ProfileDao dao) {
            try {
                mAsyncTaskDao = dao;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Integer doInBackground(final String... strings) {
            return mAsyncTaskDao.getProfileID(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            processValue(integer);
        }
    }

    private static class getHighestProfileIDAsyncTask extends AsyncTask<Void, Void, Integer> {
        private ProfileDao mAsyncTaskDao;

        getHighestProfileIDAsyncTask(ProfileDao dao) {
            try {
                mAsyncTaskDao = dao;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            return mAsyncTaskDao.getHighestProfileID();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            processValue(integer);
        }
    }

    public MutableLiveData<Profile> getProfile(int profileID) {
        mProfile.setValue(mProfileDao.getProfile(profileID));
        return mProfile;
    }

    public LiveData<List<Profile>> getAll() {
        return mAllProfiles;
    }

    public void insert(Profile profile) {
        new insertAsyncTask(mProfileDao).execute(profile);
    }

    private static class insertAsyncTask extends AsyncTask<Profile, Void, Profile> {

        private ProfileDao mAsyncTaskDao;

        insertAsyncTask(ProfileDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Profile doInBackground(final Profile... params) {
            mAsyncTaskDao.insert(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(Profile profile) {
            processInsertProfile(profile);
        }
    }

    public LiveData<Profile> getCurrentUser() {
        return mProfile;
    }
}
