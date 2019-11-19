package com.example.willdunn.lifestyle_app.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {


    private int profile_id;

    private int height;

    private int age;

    private int goal;

    private String name;

    private String activity_level;

    private String file_path;

    private double weight;

    private boolean sex;

    /// minimum use constructor that only requires the name to build the profile - helps follow builder pattern
    public User(String _name) {
        name = _name;
        weight = 0;
        height = 0;
        age = 16;
        goal = 0;
        activity_level = "Moderate";
    }

    /// common-use case constructor where parameters are based around the ability to get all but certain data fields; location not supported
    public User(String _name, double _weight, int _height, int _age, int _goal, String _activity, boolean _sex) {
        name = _name;
        weight = _weight;
        height = _height;
        age = _age;
        goal = _goal;
        activity_level = _activity;
        sex = _sex;
    }

    /// constructor where all fields are able to be entered - enabled by lat/lon support on location
    public User(String _name, double _weight, int _height, int _age, int _goal, String _activity, String _imagePath, boolean _sex, int _profileID) {
        name = _name;
        weight = _weight;
        height = _height;
        age = _age;
        goal = _goal;
        activity_level = _activity;
        file_path = _imagePath;
        sex = _sex;
        profile_id = _profileID;
    }

    protected User(Parcel in) {
        name = in.readString();
        weight = in.readDouble();
        activity_level = in.readString();
        goal = in.readInt();
        height = in.readInt();
        age = in.readInt();
        file_path = in.readString();
        sex = in.readByte() != 0;
        profile_id = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Getters
    public Bitmap getProfilePicture() {
        return BitmapFactory.decodeFile(file_path);
    }

    public User getUser() {
        return this;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public int getHeight() {
        return height;
    }

    public int getAge() {
        return age;
    }

    public int getGoal() {
        return goal;
    }

    public String getName() {
        return name;
    }

    public String getActivity_level() {
        return activity_level;
    }

    public String getFile_path() {
        return file_path;
    }

    public double getWeight() {
        return weight;
    }

    public boolean getSex() {
        return sex;
    }


    // Setters
    public void setAge(int age) {
        this.age = age;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePicture(String profilePicture) {
        file_path = profilePicture;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setProfileID(int profileID) {
        profile_id = profileID;
    }

    // Activity level is categorical and therefore requires a conversion from qualitative to quantitative.
    public void setActivityLevel(String activityLevel) {
//        if(activityLevel == "Sedentary") {
//            activity_level = 1.2;
//        }
//        else if (activityLevel == "Moderate") {
//            activity_level = 1.55;
//        }
//        else if (activityLevel == "Active") {
//            activity_level = 1.9;
//        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(weight);
        dest.writeString(activity_level);
        dest.writeInt(goal);
        dest.writeInt(height);
        dest.writeInt(age);
        dest.writeString(file_path);
        dest.writeByte((byte) (sex ? 1 : 0));
        dest.writeInt(profile_id);
    }
}
