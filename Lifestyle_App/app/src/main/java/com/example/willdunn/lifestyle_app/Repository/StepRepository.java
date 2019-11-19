package com.example.willdunn.lifestyle_app.Repository;

import android.app.Application;
import android.os.AsyncTask;

import com.example.willdunn.lifestyle_app.Model.ProfileRoomDatabase;
import com.example.willdunn.lifestyle_app.Model.StepDao;
import com.example.willdunn.lifestyle_app.Model.StepTracker;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StepRepository {
    private StepDao mStepDao;
    private static final MutableLiveData<StepTracker> mStepTracker = new MutableLiveData<StepTracker>();
    private LiveData<List<StepTracker>> mAllStepTrackers;

    public StepRepository(Application application) {
        ProfileRoomDatabase db = ProfileRoomDatabase.getDatabase(application);
        mStepDao = db.stepDao();
        mAllStepTrackers = mStepDao.getAll();
    }


    public static void processStepTracker(StepTracker stepTracker) {
        mStepTracker.setValue(stepTracker);
    }

    public StepTracker getStepTrackerByID(Integer profileID) throws ExecutionException, InterruptedException {
        return new StepRepository.getStepTrackerAsyncTask(mStepDao).execute(profileID).get();
    }

    private static class getStepTrackerAsyncTask extends AsyncTask<Integer, Void, StepTracker>{
        private StepDao mAsyncTaskDao;

        getStepTrackerAsyncTask(StepDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected StepTracker doInBackground(Integer... integers) {
            return mAsyncTaskDao.getStepTrackerByID(integers[0]);
        }

        @Override
        protected void onPostExecute(StepTracker stepTracker) {
            processStepTracker(stepTracker);
        }
    }

    public void insert(StepTracker stepTracker) {
        new StepRepository.insertAsyncTask(mStepDao).execute(stepTracker);
    }

    private static class insertAsyncTask extends AsyncTask<StepTracker, Void, StepTracker> {

        private StepDao mAsyncTaskDao;

        insertAsyncTask(StepDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected StepTracker doInBackground(final StepTracker... params) {
            mAsyncTaskDao.insert(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(StepTracker stepTracker) {
            processStepTracker(stepTracker);
        }
    }

    public LiveData<StepTracker> getCurrentStepTracker(){ return mStepTracker; }
}
