package main.selection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import main.FitnessFunc;
import main.Popmember;
import main.Population;
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

    private static List<Selection<Integer>> selections() {
        return Arrays.<Selection<Integer>>asList(new Rank<Integer>(), new RouletteWheel<Integer>(),
                new Boltzmann<Integer>(), new TournamentSelection<Integer>());
    }

    private static Population<Integer> populationOfFitness(double... fitnesses) {
        Popmember<Integer>[] members = new Popmember[fitnesses.length];
        for (int index = 0; index < fitnesses.length; index++) {
            final double fitness = fitnesses[index];
            members[index] = new TestPopmember(new Integer[] { index }, values -> fitness);
        }
        return new Population<Integer>(Arrays.asList(members));
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
