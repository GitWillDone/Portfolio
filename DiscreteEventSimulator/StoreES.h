//
// Created by Will Dunn on 2019-03-28.
//

#pragma once

#include <iostream>
#include <queue>

struct Simulator{
    int arrivalTime;
    int timeToService;
    int totalServiceTime; //will be filled out once you've the event has fired; starts on arrival, ends after service

    Simulator(int arrival, int payload, int timeRemainingForService){
        arrivalTime = arrival;
        timeToService = payload;
        totalServiceTime = arrival + timeRemainingForService + payload;
    }
};

///allows us to sort in ascending order
bool operator<(const Simulator& left, const Simulator& right);
int findShortestLine(const std::vector<std::priority_queue<Simulator>> &lines, int timeElapsed);
int getTimeForPersonInFront(int index, int timeElapse);
void simulateGrocery(double chanceIncrement, int upperBoundOfService);