package main.selection;

import main.FitnessFunc;

public class ExampleFitnessFunc<T> implements FitnessFunc<T> {

    @Override
    public double fitnessScore(T[] genes) {
        throw new UnsupportedOperationException("Unimplemented method 'fitnessScore'");
    }

}
