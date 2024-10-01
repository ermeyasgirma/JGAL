package main;
import java.util.*;

/**
 * The {@code Popmember} class represents an individual in a population, which contains a set of genes
 * and a fitness score that evaluates how well the individual's genes perform based on a provided fitness function.
 * It is an abstract class and is designed to be extended by specific implementations of individuals.
 * 
 * @param <T> The type of the genes contained within the individual.
 */
public abstract class Popmember<T> implements Comparable<Popmember<T>> {

    protected T[] genes;

    protected Double fitness;

    /**
     * The fitness function used to calculate the fitness score of this individual.
     */
    protected FitnessFunc<T> ff;

    /**
     * Constructs a {@code Popmember} with a specified class type and fitness function.
     *
     * @param className The class type of the genes.
     * @param ff The fitness function used to evaluate this individual's fitness.
     */
    public Popmember(FitnessFunc<T> ff) {
        this.ff = ff;
    }

    /**
     * Constructs a {@code Popmember} with a specified set of genes and fitness function.
     *
     * @param genes The array of genes that define this individual.
     * @param ff The fitness function used to evaluate this individual's fitness.
     */
    public Popmember(T[] genes, FitnessFunc<T> ff) {
        this.ff = ff;
        setGenes(genes);
    }

    public T[] getGenes() {
        return genes;
    }

    public void setGenes(T[] genes) {
        this.genes = genes;
        this.fitness = null; // Invalidate fitness since genes have changed
        getFitness(); // Recalculate fitness
    }

    public Double getFitness() {
        if (fitness == null) {
            fitness = calculateFitness(ff);
        }
        return fitness;
    }

    /**
     * Calculates the fitness of this individual based on the provided fitness function.
     *
     * @param fitnessFunc The fitness function used to calculate the fitness score.
     * @return The calculated fitness score.
     */
    public Double calculateFitness(FitnessFunc<T> fitnessFunc) {
        return fitnessFunc.fitnessScore(genes);
    }

    public abstract Population<T> createInitialPopulation(long size);

    public abstract T[] mutate(T[] unMutated);


    @Override
    public int compareTo(Popmember<T> other) {
        return Double.compare(this.getFitness(), other.getFitness());
    }

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
        if (Arrays.equals(this.getGenes(), otherPop.getGenes()) && this.getFitness().equals(otherPop.getFitness())) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "Popmember{" + "genes=" + Arrays.toString(genes) + ", fitness=" + fitness + '}';
    }
}