package com.example.willdunn.lifestyle_app.Model.Dto;

import java.util.ArrayList;
import java.util.List;

public class Gender {

    public List<String> generType = new ArrayList<>();

    public Gender() {
        generType.add("SELECT GENDER");
        generType.add("M");
        generType.add("F");
    }
}
