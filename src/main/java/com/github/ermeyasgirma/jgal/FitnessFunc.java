package com.github.ermeyasgirma.jgal;

public interface FitnessFunc<T> {
    double fitnessScore(T[] genes);
}
