package main;


public class ExamplePopmember<T> extends Popmember<T> {

    public ExamplePopmember(Class<T> className) {
        super(className);
    }

    public ExamplePopmember(T[] genes) {
        super(genes);
    }

    public Double calculateFitness() {
        // TODO: Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateFitness");
    }

    @Override
    public Popmember<T> crossOver(Popmember other) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'crossOver'");
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
