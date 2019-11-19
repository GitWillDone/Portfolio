#include <iostream>
#include <queue>
#include <vector>
#include <fstream>
#include "Point.hpp"
#include "BucketKNN.hpp"
#include "Generators.hpp"
#include "QuadTree.hpp"
#include "KDTree.hpp"
#include "Stopwatch.hpp"

void quadTreeProcess();

void kdTreeProcess();

void bucketProcess();

int main() {
    bucketProcess();

    //kdTreeProcess();

    //quadTreeProcess();

    return 0;
}

void bucketProcess() {
    std::ofstream bucketFile;
    bucketFile.open("/Users/willdunn/GitWillDone/Data_Analytics_and_Vis/Homeworks/Homework4/Results/bucket.csv");
    bucketFile << "K,Dimension,Number of Points,Time,Distribution" << std::endl;

    auto gen1 = UniformGenerator<1>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<1>(n, 100, gen1);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            BucketKNN<1> bk = BucketKNN<1>(data.training, 5);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                bk.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            bucketFile << k << ",1," << n << "," << avg_time << ",Uniform" << std::endl;
        }
    }
    auto gen2 = GaussianGenerator<1>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<1>(n, 100, gen2);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            BucketKNN<1> bk = BucketKNN<1>(data.training, 5);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                bk.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            bucketFile << k << ",1," << n << "," << avg_time << ",Gaussian" << std::endl;
        }
    }
    auto gen3 = UniformGenerator<2>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<2>(n, 100, gen3);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            BucketKNN<2> bk = BucketKNN<2>(data.training, 5);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                bk.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            bucketFile << k << ",2," << n << "," << avg_time << ",Uniform" << std::endl;
        }
    }
    auto gen4 = GaussianGenerator<2>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<2>(n, 100, gen4);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            BucketKNN<2> bk = BucketKNN<2>(data.training, 5);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                bk.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            bucketFile << k << ",2," << n << "," << avg_time << ",Gaussian" << std::endl;
        }
    }
    auto gen5 = UniformGenerator<3>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<3>(n, 100, gen5);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            BucketKNN<3> bk = BucketKNN<3>(data.training, 5);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                bk.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            bucketFile << k << ",3," << n << "," << avg_time << ",Uniform" << std::endl;
        }
    }
    auto gen6 = GaussianGenerator<3>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<3>(n, 100, gen6);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            BucketKNN<3> bk = BucketKNN<3>(data.training, 5);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                bk.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            bucketFile << k << ",3," << n << "," << avg_time << ",Gaussian" << std::endl;
        }
    }
    auto gen7 = UniformGenerator<4>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<4>(n, 100, gen7);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            BucketKNN<4> bk = BucketKNN<4>(data.training, 5);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                bk.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            bucketFile << k << ",4," << n << "," << avg_time << ",Uniform" << std::endl;
        }
    }
    auto gen8 = GaussianGenerator<4>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<4>(n, 100, gen8);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            BucketKNN<4> bk = BucketKNN<4>(data.training, 5);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                bk.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            bucketFile << k << ",4," << n << "," << avg_time << ",Gaussian" << std::endl;
        }
    }
    bucketFile.close();
}

void kdTreeProcess() {
    std::ofstream kdFile;
    kdFile.open("/Users/willdunn/GitWillDone/Data_Analytics_and_Vis/Homeworks/Homework4/Results/kdtree.csv");
    kdFile << "K,Dimension,Number of Points,Time,Distribution" << std::endl;

    auto gen = UniformGenerator<1>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<1>(n, 100, gen);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            KDTree<1> kt = KDTree<1>(data.training);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                kt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            kdFile << k << ",1," << n << "," << avg_time << ",Uniform" << std::endl;
        }
    }
    auto gen2 = GaussianGenerator<1>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<1>(n, 100, gen2);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            KDTree<1> kt = KDTree<1>(data.training);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                kt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            kdFile << k << ",1," << n << "," << avg_time << ",Gaussian" << std::endl;
        }
    }
    auto gen3 = UniformGenerator<2>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<2>(n, 100, gen3);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            KDTree<2> kt = KDTree<2>(data.training);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                kt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            kdFile << k << ",2," << n << "," << avg_time << ",Uniform" << std::endl;
        }
    }
    auto gen4 = GaussianGenerator<2>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<2>(n, 100, gen4);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            KDTree<2> kt = KDTree<2>(data.training);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                kt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            kdFile << k << ",2," << n << "," << avg_time << ",Gaussian" << std::endl;
        }
    }
    auto gen5 = UniformGenerator<3>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<3>(n, 100, gen5);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            KDTree<3> kt = KDTree<3>(data.training);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                kt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            kdFile << k << ",3," << n << "," << avg_time << ",Uniform" << std::endl;
        }
    }
    auto gen6 = GaussianGenerator<3>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<3>(n, 100, gen6);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            KDTree<3> kt = KDTree<3>(data.training);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                kt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            kdFile << k << ",3," << n << "," << avg_time << ",Gaussian" << std::endl;
        }
    }
    auto gen7 = UniformGenerator<4>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<4>(n, 100, gen7);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            KDTree<4> kt = KDTree<4>(data.training);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                kt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            kdFile << k << ",4," << n << "," << avg_time << ",Uniform" << std::endl;
        }
    }
    auto gen8 = GaussianGenerator<4>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<4>(n, 100, gen8);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            KDTree<4> kt = KDTree<4>(data.training);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                kt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            kdFile << k << ",4," << n << "," << avg_time << ",Gaussian" << std::endl;
        }
    }
    kdFile.close();
}

void quadTreeProcess() {
    std::ofstream quadFile;
    quadFile.open("/Users/willdunn/GitWillDone/Data_Analytics_and_Vis/Homeworks/Homework4/Results/quadtree.csv");
    quadFile << "K,Dimension,Number of Points,Time,Distribution" << std::endl;

    auto gen3 = UniformGenerator<2>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<2>(n, 100, gen3);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            QuadTree qt = QuadTree(data.training, 3);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                qt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            quadFile << k << ",2," << n << "," << avg_time << ",Uniform" << std::endl;
        }
    }
    auto gen4 = GaussianGenerator<2>(-1, 1);
    for (int n = 50; n < 1000000; n *= 2) {
        auto data = getTrialData<2>(n, 100, gen4);
        for (int k = 1; k <= 11; ++k) {
            double avg_time = 0;
            QuadTree qt = QuadTree(data.training, 3);
            for (int i = 0; i < 10; ++i) {
                Stopwatch clock;
                clock.start();
                qt.KNN(data.testing[i], k);
                avg_time += clock.stop();
            }
            avg_time /= 10;
            quadFile << k << ",2," << n << "," << avg_time << ",Gaussian" << std::endl;
        }
    }
    quadFile.close();
}





