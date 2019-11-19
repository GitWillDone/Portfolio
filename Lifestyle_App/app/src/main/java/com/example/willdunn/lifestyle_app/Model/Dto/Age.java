package com.example.willdunn.lifestyle_app.Model.Dto;

import java.util.ArrayList;
import java.util.List;

public class Age {
    public List<Integer> ageSet = new ArrayList<>();

    public Age() {
        for (int i = 16; i < 150; i++){
            ageSet.add(i);
        }
    }

}
