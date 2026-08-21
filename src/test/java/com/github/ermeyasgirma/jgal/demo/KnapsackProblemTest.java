package com.github.ermeyasgirma.jgal.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Random;

import com.github.ermeyasgirma.jgal.Popmember;
import com.github.ermeyasgirma.jgal.Population;
import org.junit.jupiter.api.Test;

class KnapsackProblemTest {
    @Test
    void createsTheRequestedBinaryPopulation() {
        Popmember<Integer> prototype = new KnapsackProblem().createPrototype();
        Population<Integer> population = prototype.createInitialPopulation(8, new Random(4));

        assertEquals(8, population.size());
        for (Popmember<Integer> member : population.getIndividuals()) {
            assertEquals(10, member.getGenes().length);
            assertTrue(Arrays.stream(member.getGenes()).allMatch(gene -> gene == 0 || gene == 1));
        }
    }

    @Test
    void givesOverweightSolutionsZeroFitness() {
        Popmember<Integer> member = new KnapsackProblem().createPrototype()
                .createChild(new Integer[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });

        assertEquals(0.0, member.getFitness(), 0.0);
    }

    @Test
    void mutationRateOneFlipsEveryGene() {
        Popmember<Integer> prototype = new KnapsackProblem().createPrototype();

        assertEquals(Arrays.asList(1, 0, 1),
                Arrays.asList(prototype.mutate(new Integer[] { 0, 1, 0 }, 1.0, new Random(2))));
    }
}
