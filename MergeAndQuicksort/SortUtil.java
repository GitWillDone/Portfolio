package assignment04;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Random;

/**
 * This class utilizes a generic mergesort and/or quicksort depending on the
 * inputs given. It will optimize the sorting, and may even utilize an insertion
 * sort if a threshold of low elements is given into the ArrayList as input.
 *
 * @param <T>
 * @author Qi Liu & William Dunn
 */

public class SortUtil<T> {
    private static int INSERT_SORT_START = 3; // threshold for switching to insertion sort
    private static int PIVOT_TYPE = 2;
    private static Random rand = new Random();

    public SortUtil() {

    }

    /**
     * Driver method that perform a mergesort on the generic ArrayList supplied as
     * input.
     *
     * @param inputArray
     * @param comp
     */
    public static <T> void mergesort(ArrayList<T> inputArray, Comparator<? super T> comp) {
        //check if the array is already sorted
        if (arrayIsSorted(inputArray, inputArray.size(), comp)) return;

        mergesortAlgorithm(inputArray, comp);
    }

    /**
     * The framework of the mergesort, which functions recursively, utilizing the
     * merge function to combine sorted arrays.
     *
     * @param inputArray
     * @param comp
     * @return ArrayList
     */
    public static <T> ArrayList<T> mergesortAlgorithm(ArrayList<T> inputArray, Comparator<? super T> comp) {
        // Creation of temporary arrays so that they can be broken down recursively and
        // put back together
        // as the inputArray.
        ArrayList<T> leftTempArray = new ArrayList<>();
        ArrayList<T> rightTempArray = new ArrayList<>();
        int middle;

        // Drop the base (case)
        if (inputArray.size() == 1) {
            return inputArray;
        } else if (inputArray.size() <= getThreshold()) {
            insertionSort(inputArray, comp, 0, inputArray.size() - 1);
        } else {
            middle = inputArray.size() / 2; // intialized here because it needs recursively divide and find new middle
            // copy the left half of inputArray into the leftTempArray.
            for (int leftIdx = 0; leftIdx < middle; leftIdx++) {
                leftTempArray.add(inputArray.get(leftIdx));
            }

            // copy the right half of inputArray into the rightTempArray.
            for (int rightIdx = middle; rightIdx < inputArray.size(); rightIdx++) {
                rightTempArray.add(inputArray.get(rightIdx));
            }

            // Sort the left and right halves of the arraylist.
            leftTempArray = mergesortAlgorithm(leftTempArray, comp);
            rightTempArray = mergesortAlgorithm(rightTempArray, comp);

            // Merge the results back together.
            merge(leftTempArray, rightTempArray, inputArray, comp);
        }
        return inputArray;
    }

    /**
     * Method that actually does the merging for the mergesort method.
     */
    private static <T> void merge(ArrayList<T> left, ArrayList<T> right, ArrayList<T> inputArray,
                                  Comparator<? super T> comp) {
        int leftIndex = 0;
        int rightIndex = 0;
        int inputIndex = 0;

        // Doing the actual sort while the right and left indices have not exceeded the
        // sizes of temp arrays
        while (leftIndex < left.size() && rightIndex < right.size()) { // left and right indexes are less than size
            if (comp.compare(left.get(leftIndex), right.get(rightIndex)) <= 0) {// left element is less than right
                inputArray.set(inputIndex, left.get(leftIndex)); // sets the inputArray element to the left index
                leftIndex++;
            } else { // if the right is smaller, put it there instead.
                inputArray.set(inputIndex, right.get(rightIndex));
                rightIndex++;
            }
            inputIndex++; // time to check the next index
        }

        // Checks the remaining array
        ArrayList<T> rest = new ArrayList<>();
        rest.add(null);
        int restIndex = 0;
        if (leftIndex >= left.size()) {
            // The left ArrayList has been use up...
            rest = right;
            restIndex = rightIndex;
        } else {
            // The right ArrayList has been used up...
            rest = left;
            restIndex = leftIndex;
        }

        // Copy the rest of whichever ArrayList (left or right) was not used up.
        for (int i = restIndex; i < rest.size(); i++) {
            inputArray.set(inputIndex, rest.get(i));
            inputIndex++;
        }
    }

    /**
     * This generic method sorts the input array using an insertion sort and the
     * input Comparator object. If a null argument is passed, the method should
     * throw a NullPointerException.
     */
    public static <T> void insertionSort(ArrayList<T> inputArray, Comparator<? super T> comp, int low, int high)
            throws NullPointerException {
        int indexPosition;
        T element;

        for (int iter = low; iter <= high; iter++) {
            indexPosition = iter;
            element = inputArray.get(indexPosition);

            // locate position to insert
            while (indexPosition > 0 && indexPosition >= low && indexPosition <= high
                    && comp.compare(inputArray.get(indexPosition - 1), element) > 0) {
                inputArray.set(indexPosition, inputArray.get(indexPosition - 1));
                indexPosition--;
            }
            inputArray.set(indexPosition, element);
        }
    }

    private static <T> int findPivot(int firstIndex, int lastIndex, int type) {
        // Last index
        if (type == 0) {
            return lastIndex;
        }
        // Middle index
        else if (type == 1) {
            return (firstIndex + lastIndex) / 2;
        }
        // Random index
        else {
            return rand.nextInt(lastIndex);
        }
    }

    /**
     * This driver method performs a quicksort on the generic ArrayList given as input.
     *
     * @param inputArray
     * @param comp
     */
    public static <T> void quicksort(ArrayList<T> inputArray, Comparator<? super T> comp) {
        //check if the array is already sorted
        if (arrayIsSorted(inputArray, inputArray.size(), comp)) return;

        quicksortAlgorithm(inputArray, comp, 0, inputArray.size() - 1);
    }

    /**
     * The actual quicksort method, which functions on a three-step divide-and-conquer process.
     * Divide:  partition (rearrange) the array into two subarrays.  Each element should be less than or equal to the pivot, which is less than or equal to each element of the right-half subarray.
     * Conquer:  sorts the two subarrays by recursive calls to quicksortAlgorithm()
     * Combine:  the subarrays are already sorted, so no work is needed to combine them.
     * <p>
     * Description inspired from Introduction to Algorithms 3rd Edition
     *
     * @param inputArray
     * @param comp
     * @param firstIndex
     * @param lastIndex
     * @param <T>
     */
    private static <T> void quicksortAlgorithm(ArrayList<T> inputArray, Comparator<? super T> comp, int firstIndex, int lastIndex) {

        if (firstIndex < lastIndex) {
            int pivot = partition(inputArray, comp, firstIndex, lastIndex); //you can't comment this out because this does actual sorting
            quicksortAlgorithm(inputArray, comp, firstIndex, pivot - 1);
            quicksortAlgorithm(inputArray, comp, pivot + 1, lastIndex);
        }
    }

    /**
     * partition() does an in place sorting of the subarrays using the passed in comparator.
     *
     * @param inputArray
     * @param comp
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    private static <T> int partition(ArrayList<T> inputArray, Comparator<? super T> comp, int left, int right) {
//        if ((right - left) <= 30){
//            insertionSort(inputArray, comp, left, right); //TODO comment out for normal quicksort
//            return rand.nextInt((right - left)) + left;
//        }

        T rightElement = inputArray.get(right); //rightElement is where we'll enter the array TODO uncomment to use original logic
//        T rightElement = inputArray.get(rand.nextInt((right - left)) + left); //random pivot
        int i = left - 1;
        for (int j = left; j <= right - 1; j++) { //iterate while the left (j) is less than the right.  When the cross, they're swapped
            if (comp.compare(inputArray.get(j), rightElement) <= 0) { //the element is less than the right element
                i++;
                Collections.swap(inputArray, i, j); //swap the two elements
            }
        }
        Collections.swap(inputArray, i + 1, right); //swap the two elements

//        for (int x = 0; x < inputArray.size(); x++) {
//
//            if (comp.compare(inputArray.get(x), rightElement) == 0) {
//                return x;
//            }
//        }
        //return right; //return last element as pivot

        return i + 1; //original
    }

    /**
     * This method generates and returns an ArrayList of integers 1 to size in
     * ascending order. The size entered must be greater than 1.
     *
     * @param size
     * @return generatedList
     */
    public static ArrayList<Integer> generateBestCase(int size) {
        ArrayList<Integer> generatedList = new ArrayList<>();

        if (size <= 1) {
            throw new IllegalArgumentException("Enter a size greater than 1.");
        } else if (size == 1) {
            generatedList.add(1);
            return generatedList;
        }

        for (int idx = 1; idx <= size; idx++) {
            generatedList.add(idx);
        }

        return generatedList;
    }

    /**
     * This method generates and returns an ArrayList of integers 1 to size in
     * permuted order (i,e., randomly ordered).
     *
     * @param size
     * @return generatedList
     */
    public static ArrayList<Integer> generateAverageCase(int size) {
        ArrayList<Integer> generatedList = generateBestCase(size);
//        Random rand = new Random();

        //shuffle the ArrayList
//        for(int i = 0; i < generatedList.size(); i++){
//            generatedList.set(i, rand.nextInt(size));
//        }

        Collections.shuffle(generatedList);

        return generatedList;
    }

    /**
     * This method generates and returns an ArrayList of integers 1 to size in
     * descending order.
     *
     * @param size
     * @return generatedList
     */
    public static ArrayList<Integer> generateWorstCase(int size) {
        ArrayList<Integer> generatedList = new ArrayList<>();

        if (size < 1) {
            for (int negIdx = size; negIdx <= 1; negIdx++) {
                generatedList.add(negIdx);
            }
            return generatedList;
        }

        for (int idx = size; idx >= 1; idx--) {
            generatedList.add(idx);
        }

        return generatedList;
    }

    /**
     * Getter for the pivot type.
     *
     * @return PIVOT_TYPE
     */
    public static int getPivot() {
        return PIVOT_TYPE;
    }

    /**
     * Getter for the threshold type.
     *
     * @return INSERT_SORT_START
     */
    public static int getThreshold() {
        return INSERT_SORT_START * 10;
    }

    /**
     * Setter for the pivot type.
     *
     * @return pivotType
     */
    public static void setPivot(int pivotType) {
        PIVOT_TYPE = pivotType;
    }

    /**
     * Setter for the pivot type.  Looks at no insertion sort, threshold and case 0, and then increments each case by 10 until 50 us reached.
     */
    public static void setThreshold(int threshold) {

        switch (threshold) {
            case 0:
                INSERT_SORT_START = 0; //this is the default without utilizing insertion sort
                break;
            case 1:
                INSERT_SORT_START = 10;
                break;
            case 2:
                INSERT_SORT_START = 20;
                break;
            case 3:
                INSERT_SORT_START = 30;
                break;
            case 4:
                INSERT_SORT_START = 40;
                break;
            case 5:
                INSERT_SORT_START = 50;
                break;
        }
    }

    /**
     * Recursive method to check if an array is sorted
     *
     * @param arr
     * @param pairIndex
     * @param comp
     * @return
     */
    public static <T> boolean arrayIsSorted(ArrayList<T> arr, int pairIndex, Comparator<? super T> comp) {
        //array has one or no elements remaining
        if (pairIndex == 1 || pairIndex == 0) return true;

        //unsorted pair is found.  NOTE: equal values are allowed to repeat
        if (comp.compare(arr.get(pairIndex - 1), arr.get(pairIndex - 2)) < 0) return false;

        //last pair was sorted, so recurse
        return arrayIsSorted(arr, pairIndex - 1, comp);
    }

//    public static <T> boolean arrayIsSorted(ArrayList<T> arr, int pairIndex, Comparator<? super T> comp){
//        for (int i = 0; i < arr.size() - 1; i++){
//            if (comp.compare(arr.get(i), arr.get(i+1)) < 0){
//                return false;
//            }
//        }
//        return true;
//
//    }
}