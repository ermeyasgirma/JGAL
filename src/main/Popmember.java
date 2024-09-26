package main;
import java.util.*;

public abstract class Popmember<T> implements Comparable<Popmember<T>> {

    private T[] genes;
    private Double fitness;
    private Class<T> className;
    private FitnessFunc<T> ff;


    public Popmember(Class<T> className, FitnessFunc<T> ff) {
        this.className = className;
        this.ff = ff;
    }

    public Popmember(T[] genes, FitnessFunc<T> ff) {
        this.ff = ff;
        setGenes(genes);
    }

    public T[] getGenes() {
        return genes;
    }

    public void setGenes(T[] genes) {
        this.genes = genes;
        this.fitness = null;
        getFitness();
    }

    public Double getFitness() {
        if (fitness == null) {
            fitness = calculateFitness(ff);
        }
        return fitness;
    }

    // Requires you to implement FitnessFunc
    public  Double calculateFitness(FitnessFunc<T> fitnessFunc) {
        return fitnessFunc.fitnessScore(genes);
    }

    public abstract Popmember<T> createInstance(T[] newGenes);

    public abstract Population<T> createInitialPopulation(long size);

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this.getClass() != other.getClass()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        final Popmember<T> otherPop = (Popmember<T>) other;
        if (Arrays.equals(this.getGenes(), otherPop.getGenes()) && this.getFitness() == otherPop.getFitness()) {
            return true;
        }

        return false;
    }

    @Override
    public int compareTo(Popmember<T> other) {
        return Double.compare(this.getFitness(), other.getFitness());
    }

    @Override
    public String toString() {
        return "Fittest solution:{" + "genes=" + Arrays.toString(genes) + ", fitness=" + fitness + '}';
    }


}