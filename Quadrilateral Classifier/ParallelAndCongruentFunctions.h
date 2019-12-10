//
// Created by Will Dunn on 1/15/19.
//
#include <vector>
#include <cmath>
#include <iostream> //todo delete when done testing
#pragma once

/**
 * Side lengths and angles used for these functions are based on calculations from the points and moving in clockwise order.
 * An example of the numbering of sides is shown in the diagram below, with the origin of that side being respective to
 * the numbering of the angle as well.
 *
 *              22222222
 *               ______
 *             1|      |3
 *             1|      |3
 *             1|______|3
 *
 *              4444444
 *
 * @param points
 */
bool hasBothSidesParallel(const std::vector<double> &points);
bool hasOneParallelSide(const std::vector<double> &points);
bool hasTwoCongruentSides(const std::vector<double> &points);
bool hasFourCongruentSides(const std::vector<double> &points);
bool hasFourRightAngles(const std::vector<double> &points);
int parallelSideCount(const std::vector<double> &points);
int congruentSideCount(const std::vector<double> &points);
