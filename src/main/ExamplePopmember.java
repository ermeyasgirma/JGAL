package main;


public class ExamplePopmember<T> extends Popmember<T> {

    public ExamplePopmember(Class<T> className, FitnessFunc<T> ff) {
        super(className, ff);
    }

    public ExamplePopmember(T[] genes, FitnessFunc<T> ff) {
        super(genes, ff);
    }

    @Override
    public Popmember<T> createInstance(Object[] newGenes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createInstance'");
    }

    @Override
    public Population<T> createInitialPopulation(long size) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createInitialPopulation'");
    }

}
