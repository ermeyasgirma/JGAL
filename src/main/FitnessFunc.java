package main;
public interface FitnessFunc<T> {

    double fitnessScore(Popmember<T> pm);
}