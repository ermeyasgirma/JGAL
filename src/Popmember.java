import java.util.*;

public abstract class Popmember<T> implements Comparable<Popmember<T>> {

    private T[] genes;
    private Double fitness;

    public Popmember(T[] genes) {
        this.genes = genes;
        getFitness();
    }

    public T[] getGenes() {
        return genes;
    }

    public Double getFitness() {
        if (fitness == null) {
            fitness = calculateFitness();
        }
        return fitness;
    }

    public abstract Double calculateFitness();

    public abstract Popmember<T> crossOver(Popmember<T> other);

    public abstract Popmember<T> createInstance(T[] newGenes);

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this.getClass() != other.getClass()) {
            return false;
        }

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

}