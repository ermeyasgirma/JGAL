package com.github.ermeyasgirma.jgal.selection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.github.ermeyasgirma.jgal.FitnessFunc;
import com.github.ermeyasgirma.jgal.Popmember;
import com.github.ermeyasgirma.jgal.Population;
import org.junit.jupiter.api.Test;

class SelectionTest {
    @Test
    void everySelectionReturnsRequestedMembersFromSourcePopulation() {
        Population<Integer> population = populationOfFitness(1.0, 2.0, 3.0, 4.0);
        for (Selection<Integer> selection : selections()) {
            List<Popmember<Integer>> selected = selection.select(population, 6, new Random(7));
            assertEquals(6, selected.size());
            assertTrue(population.getIndividuals().containsAll(selected));
        }
    }

    @Test
    void elitismReturnsTheFittestMembers() {
        Population<Integer> population = populationOfFitness(1.0, 4.0, 2.0, 3.0);

        List<Popmember<Integer>> selected = new Elitism<Integer>().select(population, 2, new Random(1));

        assertEquals(4.0, selected.get(0).getFitness(), 0.0);
        assertEquals(3.0, selected.get(1).getFitness(), 0.0);
    }

    @Test
    void rouletteFallsBackToUniformSelectionWhenAllFitnessIsZero() {
        Population<Integer> population = populationOfFitness(0.0, 0.0);
        List<Popmember<Integer>> selected = new RouletteWheel<Integer>().select(population, 8, new Random(3));

        assertEquals(8, selected.size());
        assertTrue(population.getIndividuals().containsAll(selected));
    }

    @Test
    void rankPrefersTheHighestRankWithDeterministicDraws() {
        Population<Integer> population = populationOfFitness(1.0, 2.0, 3.0);
        List<Popmember<Integer>> selected = new Rank<Integer>().select(population, 50, new Random(4));

        long fittestCount = selected.stream().filter(member -> member.getFitness() == 3.0).count();
        long weakestCount = selected.stream().filter(member -> member.getFitness() == 1.0).count();
        assertTrue(fittestCount > weakestCount);
    }

    @Test
    void lowTemperatureBoltzmannFavorsTheFittestMember() {
        Population<Integer> population = populationOfFitness(0.0, 1.0, 5.0);
        List<Popmember<Integer>> selected = new Boltzmann<Integer>().select(population, 30, 0.01, new Random(5));

        assertTrue(selected.stream().allMatch(member -> member.getFitness() == 5.0));
    }

    @Test
    void tournamentUsesTheConfiguredNumberOfSamples() {
        Population<Integer> population = populationOfFitness(1.0, 2.0, 3.0);
        CountingRandom random = new CountingRandom(6);
        List<Popmember<Integer>> selected = new TournamentSelection<Integer>(3).select(population, 4, random);

        assertEquals(4, selected.size());
        assertTrue(population.getIndividuals().containsAll(selected));
        assertEquals(12, random.getNextIntCalls());
    }

    private static List<Selection<Integer>> selections() {
        return Arrays.<Selection<Integer>>asList(new Rank<Integer>(), new RouletteWheel<Integer>(),
                new Boltzmann<Integer>(), new TournamentSelection<Integer>());
    }

    private static Population<Integer> populationOfFitness(double... fitnesses) {
        List<Popmember<Integer>> members = new ArrayList<Popmember<Integer>>(fitnesses.length);
        for (int index = 0; index < fitnesses.length; index++) {
            final double fitness = fitnesses[index];
            members.add(new TestPopmember(new Integer[] { index }, values -> fitness));
        }
        return new Population<Integer>(members);
    }

    private static final class CountingRandom extends Random {
        private int nextIntCalls;

        CountingRandom(long seed) {
            super(seed);
        }

        @Override
        public int nextInt(int bound) {
            nextIntCalls++;
            return super.nextInt(bound);
        }

        int getNextIntCalls() {
            return nextIntCalls;
        }
    }

    private static final class TestPopmember extends Popmember<Integer> {
        TestPopmember(Integer[] genes, FitnessFunc<Integer> fitnessFunc) {
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
