package com.github.ermeyasgirma.jgal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import com.github.ermeyasgirma.jgal.demo.KnapsackProblem;
import org.junit.jupiter.api.Test;

class JGALTest {
    @Test
    void parsesAllSupportedOptions() {
        JGAL.RunConfiguration configuration = JGAL.parseArguments(new String[] {
                "com.github.ermeyasgirma.jgal.demo.KnapsackProblem", "--population-size", "20", "--generations", "5",
                "--selection", "tournament", "--seed", "9", "--mutation-rate", "0.25" });

        assertEquals("com.github.ermeyasgirma.jgal.demo.KnapsackProblem", configuration.getProblemClass());
        assertEquals(20, configuration.getPopulationSize());
        assertEquals(5, configuration.getGenerations());
        assertEquals("tournament", configuration.getSelection());
        assertEquals(Long.valueOf(9L), configuration.getSeed());
        assertEquals(0.25, configuration.getMutationRate(), 0.0);
    }

    @Test
    void parsesNewRunControls() {
        JGAL.RunConfiguration configuration = JGAL.parseArguments(new String[] {
                "com.github.ermeyasgirma.jgal.demo.KnapsackProblem", "--elite-rate", "0.2",
                "--crossover-rate", "0.5", "--tournament-size", "3", "--target-fitness", "10", "--quiet" });

        assertEquals(0.2, configuration.getEliteRate(), 0.0);
        assertEquals(0.5, configuration.getCrossoverRate(), 0.0);
        assertEquals(3, configuration.getTournamentSize());
        assertEquals(Double.valueOf(10.0), configuration.getTargetFitness());
        assertTrue(configuration.isQuiet());
    }

    @Test
    void parsesAPositionalProblemWithDefaults() {
        JGAL.RunConfiguration configuration = JGAL.parseArguments(
                new String[] { "com.github.ermeyasgirma.jgal.demo.KnapsackProblem" });

        assertEquals("com.github.ermeyasgirma.jgal.demo.KnapsackProblem", configuration.getProblemClass());
        assertEquals(100, configuration.getPopulationSize());
        assertEquals(100, configuration.getGenerations());
        assertEquals("rank", configuration.getSelection());
        assertEquals(null, configuration.getSeed());
        assertEquals(0.01, configuration.getMutationRate(), 0.0);
    }

    @Test
    void parsesOptionsAfterThePositionalProblem() {
        JGAL.RunConfiguration configuration = JGAL.parseArguments(new String[] {
                "com.github.ermeyasgirma.jgal.demo.KnapsackProblem", "--population-size", "20", "--generations", "5",
                "--selection", "tournament", "--seed", "9", "--mutation-rate", "0.25" });

        assertEquals("com.github.ermeyasgirma.jgal.demo.KnapsackProblem", configuration.getProblemClass());
        assertEquals(20, configuration.getPopulationSize());
        assertEquals(5, configuration.getGenerations());
        assertEquals("tournament", configuration.getSelection());
        assertEquals(Long.valueOf(9L), configuration.getSeed());
        assertEquals(0.25, configuration.getMutationRate(), 0.0);
    }

    @Test
    void noArgumentsPrintsUsageAndSucceeds() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            assertEquals(0, JGAL.runMain(new String[0]));
        } finally {
            System.setOut(originalOut);
        }

        assertEquals(JGAL.usage() + System.lineSeparator(),
                new String(output.toByteArray(), StandardCharsets.UTF_8));
    }

    @Test
    void rejectsTheRemovedProblemOptionAndExtraPositionalArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> JGAL.parseArguments(new String[] { "--problem", "com.github.ermeyasgirma.jgal.demo.KnapsackProblem" }));
        assertThrows(IllegalArgumentException.class,
                () -> JGAL.parseArguments(new String[] { "problem.jar", "example.Problem", "other.Problem" }));
    }

    @Test
    void rejectsAnOutOfRangeMutationRate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> JGAL.parseArguments(new String[] { "com.github.ermeyasgirma.jgal.demo.KnapsackProblem", "--mutation-rate", "2" }));

        assertEquals("Mutation rate must be between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    void rejectsPopulationSizesOutsideTheIntegerRange() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> JGAL.parseArguments(new String[] { "com.github.ermeyasgirma.jgal.demo.KnapsackProblem",
                        "--population-size", "2147483648" }));

        assertEquals("Population size must be an integer", exception.getMessage());
    }

    @Test
    void repeatsASeededRunExactly() {
        JGAL.RunConfiguration configuration = new JGAL.RunConfiguration(
                "com.github.ermeyasgirma.jgal.demo.KnapsackProblem", 24, 8, "rank", Long.valueOf(42L),
                0.05, 0.1, 1.0, 2, null, true);

        Popmember<Integer> first = JGAL.run(new KnapsackProblem(), configuration).getFittest();
        Popmember<Integer> second = JGAL.run(new KnapsackProblem(), configuration).getFittest();

        assertArrayEquals(first.getGenes(), second.getGenes());
        assertEquals(first.getFitness(), second.getFitness());
    }

    @Test
    void recordsAnImmutableBestFitnessHistoryStartingAtGenerationZero() {
        JGAL.RunConfiguration configuration = new JGAL.RunConfiguration(
                "unused", 8, 2, "rank", Long.valueOf(3L), 0.0, 0.1, 1.0, 2, null, true);

        RunResult<Integer> result = JGAL.run(new KnapsackProblem(), configuration);

        assertEquals(3, result.getBestFitnessHistory().size());
        assertEquals(result.getFittest().getFitness(),
                result.getBestFitnessHistory().get(result.getBestFitnessHistory().size() - 1), 0.0);
        assertThrows(UnsupportedOperationException.class, () -> result.getBestFitnessHistory().add(0.0));
    }

    @Test
    void stopsBeforeReproductionWhenInitialFitnessMeetsTarget() {
        JGAL.RunConfiguration configuration = new JGAL.RunConfiguration(
                "unused", 2, 10, "rank", Long.valueOf(1L), 0.0, 0.1, 1.0, 2, Double.valueOf(10.0), true);

        RunResult<Integer> result = JGAL.run(new FixedProblem(10.0), configuration);

        assertEquals(1, result.getBestFitnessHistory().size());
        assertEquals(10.0, result.getFittest().getFitness(), 0.0);
    }

    @Test
    void stopsAfterTheFirstCompletedGenerationWhenFitnessReachesTarget() {
        JGAL.RunConfiguration configuration = new JGAL.RunConfiguration(
                "unused", 2, 10, "rank", Long.valueOf(1L), 1.0, 0.1, 1.0, 2, Double.valueOf(1.0), true);

        RunResult<Integer> result = JGAL.run(new ImprovingProblem(), configuration);

        assertEquals(Arrays.asList(0.0, 1.0), result.getBestFitnessHistory());
    }

    @Test
    void parsesAnExternalProblemJarAndClass() {
        JGAL.RunConfiguration configuration = JGAL.parseArguments(new String[] {
                "problem.jar", "example.ExternalProblem", "--quiet" });

        assertEquals("problem.jar", configuration.getProblemJar());
        assertEquals("example.ExternalProblem", configuration.getProblemClass());
        assertTrue(configuration.isQuiet());
    }

    @Test
    void rejectsInvalidNewOptionValues() {
        assertThrows(IllegalArgumentException.class, () -> JGAL.parseArguments(new String[] {
                "problem", "--target-fitness", "NaN" }));
        assertThrows(IllegalArgumentException.class, () -> JGAL.parseArguments(new String[] {
                "problem", "--elite-rate", "2" }));
        assertThrows(IllegalArgumentException.class, () -> JGAL.parseArguments(new String[] {
                "problem", "--crossover-rate", "-1" }));
        assertThrows(IllegalArgumentException.class, () -> JGAL.parseArguments(new String[] {
                "problem", "--tournament-size", "0" }));
    }

    private static final class FixedProblem implements GAProblem<Integer> {
        private final double fitness;

        FixedProblem(double fitness) {
            this.fitness = fitness;
        }

        @Override
        public Popmember<Integer> createPrototype() {
            return new TestPopmember(fitness, false);
        }
    }

    private static final class ImprovingProblem implements GAProblem<Integer> {
        @Override
        public Popmember<Integer> createPrototype() {
            return new TestPopmember(0.0, true);
        }
    }

    private static final class TestPopmember extends Popmember<Integer> {
        private final boolean improving;

        TestPopmember(final double fitness, boolean improving) {
            super(new Integer[] { (int) fitness }, values -> values[0].doubleValue());
            this.improving = improving;
        }

        @Override
        public Population<Integer> createInitialPopulation(int size, Random random) {
            java.util.List<Popmember<Integer>> members = new java.util.ArrayList<Popmember<Integer>>(size);
            for (int index = 0; index < size; index++) {
                members.add(new TestPopmember(getGenes()[0].doubleValue(), improving));
            }
            return new Population<Integer>(members);
        }

        @Override
        public Popmember<Integer> createChild(Integer[] genes) {
            return new TestPopmember(genes[0].doubleValue(), improving);
        }

        @Override
        public Integer[] mutate(Integer[] genes, double mutationRate, Random random) {
            if (improving && random.nextDouble() < mutationRate) {
                genes[0] = Integer.valueOf(genes[0].intValue() + 1);
            }
            return genes;
        }
    }
}
