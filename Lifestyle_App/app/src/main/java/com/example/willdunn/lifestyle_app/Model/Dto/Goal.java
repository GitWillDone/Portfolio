package com.example.willdunn.lifestyle_app.Model.Dto;

import java.util.ArrayList;
import java.util.List;

/***
 * Goals are broken down into three categories that are ties to integers 0-2.  They are:
 *  - lose weight = 2
 * 	- gain weight = 1
 * 	- maintain weight = 0
 */
public class Goal {

    public List<String> goals = new ArrayList<>();

    public Goal(){
        goals.add("Maintain Weight");
        goals.add("Gain Weight");
        goals.add("Lose Weight");
    }
}
