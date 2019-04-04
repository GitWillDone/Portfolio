cmake_minimum_required(VERSION 3.13)
project(DiscreteEventSimulator)

set(CMAKE_CXX_STANDARD 17)

add_executable(DiscreteEventSimulator main.cpp BankES.h StoreES.h StoreES.cpp)