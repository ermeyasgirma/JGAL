package main.demo;

import main.FitnessFunc;

final class KnapsackFitnessFunc implements FitnessFunc<Integer> {
    private static final int CAPACITY = 12;
    private static final int[] VALUES = { 5, 3, 4, 2, 2, 20, 10, 15, 30, 12 };
    private static final int[] WEIGHTS = { 4, 2, 3, 1, 3, 1, 1, 1, 1, 2 };

    @Override
    public double fitnessScore(Integer[] genes) {
        int weight = 0;
        int value = 0;
        for (int index = 0; index < genes.length; index++) {
            if (genes[index] == 1) {
                weight += WEIGHTS[index];
                value += VALUES[index];
            }
        }
        return weight > CAPACITY ? 0.0 : value * value;
    }
}
