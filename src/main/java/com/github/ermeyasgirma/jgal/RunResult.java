package com.github.ermeyasgirma.jgal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RunResult<T> {
    private final Popmember<T> fittest;
    private final List<Double> bestFitnessHistory;

    public RunResult(Popmember<T> fittest, List<Double> bestFitnessHistory) {
        this.fittest = fittest;
        this.bestFitnessHistory = Collections.unmodifiableList(new ArrayList<Double>(bestFitnessHistory));
    }

    public Popmember<T> getFittest() { return fittest; }
    public List<Double> getBestFitnessHistory() { return bestFitnessHistory; }
}
