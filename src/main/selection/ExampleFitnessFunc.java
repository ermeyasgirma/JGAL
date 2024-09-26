package main.selection;

import main.FitnessFunc;
import main.Popmember;

public class ExampleFitnessFunc<T> implements FitnessFunc<T> {

    @Override
    public double fitnessScore(Popmember<T> pm) {
        return 0;
    }

}
