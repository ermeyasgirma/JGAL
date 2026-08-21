package com.github.ermeyasgirma.jgal;

import java.util.Random;

public final class Crossover {
    private Crossover() {
    }

    public static <T> Popmember<T> crossGenes(Popmember<T> first, Popmember<T> second,
            double crossoverRate, double mutationRate, Random random) {
        T[] firstGenes = first.getGenes();
        T[] secondGenes = second.getGenes();
        if (firstGenes.length != secondGenes.length) {
            throw new IllegalArgumentException("Parents must have the same gene count");
        }

        if (random.nextDouble() < crossoverRate) {
            int point = random.nextInt(firstGenes.length + 1);
            for (int index = point; index < firstGenes.length; index++) {
                firstGenes[index] = secondGenes[index];
            }
        }
        return first.createChild(first.mutate(firstGenes, mutationRate, random));
    }
}
