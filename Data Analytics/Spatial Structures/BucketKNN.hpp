#pragma once

#include "Point.hpp"
#include <vector>
#include <set>
#include <memory>


template<int Dimension> //The dimension of the points.  This can be used like any other constant within.
class BucketKNN {
private:
    std::array<float, Dimension> minP, maxP, bucketSize;
    std::vector<std::vector<Point<Dimension> > > buckets;
    int _divisions;

public:

    int _numPoints;

    int bucketCoordsToIndex(const Point<Dimension> &p) const {
        int index = 0;
        Point<Dimension> loc = ((p - minP) / _divisions);
        for (int i = 0; i < Dimension; ++i) {
            index += std::clamp((int) loc[i], 0, _divisions - 1) * pow(_divisions, Dimension - i - 1);
        }
        return index;
    }

    //constructor
    BucketKNN(const std::vector<Point<Dimension> > &points, int divisions_) {
        _divisions = divisions_;

        //start by getting AABB bounds from the functions supplied by Ben
        AABB<Dimension> bounds = getBounds(points);
        minP = bounds.mins;
        maxP = bounds.maxs;

        //vector of buckets with length numberOfSplitsPerDimension^numDimensions
        buckets = std::vector<std::vector<Point<Dimension>>>((int) pow(divisions_, Dimension));
        for (int i = 0; i < Dimension; ++i) {
            bucketSize[i] = (maxP[i] - minP[i]) / _divisions;
        }

        //you can insert by finding the real coordinates, then bucket coords, then the index on a laid out vector
        for (auto p : points) {
            //put the point in the bucket index
            std::array<int, Dimension> index;
            for (int i = 0; i < Dimension; ++i) {
                int temp = (p[i] - minP[i]) / bucketSize[i];
                index[i] = std::min(temp, _divisions - 1); //todo should i be clamping here instead???
            }
            int new_index = 0;
            for (int i = 0; i < Dimension; ++i) {
                new_index += index[i] * pow(divisions_, i);
            }
            buckets[new_index].push_back(p);
            _numPoints++;
        }

        /* //Uncomment the star-brackets to visualize the points and buckets
        for(BUCKET b: buckets){
            std::cout << b.size() << std::endl;
        }

        for (int i = 0; i < buckets.size(); ++i) {
            std::cout << "Bucket #" << i << std::endl;
            for (int j = 0; j < buckets[i].size(); ++j) {
                std::cout << buckets[i][j] << std::endl;
            }
        }
         */
    }

    //minP/maxP should be an array of ints
    std::array<int, Dimension> nextBucket(std::array<int, Dimension> current, std::array<int, Dimension> minCoords,
                                          std::array<int, Dimension> maxCoords) const {

        current[Dimension - 1]++; //increment the last dimension
        for (int i = Dimension - 1; i > 0; i--) {
            //if we need to "carry"
            if (current[i] > maxCoords[i]) {
                //reset this dimension
                current[i] = minCoords[i];
                //and add to the next "digit"
                current[i - 1]++;
            } else {
                //no more carries... we're done here
                break;
            }
        }
        return current;
    }

    void visualizePoints(const std::vector<Point<Dimension> > &points) const {
        for (auto point : points) {
            std::cout << point << std::endl;
        }
    }

    //puts all points that fall into a radius from a POI into the input vector of points
    void
    process(const Point<Dimension> &p, const float &radius, const std::vector<std::vector<Point<Dimension> > > &buck,
            std::vector<Point<Dimension> > &sphere) const {
        for (auto point : buck) {
            if (distance(point, p) <= radius) { //determine if a point is within the radius from the POI
                sphere.push_back(point);
            }
        }
    }

    std::vector<Point<Dimension> > rangeQuery(const Point<Dimension> &p, float radius) const {
        //start by getting the bounding box by subtracting/adding the radius from the point
//        Point<Dimension> minBound = Point<Dimension>{p - radius};
//        Point<Dimension> maxBound = Point<Dimension>{p + radius};
//        Point<Dimension> coords = p;
//        std::array<int, Dimension>  minIdx = pointToIntArr(minBound);
//        std::array<int, Dimension>   maxIdx = pointToIntArr(maxBound);
//        std::array<int, Dimension>  curr = pointToIntArr(coords);
////        std::cout << "POI: " << p << " Min: " << minBound << " Max: " << maxBound << std::endl; //visualize the min/max bounds of the POI
//
//        //then get the minP/maxP bounds from the bounding box, which will make up a cube of interest
//        std::vector<Point<Dimension> > sphere; //not the actual sphere, rather the points contained in the range query
//        //loop through all the buckets in the cube and see if the points within fall into the radius.
//        for (curr = minIdx; //start at the beginning
//             curr != nextBucket(maxIdx, minIdx, maxIdx); //stop once we go past the end
//             curr = nextBucket(curr, minIdx, maxIdx)) { //advance to the next set of coordinates
//            int buckIdx = bucketCoordsToIndex(coords);
//            process(p, radius, buckets[buckIdx], sphere); //add to the sphere all the points that are within the radius
//        }

        std::array<int, Dimension> min_index, max_index;
        std::array<float, Dimension> min_bound, max_bound;
        for (int i = 0; i < Dimension; ++i) {
            min_bound[i] = p[i] - radius;
            int temp = (min_bound[i] - minP[i]) / bucketSize[i];
            min_index[i] = std::max(temp, 0);
            max_bound[i] = p[i] + radius;
            temp = (max_bound[i] - minP[i]) / bucketSize[i];
            max_index[i] = std::min(temp, _divisions - 1);
        }

        std::vector<Point<Dimension> > sphere;
        std::array<int, Dimension> current = min_index;

        while (true) {
            int new_index = 0;
            for (int i = 0; i < Dimension; ++i) {
                new_index += current[i] * pow(_divisions, i);
            }
            for (Point<Dimension> point : buckets[new_index]) {
                if (distance(point, p) <= radius) {
                    sphere.push_back(point);
                }
            }
            if (current == max_index) {
                break;
            }
            current = nextBucket(current, min_index, max_index);
        }

        //visualizePoints(sphere); //comment out when not testing

        return sphere;
    }


    std::vector<Point<Dimension> > KNN(const Point<Dimension> &p, int k) const {
        std::vector<Point<Dimension> > nearestNeighbors; //will return exact points

        assert(_numPoints >= k);

        //optimization to exit KNN if k is 0
        if (k == 0) return nearestNeighbors;

        float radiusRange = bucketSize[0];
        DistanceComparator comp = DistanceComparator<Dimension>(p); //comparator that will be used for sorting

        std::vector<Point<Dimension> > pointHolder = rangeQuery(p, radiusRange); //used to grab large range of points
        while (pointHolder.size() < k) {
            pointHolder = rangeQuery(p, radiusRange);
            if (pointHolder.size() == k) { return pointHolder; } //optimization to exit the loop if

            //sort the points so that you can grab the ones, if you've gone over k, that are the best fit
            if (pointHolder.size() > k) {
                std::sort(pointHolder.begin(), pointHolder.end(), comp);
                for (int i = 0; i <= k; ++i) {
                    nearestNeighbors.push_back(pointHolder[i]);
                }
                return nearestNeighbors;
            }

            //increase the radius in a linear fashion
            radiusRange += bucketSize[0];
        }

        //visualizePoints(nearestNeighbors); //comment out when not testing

        return nearestNeighbors;

    }
};
