package com.example.willdunn.lifestyle_app.Model.Dto;

import java.util.ArrayList;
import java.util.List;

public class Height {
    public List<Integer> heights = new ArrayList<>();

    //85 is a magic number, it is 7 feet tall in inches

    public Height() {
        for (int i = 12; i < 85; i++) {
            heights.add(i);
        }
    }
}
