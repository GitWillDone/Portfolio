package assignment04;

import java.util.ArrayList;

/**
 * Class that times the areAnagrams method using sizes in doubling magnitudes.
 */
public class pivotTiming {

    private static final int ITER_COUNT = 1_000_000_000;
    private static final int SAMPLE_SIZE = 12_000;

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        while (System.nanoTime() - startTime < 1_000_000_000) ;

//		try (FileWriter fw = new FileWriter(new File("thresholdTiming.tsv"))) {

        for (int exp = 2; exp <= SAMPLE_SIZE; exp += 500) {
            //create an ArrayList of integers to time
            ArrayList<Integer> thresholdArray = SortUtil.generateAverageCase(exp);

            // Do the experiment multiple times, and average out the results
            long totalTime = 0;

            for (int iter = 0; iter < exp; iter++) {

                // TIME IT!
                long start = System.nanoTime();
                //method to test
                SortUtil.quicksort(thresholdArray, (lhs, rhs) -> lhs.compareTo(rhs));
                long stop = System.nanoTime();
                totalTime += stop - start;
            }
            double averageTime = (double) totalTime / exp;
            System.out.println(/*exp + "\t" + */averageTime); // print to console
        }
    }
}
