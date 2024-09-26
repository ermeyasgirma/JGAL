package main;

public abstract class Mutation<T> {

    public abstract T[] mutate(T[] genes);
}
