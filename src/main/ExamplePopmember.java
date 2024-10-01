package main;

import java.util.*;

public class ExamplePopmember<T> extends Popmember<T> {

    public ExamplePopmember(FitnessFunc<T> ff) {
        super(ff);
    }

    public ExamplePopmember(T[] genes, FitnessFunc<T> ff) {
        super(genes, ff);
    }

    /* TODO: Implement function */
    @Override
    public Population<T> createInitialPopulation(long size) {
        List<Popmember> list = new ArrayList<>();
        Set<Integer[]> populationSet = new HashSet<>();
        populationSet = randomGenePool(size);
        for (Integer[] genes : populationSet) {
            Popmember<Integer> pm = new ExamplePopmember(genes, super.ff);
            list.add(pm);
        }

        return new Population(list);
    }

    public Set<Integer[]> randomGenePool(long size) {
        Set<Integer[]> set = new HashSet<>();
        Set<Integer> uniqueHashes = new HashSet<>(); 

        while (set.size() < size) {
            Integer[] array = randomGenes();
            int hash = Arrays.deepHashCode(array);
            if (uniqueHashes.add(hash)) {
                set.add(array);
            }
        }

        return set;
    }

    public Integer[] randomGenes() {
        Integer[] array = new Integer[10];

        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * 2);
        }
        return array;
    }

    @Override
    public T[] mutate(T[] unMutated) {
        Random random = new Random();
        int index = random.nextInt(unMutated.length);
        Integer chromosome = (Integer) unMutated[index];
        unMutated[index] = (T) (Integer) (chromosome.equals(1) ? 0 : 1);
        return unMutated;
    }

}
