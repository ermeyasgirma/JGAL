package main;

import java.util.*;

public class ExampleFitnessFunc<T> implements FitnessFunc<T> {

    private Map<Integer, Integer[]> knapMap = new HashMap<>();

    public ExampleFitnessFunc() {
        knapMap.put(
            0, new Integer[] {5, 4}
        );

        knapMap.put(
            1, new Integer[] {3, 2}
        );

        knapMap.put(
            2, new Integer[] {4, 3}
        );

        knapMap.put(
            3, new Integer[] {2, 1}
        );

        knapMap.put(
            4, new Integer[] {2, 3}
        );

        knapMap.put(
            5, new Integer[] {20, 1}
        );

        knapMap.put(
            6, new Integer[] {10, 1}
        );

        knapMap.put(
            7, new Integer[] {15, 1}
        );

        knapMap.put(
            8, new Integer[] {30, 1}
        );

        knapMap.put(
            9, new Integer[] {12, 2}
        );
    }

    /* TODO:  Implement function */
    @Override
    public double fitnessScore(T[] genes) {
        int weight = 0;
        int value = 0;
        for (int i = 0; i < genes.length; i++) {
            if ((Integer) genes[i] == 1) {
                weight += knapMap.get(i)[1];
                value += knapMap.get(i)[0];
            }
        }
        if (weight > 12) {return 0;}
        return value*value;
    }

}
