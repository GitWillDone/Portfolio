//
// Created by Will Dunn on 2019-03-28.
//

#pragma once

#include <iostream>
#include <string>
#include <vector>
#include <queue>
#include <unordered_map>

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

// a standard way of printing events
void printEvent(const EventStruct &event) {
    std::cout << " Time: " << event.eventTime;
    std::cout << ", Duration: " << event.duration;
    std::cout << ", Event type: ";
    if (event.event == ARRIVAL) std::cout << "ARRIVAL";
    else std::cout << "DEPARTURE";
}

void printEventDurations(const EventStruct &event){

}

// how to compare event times for the priority queue
struct compareEventTime {
    bool operator()(const EventStruct &lhs, const EventStruct &rhs) const {
        return lhs.eventTime > rhs.eventTime;
    }
};

// our Discrete Event Simulation class
class DESim {
private:
    // event queue -- priority queue with events queued by event time
    std::priority_queue<EventStruct, std::vector<EventStruct>, compareEventTime> eventQueue;
    // wait queue -- priority queue with waiting customers queued by arrival time
    std::queue<EventStruct> bankQueue;
    TimeType currentTime = 0;
    bool debugOn = false;
    int tellersAvailable = NUM_TELLERS;
public:
    void addEvent(EventStruct &event);

    void addBankQueueCustomer(EventStruct &event);

    void setDebugOn(bool value) { debugOn = value; };

    void printTellerNumberChange();

    void runSim();
}; // end DESim

// add a priority queue event
void DESim::addEvent(EventStruct &event) {
    if (debugOn) {
        std::cout << "[" << currentTime << "] Adding event- ";
        printEvent(event);
        std::cout << std::endl;
    }
    eventQueue.push(event);
}

// add a bank queue customer (not really an event)
void DESim::addBankQueueCustomer(EventStruct &event) {
    if (debugOn) {
        std::cout << "[" << currentTime << "] Adding bank queue customer: ";
        printEvent(event);
        std::cout << std::endl;
    }
    bankQueue.push(event);
    event.waitTime = currentTime;
}

// indicate that the number of available tellers has changed
void DESim::printTellerNumberChange() {
    if (debugOn) {
        std::cout << "[" << currentTime << "] Current number of tellers available: " << tellersAvailable << std::endl;
    }
}

// run the simulation
void DESim::runSim() {
    std::vector<int> durationCount(eventQueue.size());
    int totalCustomers = 0;

    while (!eventQueue.empty()) {
        EventStruct nextEvent = eventQueue.top(); // get next event in priority queue
        currentTime = nextEvent.eventTime;
        eventQueue.pop();
        switch (nextEvent.event) {
            case ARRIVAL:
                totalCustomers++;
                if (tellersAvailable) {
                    durationCount.push_back(nextEvent.duration);
                    // enter departure event in eventQueue
                    nextEvent.eventTime = currentTime + nextEvent.duration;
                    nextEvent.event = DEPARTURE;
                    addEvent(nextEvent);
                    tellersAvailable--;
                    printTellerNumberChange();
                } else {
                    // no tellers available, put customer in bank queue
                    addBankQueueCustomer(nextEvent);
                }
                break;
            case DEPARTURE:
                if (!bankQueue.empty()) {
//                    durationCount.push_back(nextEvent.duration); //todo this is wrong
                    EventStruct nextCustomer = bankQueue.front();
                    bankQueue.pop();
                    nextCustomer.eventTime = currentTime + nextEvent.duration;
                    nextCustomer.event = DEPARTURE;
                    addEvent(nextCustomer);
                } else {
                    tellersAvailable++;
                    printTellerNumberChange();
                }
                break;
            default:
                std::cout << "ERROR: Should never get here! " << std::endl;
        }
    }

    double totalTime, tenthPercentile, fiftiethPercentile, nintiethPercentile;
    int tenthOfTimeVec = durationCount.size() / 10;
    std::sort(durationCount.begin(), durationCount.end());

    for (int i = 0; i < durationCount.size(); i++) {
        if(i <= tenthOfTimeVec) tenthPercentile += durationCount[i];
        else if (i >= durationCount.size() - tenthOfTimeVec) nintiethPercentile += durationCount[i];
        else fiftiethPercentile += durationCount[i];
        totalTime += durationCount[i];
    }

    std::cout << "BANK RESULTS:\n";
    std::cout << "Total Customers: " << totalCustomers << std::endl;
    std::cout << "Average Time (minutes): " << totalTime / (60 * totalCustomers) << std::endl;
    std::cout << "10th Percentile (minutes): " << tenthPercentile / (60 * (totalCustomers * .1)) << std::endl;
    std::cout << "50th Percentile  (minutes): " << fiftiethPercentile / (60 * (totalCustomers * .8)) << std::endl;
    std::cout << "90th Percentile  (minutes): " << nintiethPercentile / (60 * (totalCustomers * .1)) << std::endl;
}