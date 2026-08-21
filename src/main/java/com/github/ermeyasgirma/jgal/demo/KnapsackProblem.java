package com.github.ermeyasgirma.jgal.demo;

import com.github.ermeyasgirma.jgal.GAProblem;
import com.github.ermeyasgirma.jgal.Popmember;

public final class KnapsackProblem implements GAProblem<Integer> {
    @Override
    public Popmember<Integer> createPrototype() {
        return new KnapsackPopmember(new KnapsackFitnessFunc());
    }
}
