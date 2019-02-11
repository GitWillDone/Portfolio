cmake_minimum_required(VERSION 3.13)
project(shellProg)

set(CMAKE_CXX_STANDARD 14)

add_executable(shellProg main.cpp shelpers.cpp shelpers.h)
target_link_libraries(shellProg readline)
