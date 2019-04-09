//
//  businessHelps.hpp
//  will_simulator
//
//  Created by Nicole Morris on 4/9/19.
//  Copyright © 2019 Nicole Morris. All rights reserved.
//

#ifndef businessHelps_hpp
#define businessHelps_hpp

#include <iostream>
#include <stdio.h>

// define the time data type
#define TimeType int

// define constants used in our simulation
const int SIM_LENGTH = 43200; //12 hours
#define NUM_TELLERS 6

// define event types
typedef enum {
    ARRIVAL, DEPARTURE
} EventType;

// define an event struct to store events
typedef struct {
    TimeType eventTime;
    TimeType duration;
    EventType event;
    TimeType waitTime;
} EventStruct;



// how to compare event times for the priority queue
struct compareEventTime {
    bool operator()(const EventStruct &lhs, const EventStruct &rhs) const {
        return lhs.eventTime > rhs.eventTime;
    }
};

#endif /* businessHelps_hpp */
