package com.github.ermeyasgirma.jgal;

import java.util.Arrays;
import java.util.Random;

public abstract class Popmember<T> implements Comparable<Popmember<T>> {
    protected final FitnessFunc<T> ff;
    private T[] genes;
    private Double fitness;

    protected Popmember(FitnessFunc<T> fitnessFunc) {
        ff = fitnessFunc;
    }

    protected Popmember(T[] genes, FitnessFunc<T> fitnessFunc) {
        this(fitnessFunc);
        setGenes(genes);
    }

    public T[] getGenes() {
        return genes.clone();
    }

    public final void setGenes(T[] genes) {
        this.genes = genes.clone();
        fitness = null;
    }

    public final double getFitness() {
        if (fitness == null) {
            fitness = ff.fitnessScore(genes);
        }
        return fitness;
    }

    /**
     * Creates an initial population using the runner's random source.
     */
    public abstract Population<T> createInitialPopulation(int size, Random random);

    /**
     * Creates a new member of the same concrete problem type from the supplied genes.
     */
    public abstract Popmember<T> createChild(T[] genes);

    /**
     * Mutates a child gene array according to the requested per-gene rate.
     */
    public abstract T[] mutate(T[] genes, double mutationRate, Random random);

    @Override
    public int compareTo(Popmember<T> other) {
        return Double.compare(getFitness(), other.getFitness());
    }

    @Override
    public String toString() {
        return "Popmember{" + "genes=" + Arrays.toString(genes) + ", fitness=" + getFitness() + '}';
    }
}
