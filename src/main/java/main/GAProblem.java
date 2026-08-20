package main;

public interface GAProblem<T> {
    /**
     * Creates a prototype used to build the initial population and offspring.
     *
     * @return a chromosome implementation for this problem
     */
    Popmember<T> createPrototype();
}
