package main;

public abstract class Mutation<T> {

    private double mutationRate = 0.025;

    public static <T> T[] mutateGenes(T[] genes) {
        return mutateGenes(genes);
    }

    public abstract T[] mutate(T[] genes);
}
