package main.demo;

import main.GAProblem;
import main.Popmember;

public final class KnapsackProblem implements GAProblem<Integer> {
    @Override
    public Popmember<Integer> createPrototype() {
        return new KnapsackPopmember(new KnapsackFitnessFunc());
    }
}
