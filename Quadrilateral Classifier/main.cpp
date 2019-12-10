#include <iostream>
#include <vector>
#include <string>
#include "ParallelAndCongruentFunctions.h"
#include "FilterFunctions.h"

//prototyping
void fillPointsCoordinates(int argc, char *const *argv, std::vector<double> &points);

/**
 *
 * @param argv
 *  -contains four points (the first being 0,0), which make up a quadrilateral, with a space between each point.
 */

int main(int argc, char *argv[]) {
    std::vector<double> points{0, 0}; //the first point will always be (0, 0)

    //todo uncomment argv[i] and delete two below variables; change i to start at 1
//    std::string greg = "14 0 10 10 4 10";
//    argc = greg.size();


    //fill vector with points from arguments
    for (int i = 0; i < argc; i++) {
//        if(greg[i] == ' ') continue; //todo delete me after testing
        std::string strToConvertToDecimal = argv[i]; //zero added to ensure a find() result of 0 means unfound '.'
//        std::string strToConvertToDecimal(1, greg[i]);
        std::size_t pos = strToConvertToDecimal.find(".");
        if(pos != std::string::npos || strToConvertToDecimal[0] == '.'){ //dealing with a decimal number
            strToConvertToDecimal = strToConvertToDecimal.substr(0, pos) + strToConvertToDecimal.substr(pos, 3);
        }
        points.push_back(std::stod(strToConvertToDecimal));
    }

    //go through hierarchy of determining which type of quadrilateral the input is utilizing finite states
    int quadState = 0;

    //state check for parallelogram, trap, or kite route
    if (hasBothSidesParallel(points)) quadState = 1;
    else if (hasOneParallelSide(points)) quadState = 5;
    else if (hasTwoCongruentSides(points)) quadState = 6;
    //else the state remains at 0;

    //further change the state, if necessary, based on further checks to the hierarchy of parallelograms
    if (quadState == 1) quadState = parallelogramFilter(points);

    //output based on the state
    std::vector<std::string> typeOfQuad{"quadrilateral", "parallelogram", "rectangle", "rhombus", "square",
                                        "trapezoid", "kite"};
    std::cout << typeOfQuad[quadState] << std::endl;
    return 0;
}