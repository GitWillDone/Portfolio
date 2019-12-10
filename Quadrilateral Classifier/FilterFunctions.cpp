//
// Created by Will Dunn on 1/15/19.
//

#include "FilterFunctions.h"
#include "ParallelAndCongruentFunctions.h"

int parallelogramFilter(const std::vector<double> &points){
    int quadState = 1;

    //check for 4 congruent sides (not inclusive) to advance to rhombus state or stay at parallelogram
    if(congruentSideCount(points) > 1) quadState = 3;
    else if(hasFourRightAngles(points)) quadState = 2; //check non-rhombi parallelogram to see if rectangle

    //check if rhombi is square
    if(quadState == 3 && hasFourRightAngles(points)) quadState = 4;

//    //check for right angles
//    if(hasFourRightAngles(points)) quadState = 2;
//
//    //check for congruent sides
//    if(hasFourCongruentSides(points)){
//        if(quadState == 2) quadState = 3; //todo test is catagorizing parallelogram as rhombus because you are not checking if all congruent sides are =
//        else quadState = 4;
//    }

    //return the state the parallelogram reached
    return quadState;
}

