package com.example.willdunn.lifestyle_app.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile")
public class Profile {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "profile_id")
    private int profile_id;

    @ColumnInfo(name = "height")
    private int height;

    @ColumnInfo(name = "age")
    private int age;

    @ColumnInfo(name = "goal")
    private int goal;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "activity_level")
    private String activity_level;

    @ColumnInfo(name = "file_path")
    private String file_path;

    @ColumnInfo(name = "weight")
    private double weight;

    @ColumnInfo(name = "sex")
    private boolean sex;

    @ColumnInfo(name = "password")
    private String password;

    // constructors
    /// minimum use constructor that only requires the username to build the profile - helps follow builder pattern
    public Profile(String username) {
        this.username = username;
        weight = 0;
        height = 0;
        age = 0;
        goal = 0;
        activity_level = "Moderate";
        this.password = "";
    }

    /// common-use case constructor where parameters are based around the ability to get all but certain data fields; location not supported
    public Profile(String _name, double _weight, int _height, int _age, int _goal, String _activity, boolean _sex, String _password) {
        username = _name;
        weight = _weight;
        height = _height;
        age = _age;
        goal = _goal;
        activity_level = _activity;
        sex = _sex;
        password = _password;
    }

    /// constructor where all fields are able to be entered - enabled by lat/lon support on location
    public Profile(String _name, double _weight, int _height, int _age, int _goal, String _activity, String _imagePath, boolean _sex, String _password, int _profileID) {
        username = _name;
        weight = _weight;
        height = _height;
        age = _age;
        goal = _goal;
        activity_level = _activity;
        file_path = _imagePath;
        sex = _sex;
        profile_id = _profileID;
        password = _password;
    }


    // getters and setters
    public Bitmap getProfilePicture() {
        return BitmapFactory.decodeFile(file_path);
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActivity_level() {
        return activity_level;
    }

    public void setActivity_level(String activity_level) {
        this.activity_level = activity_level;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
