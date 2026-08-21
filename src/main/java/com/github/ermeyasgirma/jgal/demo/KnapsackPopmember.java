package com.github.ermeyasgirma.jgal.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.ermeyasgirma.jgal.FitnessFunc;
import com.github.ermeyasgirma.jgal.Popmember;
import com.github.ermeyasgirma.jgal.Population;

final class KnapsackPopmember extends Popmember<Integer> {
    KnapsackPopmember(FitnessFunc<Integer> fitnessFunc) {
        super(fitnessFunc);
    }

    KnapsackPopmember(Integer[] genes, FitnessFunc<Integer> fitnessFunc) {
        super(genes, fitnessFunc);
    }

    @Override
    public Population<Integer> createInitialPopulation(int size, Random random) {
        List<Popmember<Integer>> members = new ArrayList<Popmember<Integer>>(size);
        for (int member = 0; member < size; member++) {
            Integer[] genes = new Integer[10];
            for (int gene = 0; gene < genes.length; gene++) {
                genes[gene] = random.nextBoolean() ? 1 : 0;
            }
            members.add(createChild(genes));
        }
        return new Population<Integer>(members);
    }

    @Override
    public Popmember<Integer> createChild(Integer[] genes) {
        return new KnapsackPopmember(genes, ff);
    }

    @Override
    public Integer[] mutate(Integer[] genes, double mutationRate, Random random) {
        for (int index = 0; index < genes.length; index++) {
            if (random.nextDouble() < mutationRate) {
                genes[index] = genes[index] == 1 ? 0 : 1;
            }
        }
        return genes;
    }
}
