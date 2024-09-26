package main;
public interface FitnessFunc<T> {

    double fitnessScore(T[] genes);
}