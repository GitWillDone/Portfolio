package com.example.willdunn.lifestyle_app.View;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.willdunn.lifestyle_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class WeatherActivity extends AppCompatActivity {

    private TextView mCityWeather, mDescWeather, mTemp, mHigh, mLow;
    private String userLocation;
    private ConstraintLayout layout;
    private AnimationDrawable animationDrawable;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_weather);

        //transition background
        layout = findViewById(R.id.weather_transition_background);
//        animationDrawable = (AnimationDrawable) layout.getBackground();
//        animationDrawable.setEnterFadeDuration(1500);
//        animationDrawable.setExitFadeDuration(1500);
//        animationDrawable.start();

        Intent receivedIntent = getIntent();
        findWeather(receivedIntent.getStringExtra("BTN_STRING_LOC"));
    }

    //the tX fields can be changed to be determined on how we want the information displayed
    public void findWeather(String userLocation) {
        //format for lat/lon from openweathermap.com
        String url = "http://api.openweathermap.org/data/2.5/weather?" + userLocation + "&appid=6e5e30e8992a1adf3df1e086049a2378&units=Imperial";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject mainObject = response.getJSONObject("main");
                    JSONArray weatherArr = response.getJSONArray("weather");
                    JSONObject weatherJSON = weatherArr.getJSONObject(0);

                    String temp = String.valueOf(mainObject.getDouble("temp"));
                    String weatherDescription = weatherJSON.getString("description");
                    String city = response.getString("name");
                    String high = String.valueOf(mainObject.getDouble("temp_max"));
                    String low = String.valueOf(mainObject.getDouble("temp_min"));
                    String main = weatherJSON.getString("main");

                    //get the text views from the fragment
                    mCityWeather = findViewById(R.id.tv_city_weather);
                    mDescWeather = findViewById(R.id.tv_description_weather);
                    mTemp = findViewById(R.id.tv_degrees_f);
                    mHigh = findViewById(R.id.tv_description_high);
                    mLow = findViewById(R.id.tv_description_low);
                    imageView = findViewById(R.id.image_icon);

                    //replace the text values
                    mTemp.setText(String.valueOf(Math.round(Double.parseDouble(temp))) + (char) 0x00B0);
                    mDescWeather.setText(String.valueOf(weatherDescription));
                    mCityWeather.setText(String.valueOf(city));
                    mHigh.setText("High: " + Math.round(Double.parseDouble(high)) + (char) 0x00B0);
                    mLow.setText("Low: " + Math.round(Double.parseDouble(low)) + (char) 0x00B0);

                    if(main.equals("Thunderstorm")) {
                        layout.setBackgroundResource(R.drawable.third_layer);
                        imageView.setBackgroundResource(R.drawable.ic_icon8_rain_cloud);
                    }
                    else if(main.equals("Drizzle")) {
                        layout.setBackgroundResource(R.drawable.third_layer);
                        imageView.setBackgroundResource(R.drawable.ic_icon8_rain_cloud);
                    }
                    else if(main.equals("Rain")) {
                        layout.setBackgroundResource(R.drawable.third_layer);
                        imageView.setBackgroundResource(R.drawable.ic_icon8_rain_cloud);
                    }
                    else if(main.equals("Snow")) {
                        layout.setBackgroundResource(R.drawable.second_layer);
                        imageView.setBackgroundResource(R.drawable.ic_snowflake);
                    }
                    else if(main.equals("Clouds")) {
                        layout.setBackgroundResource(R.drawable.first_layer);
                        imageView.setBackgroundResource(R.drawable.ic_breeze);
                    }
                    else {
                        layout.setBackgroundResource(R.drawable.first_layer);
                        imageView.setBackgroundResource(R.drawable.ic_clear);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }
}
