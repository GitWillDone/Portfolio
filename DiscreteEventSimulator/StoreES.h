//
// Created by Will Dunn on 2019-03-28.
//

#pragma once

#include "businessHelps.hpp"
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

int lineWaitTime[NUM_TELLERS];
int totalCustomers = 0;
double totalTime = 0;
std::vector<int> timeCount;
bool operator<(const Simulator& left, const Simulator& right){
    return left.totalServiceTime > right.totalServiceTime;
}

///finds the index of the shortest line for the store
int findShortestLine(const std::vector<std::priority_queue<Simulator>> &lines, int timeElapsed) {
    int shortestLineIndex = 0;
    for (int i = 0; i < NUM_TELLERS; i++) {
        if (lineWaitTime[i] < lineWaitTime[shortestLineIndex]) shortestLineIndex = i;
    }
    return shortestLineIndex;
}

int getTimeForPersonInFront(int index, int timeElapse) {
    if (lineWaitTime[index] == 0) return 0; //there is no one ahead of them, so they are helped right away
    else if (lineWaitTime[index] < timeElapse) return 0;
    return lineWaitTime[index];
}

void simulateGrocery(double chanceIncrement, int upperBoundOfService) {//set up the lines for the store - each holds a time in front
    std::vector<std::priority_queue<Simulator>> lines(NUM_TELLERS);
    double chanceOfArrival = 0, payload = 0;
    for (int i = 0; i < SIM_LENGTH; i++) {
        //using the chance, see if a customer arrives
        chanceOfArrival += chanceIncrement;
        while (chanceOfArrival > 1) {
            chanceOfArrival--;
            //determines how long it will take for the customer to get through the line
            payload = rand() % upperBoundOfService;
            int indexForLine = findShortestLine(lines, i); //i is used to see if the last time in the line expired
            
            //determine where that customer is going to go, and put them in that line
            lines[indexForLine].push(Simulator(i, payload, getTimeForPersonInFront(indexForLine, i)));
            
            //update the last time for that line, which is the prior persons end + payload
            if (getTimeForPersonInFront(indexForLine, i) > i) {
                lineWaitTime[indexForLine] = getTimeForPersonInFront(indexForLine, i) + payload; //for wait time
            } else {
                lineWaitTime[indexForLine] = i + payload; //for 0 wait time
            }
            
            totalCustomers++;
            
            //get the amount of time this person will take
            timeCount.push_back(lineWaitTime[indexForLine] - i);
        }
    }
    
    //get the different percentiles and print them out
    std::sort(timeCount.begin(), timeCount.end());
    double tenthPercentile, fiftiethPercentile, nintiethPercentile;
    int tenthOfTimeVec = timeCount.size() / 10;
    for (int i = 0; i < timeCount.size(); i++) {
        if(i <= tenthOfTimeVec) tenthPercentile += timeCount[i];
        else if (i >= timeCount.size() - tenthOfTimeVec) nintiethPercentile += timeCount[i];
        else fiftiethPercentile += timeCount[i];
        totalTime += timeCount[i];
    }
    
    std::cout << "GROCERY STORE RESULTS:\n";
    std::cout << "Total Customers: " << totalCustomers << std::endl;
    std::cout << "Average Time (minutes): " << totalTime / (60 * totalCustomers) << std::endl;
    std::cout << "10th Percentile (minutes): " << tenthPercentile / (60 * (totalCustomers * .1)) << std::endl;
    std::cout << "50th Percentile  (minutes): " << fiftiethPercentile / (60 * (totalCustomers * .8)) << std::endl;
    std::cout << "90th Percentile  (minutes): " << nintiethPercentile / (60 * (totalCustomers * .1)) << std::endl << std::endl;
}