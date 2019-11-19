#pragma once

#include "Point.hpp"
#include <memory>
#include <queue>

class QuadTree {

public:

    QuadTree(const std::vector<Point<2> > &points, int maxSize) { //todo get rid of maxP size; it will always be 4
        std::vector<Point<2> > myPoints = points;
        root = std::unique_ptr<QuadTree::Node>(new QuadTree::Node(myPoints, maxSize, getBounds(myPoints)));
    }

    std::vector<Point<2> > rangeQuery(const Point<2> &p, float radius) const {
        std::vector<Point<2> > query = std::vector<Point<2>>();
        rangeQueryHelper(root, p, radius, query);
        return query;
    }

    std::vector<Point<2> > KNN(const Point<2> &p, int k) const {
        std::vector<Point<2>> heap = std::vector<Point<2>>();
        KNNHelper(root, p, k, heap);
        return heap;
    }

private:

    struct Node {
        std::vector<Point<2>> points;
        bool isLeaf;
        std::unique_ptr<Node> nw, ne, sw, se;
        AABB<2> bounds;

        Node(std::vector<Point<2>> &myPoints, int maxSize, AABB<2> bound) {
            if (myPoints.size() <= maxSize) {
                points = myPoints;
                isLeaf = true;
                return;
            }
            bounds = bound;
            float xSplit = (bounds.maxs[0] + bounds.mins[0]) / 2;
            float ySplit = (bounds.maxs[1] + bounds.mins[1]) / 2;
            std::vector<Point<2>> vec_nw, vec_ne, vec_sw, vec_se;

            for (Point<2> point : myPoints) {
                if (point[0] >= xSplit && point[1] >= ySplit) {
                    vec_ne.push_back(point);
                } else if (point[0] < xSplit && point[1] >= ySplit) {
                    vec_nw.push_back(point);
                } else if (point[0] >= xSplit && point[1] < ySplit) {
                    vec_se.push_back(point);
                } else {
                    vec_sw.push_back(point);
                }
            }
            if (vec_ne.size() != 0) {
                AABB<2> temp = bounds;
                temp.mins[0] = xSplit;
                temp.mins[1] = ySplit;
                ne = std::unique_ptr<Node>(new Node(vec_ne, maxSize, temp));
            }
            if (vec_nw.size() != 0) {
                AABB<2> temp = bounds;
                temp.maxs[0] = xSplit;
                temp.mins[1] = ySplit;
                nw = std::unique_ptr<Node>(new Node(vec_nw, maxSize, temp));
            }
            if (vec_se.size() != 0) {
                AABB<2> temp = bounds;
                temp.mins[0] = xSplit;
                temp.maxs[1] = ySplit;
                se = std::unique_ptr<Node>(new Node(vec_se, maxSize, temp));
            }
            if (vec_sw.size() != 0) {
                AABB<2> temp = bounds;
                temp.maxs[0] = xSplit;
                temp.maxs[1] = ySplit;
                sw = std::unique_ptr<Node>(new Node(vec_sw, maxSize, temp));
            }
        }

    };

    std::unique_ptr<Node> root;


    void rangeQueryHelper(const std::unique_ptr<Node> &node, const Point<2> &p, float radius,
                          std::vector<Point<2>> &query) const {
        if (node->isLeaf) {
            for (Point<2> point : node->points) {
                if (distance(p, point) <= radius) {
                    query.push_back(point);
                }
            }
            return;
        }
        if (node->nw != nullptr && distance(node->nw->bounds.closestInBox(p), p) <= radius) {
            rangeQueryHelper(node->nw, p, radius, query);
        }
        if (node->ne != nullptr && distance(node->ne->bounds.closestInBox(p), p) <= radius) {
            rangeQueryHelper(node->ne, p, radius, query);
        }
        if (node->sw != nullptr && distance(node->sw->bounds.closestInBox(p), p) <= radius) {
            rangeQueryHelper(node->sw, p, radius, query);
        }
        if (node->se != nullptr && distance(node->se->bounds.closestInBox(p), p) <= radius) {
            rangeQueryHelper(node->se, p, radius, query);
        }
    }

    void KNNHelper(const std::unique_ptr<Node> &node, const Point<2> &p, int k, std::vector<Point<2>> &heap) const {
        if (node->isLeaf) {
            std::vector<Point<2>> temp = node->points;
            for (int i = 0; i < temp.size(); ++i) {
                if (heap.size() < k) {
                    heap.push_back(temp.at(i));
                    push_heap(heap.begin(), heap.end(), DistanceComparator<2>(p));
                } else if (distance(temp.at(i), p) <= distance(heap.front(), p)) {
                    pop_heap(heap.begin(), heap.end(), DistanceComparator<2>(p));
                    heap.pop_back();
                    heap.push_back(temp.at(i));
                    push_heap(heap.begin(), heap.end(), DistanceComparator<2>(p));
                }
            }
            return;
        }
        if (node->nw != nullptr &&
            (heap.size() < k || distance(node->nw->bounds.closestInBox(p), p) <= distance(heap.front(), p))) {
            KNNHelper(node->nw, p, k, heap);
        }
        if (node->ne != nullptr &&
            (heap.size() < k || distance(node->ne->bounds.closestInBox(p), p) <= distance(heap.front(), p))) {
            KNNHelper(node->ne, p, k, heap);
        }
        if (node->sw != nullptr &&
            (heap.size() < k || distance(node->sw->bounds.closestInBox(p), p) <= distance(heap.front(), p))) {
            KNNHelper(node->sw, p, k, heap);
        }
        if (node->se != nullptr &&
            (heap.size() < k || distance(node->se->bounds.closestInBox(p), p) <= distance(heap.front(), p))) {
            KNNHelper(node->se, p, k, heap);
        }
    }
};
