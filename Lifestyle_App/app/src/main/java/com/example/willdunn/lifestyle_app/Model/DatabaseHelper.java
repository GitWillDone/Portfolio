package com.example.willdunn.lifestyle_app.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "profile";

    public DatabaseHelper(Context context) {
        super(context, "LifestyleApp.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProfileTable = "CREATE TABLE profile(profile_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, age INTEGER, height INTEGER, weight REAL, sex INTEGER, goal INTEGER, activity_level TEXT, file_path TEXT)";
        db.execSQL(createProfileTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS profile");
        onCreate(db);
    }

    public boolean addData(String username, String password, int age, int height, double weight, boolean sex, int goal, String activity_level, String file_path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("age", age);
        contentValues.put("height", height);
        contentValues.put("weight", weight);
        contentValues.put("sex", sex);
        contentValues.put("goal", goal);
        contentValues.put("activity_level", activity_level);
        contentValues.put("file_path", file_path);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateProfile(int profileId, String username, String password, int age, int height, double weight, boolean sex, int goal, String activity_level, String file_path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        if(password != null) contentValues.put("password", password);
        contentValues.put("age", age);
        contentValues.put("height", height);
        contentValues.put("weight", weight);
        contentValues.put("sex", sex);
        contentValues.put("goal", goal);
        contentValues.put("activity_level", activity_level);
        if(file_path != null && !file_path.equals("")) contentValues.put("file_path", file_path);


        String filter = "profile_id = " + profileId;

        long result = db.update(TABLE_NAME, contentValues, "profile_id = ?", new String[] { Integer.toString(profileId) });

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the data from database
     * @return
     */
    public int checkUser(String user, String pass){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT profile_id FROM " + TABLE_NAME + " where username = '" + user + "' and password = '" + pass + "'";
            Cursor data = db.rawQuery(query, null);
            data.moveToFirst();
            int check = data.getInt(0);
            if (check > 0) return check;
            data.close();
            return 0;
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }
    }

    public User getUser(int id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT profile_id, age, height, weight, sex, goal, activity_level, file_path, username FROM " + TABLE_NAME + " where profile_id = " + id;
            Cursor data = db.rawQuery(query, null);
            data.moveToFirst();
            int profile_id = data.getInt(0);
            int age = data.getInt(1);
            int height = data.getInt(2);
            double weight = data.getDouble(3);
            int sex = data.getInt(4);
            int goal = data.getInt(5);
            String activity_level = data.getString(6);
            String file_path = data.getString(7);
            String username = data.getString(8);

            boolean sex_bool = false;
            if(sex > 0) sex_bool = true;

            User user = new User(username, weight, height, age, goal, activity_level, file_path, sex_bool, profile_id);

            data.close();
            return user;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
