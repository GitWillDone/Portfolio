#include "StoreES.h"
#include "BankES.h"

/*
 * argv[0]:  program name
 * argv[1]:  chance, for each minute, that a new customer will arrive
 * argv[2]:  upper limit of time it takes for a customer to complete a transaction; lower limit is 0
 * argv[3]:  random seed
 */
int main(int argc, char *argv[]) {
    //set up the seed based on the third argument
    srand(atoi(argv[3]));
    double chanceIncrement = std::stod(argv[1]) / 60; //chance for a new customer to arrive each second

    /* for store */
    //determine how many customers will show up for the day based on the run time and the argv[1];
    int upperBoundOfService = (std::stod(argv[2]) * 60);
    simulateGrocery(chanceIncrement, upperBoundOfService);

    /* for bank */
    DESim mySim;
//    mySim.setDebugOn(true);
    EventStruct myEvent;

    //get the number of events that will occur
    double chanceOfArrival = 0;
    for(int i = 0; i < SIM_LENGTH; i++) chanceOfArrival += chanceIncrement;

    for (int i = 0; i < (int) chanceOfArrival; i++) {

        myEvent.eventTime = rand() % SIM_LENGTH;
        myEvent.duration = rand() % upperBoundOfService;
        myEvent.event = ARRIVAL;
        mySim.addEvent(myEvent);
    }
    mySim.runSim();


    return 0;
}