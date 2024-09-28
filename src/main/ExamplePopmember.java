package main;


public class ExamplePopmember<T> extends Popmember<T> {

    public ExamplePopmember(Class<T> className, FitnessFunc<T> ff) {
        super(className, ff);
    }

    public ExamplePopmember(T[] genes, FitnessFunc<T> ff) {
        super(genes, ff);
    }

    /* TODO: Implement function */
    @Override
    public Population<T> createInitialPopulation(long size) {
        throw new UnsupportedOperationException("Unimplemented method 'createInitialPopulation'");
    }

}
