package com.github.ermeyasgirma.jgal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

class PopulationTest {
    @Test
    void retainsMembersWhenTheSourceListIsChanged() {
        List<Popmember<Integer>> members = new ArrayList<Popmember<Integer>>();
        members.add(member(new Integer[] { 0 }, 1.0));
        members.add(member(new Integer[] { 1 }, 2.0));
        Population<Integer> population = new Population<Integer>(members);

        members.clear();

        assertEquals(2, population.size());
        assertEquals(2.0, population.getFittest().getFitness(), 0.0);
    }

    @Test
    void calculatesTotalFitnessFromMembers() {
        Population<Integer> population = new Population<Integer>(Arrays.asList(
                member(new Integer[] { 0 }, 2.0), member(new Integer[] { 1 }, 3.0)));

        assertEquals(5.0, population.getTotalFitness(), 0.0);
    }

    @Test
    void rejectsNonFiniteFitness() {
        IllegalArgumentException nan = assertThrows(IllegalArgumentException.class,
                () -> member(new Integer[] { 0 }, Double.NaN).getFitness());
        IllegalArgumentException infinity = assertThrows(IllegalArgumentException.class,
                () -> member(new Integer[] { 0 }, Double.POSITIVE_INFINITY).getFitness());

        assertEquals("Fitness must be finite", nan.getMessage());
        assertEquals("Fitness must be finite", infinity.getMessage());
    }

    private static Popmember<Integer> member(Integer[] genes, double fitness) {
        return new TestPopmember(genes, new FitnessFunc<Integer>() {
            @Override
            public double fitnessScore(Integer[] ignored) {
                return fitness;
            }
        });
    }

    private static final class TestPopmember extends Popmember<Integer> {
        private TestPopmember(Integer[] genes, FitnessFunc<Integer> fitnessFunc) {
            super(genes, fitnessFunc);
        }

        @Override
        public Population<Integer> createInitialPopulation(int size, Random random) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Popmember<Integer> createChild(Integer[] genes) {
            return new TestPopmember(genes, ff);
        }

        @Override
        public Integer[] mutate(Integer[] genes, double mutationRate, Random random) {
            return genes;
        }
    }
}
