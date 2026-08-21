package com.github.ermeyasgirma.jgal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
                () -> JGAL.parseArguments(new String[] { "com.github.ermeyasgirma.jgal.demo.KnapsackProblem", "other.Problem" }));
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
        JGAL.RunConfiguration configuration = new JGAL.RunConfiguration("com.github.ermeyasgirma.jgal.demo.KnapsackProblem", 24, 8,
                "rank", Long.valueOf(42L), 0.05);

        Popmember<Integer> first = JGAL.run(new KnapsackProblem(), configuration);
        Popmember<Integer> second = JGAL.run(new KnapsackProblem(), configuration);

        assertArrayEquals(first.getGenes(), second.getGenes());
        assertEquals(first.getFitness(), second.getFitness());
    }
}
