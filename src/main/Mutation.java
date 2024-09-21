package main;
import java.util.*;

public abstract class Mutation<T> {

    public static <T> T[] mutateGenes(T[] genes) {
        return mutateGenes(genes);
    }

    public abstract T[] mutate(T[] genes);
}
