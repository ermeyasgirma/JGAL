package com.github.ermeyasgirma.jgal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.ermeyasgirma.jgal.selection.Boltzmann;
import com.github.ermeyasgirma.jgal.selection.Elitism;
import com.github.ermeyasgirma.jgal.selection.Rank;
import com.github.ermeyasgirma.jgal.selection.RouletteWheel;
import com.github.ermeyasgirma.jgal.selection.Selection;
import com.github.ermeyasgirma.jgal.selection.TournamentSelection;

public final class JGAL {
    private static final int DEFAULT_POPULATION_SIZE = 100;
    private static final int DEFAULT_GENERATIONS = 100;
    private static final double DEFAULT_MUTATION_RATE = 0.01;

    private JGAL() {
    }

    /**
     * Runs a configured genetic algorithm from the command line.
     */
    public static void main(String[] args) {
        System.exit(runMain(args));
    }

    public static int runMain(String[] args) {
        try {
            RunConfiguration configuration = parseArguments(args);
            if (configuration.isHelpRequested()) {
                System.out.println(usage());
                return 0;
            }
            Popmember<?> solution = run(loadProblem(configuration.getProblemClass()), configuration);
            System.out.println("Fittest solution is: " + solution);
            return 0;
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            System.err.println(usage());
            return 1;
        }
    }

    public static RunConfiguration parseArguments(String[] args) {
        if (args.length == 0) {
            return RunConfiguration.help();
        }
        if ("--help".equals(args[0])) {
            if (args.length != 1) {
                throw new IllegalArgumentException("--help cannot be combined with other options");
            }
            return RunConfiguration.help();
        }
        if (args[0].startsWith("--")) {
            throw new IllegalArgumentException("Unknown option: " + args[0]);
        }

        String problemClass = args[0];
        Map<String, String> options = new HashMap<String, String>();
        for (int index = 1; index < args.length; index++) {
            String option = args[index];
            if (!isSupportedOption(option)) {
                throw new IllegalArgumentException(option.startsWith("--")
                        ? "Unknown option: " + option : "Unexpected argument: " + option);
            }
            if (index + 1 == args.length || args[index + 1].startsWith("--")) {
                throw new IllegalArgumentException("Missing value for " + option);
            }
            if (options.put(option, args[++index]) != null) {
                throw new IllegalArgumentException("Duplicate option: " + option);
            }
        }
        int populationSize = positiveInt(options, "--population-size", DEFAULT_POPULATION_SIZE);
        int generations = nonNegativeInt(options, "--generations", DEFAULT_GENERATIONS);
        String selection = options.containsKey("--selection") ? options.get("--selection").toLowerCase() : "rank";
        if (!"rank".equals(selection) && !"roulette".equals(selection) && !"boltzmann".equals(selection)
                && !"tournament".equals(selection)) {
            throw new IllegalArgumentException("Invalid selection method: " + selection);
        }
        Long seed = options.containsKey("--seed") ? Long.valueOf(parseLong(options.get("--seed"), "Seed")) : null;
        double mutationRate = options.containsKey("--mutation-rate")
                ? parseDouble(options.get("--mutation-rate"), "Mutation rate") : DEFAULT_MUTATION_RATE;
        if (mutationRate < 0.0 || mutationRate > 1.0) {
            throw new IllegalArgumentException("Mutation rate must be between 0.0 and 1.0");
        }
        return new RunConfiguration(problemClass, populationSize, generations, selection, seed, mutationRate);
    }

    public static String usage() {
        return "Usage: java -jar jgal.jar <problem-class> [--population-size <positive integer>] "
                + "[--generations <non-negative integer>] [--selection <rank|roulette|boltzmann|tournament>] "
                + "[--seed <long>] [--mutation-rate <0.0-1.0>]";
    }

    public static <T> Popmember<T> run(GAProblem<T> problem, RunConfiguration configuration) {
        Random random = configuration.getSeed() == null ? new Random() : new Random(configuration.getSeed().longValue());
        Population<T> current = problem.createPrototype().createInitialPopulation(configuration.getPopulationSize(), random);
        if (current.size() != configuration.getPopulationSize()) {
            throw new IllegalArgumentException("Problem returned an unexpected population size");
        }
        Selection<T> selection = selectionFor(configuration.getSelection());
        Elitism<T> elitism = new Elitism<T>();
        for (int generation = 0; generation < configuration.getGenerations(); generation++) {
            int eliteCount = Math.max(1, (int) Math.ceil(current.size() / 10.0));
            int childCount = current.size() - eliteCount;
            List<Popmember<T>> next = new ArrayList<Popmember<T>>(elitism.select(current, eliteCount, random));
            List<Popmember<T>> parents = selectParents(selection, current, childCount * 2,
                    boltzmannTemperature(generation, configuration.getGenerations()), random);
            for (int index = 0; index < parents.size(); index += 2) {
                next.add(Crossover.crossGenes(parents.get(index), parents.get(index + 1),
                        configuration.getMutationRate(), random));
            }
            current = new Population<T>(next);
        }
        return current.getFittest();
    }

    private static <T> List<Popmember<T>> selectParents(Selection<T> selection, Population<T> population,
            int count, double temperature, Random random) {
        if (selection instanceof Boltzmann) {
            return ((Boltzmann<T>) selection).select(population, count, temperature, random);
        }
        return selection.select(population, count, random);
    }

    private static <T> Selection<T> selectionFor(String name) {
        if ("roulette".equals(name)) {
            return new RouletteWheel<T>();
        }
        if ("boltzmann".equals(name)) {
            return new Boltzmann<T>();
        }
        if ("tournament".equals(name)) {
            return new TournamentSelection<T>();
        }
        return new Rank<T>();
    }

    private static double boltzmannTemperature(int generation, int generations) {
        return 1.0 - 0.99 * generation / Math.max(1, generations - 1);
    }

    @SuppressWarnings("unchecked")
    private static <T> GAProblem<T> loadProblem(String className) {
        try {
            Class<?> type = Class.forName(className);
            if (!GAProblem.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("Problem class must implement GAProblem: " + className);
            }
            Constructor<?> constructor = type.getConstructor();
            return (GAProblem<T>) constructor.newInstance();
        } catch (ClassNotFoundException exception) {
            throw new IllegalArgumentException("Problem class not found: " + className);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException("Problem class needs a public zero-argument constructor: " + className);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException("Could not create problem class: " + className);
        }
    }

    private static boolean isSupportedOption(String option) {
        return "--population-size".equals(option) || "--generations".equals(option) || "--selection".equals(option)
                || "--seed".equals(option) || "--mutation-rate".equals(option);
    }

    private static int positiveInt(Map<String, String> options, String option, int defaultValue) {
        long parsed = options.containsKey(option) ? parseLong(options.get(option), "Population size") : defaultValue;
        if (parsed > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Population size must be an integer");
        }
        int value = (int) parsed;
        if (value <= 0) {
            throw new IllegalArgumentException("Population size must be positive");
        }
        return value;
    }

    private static int nonNegativeInt(Map<String, String> options, String option, int defaultValue) {
        long parsed = options.containsKey(option) ? parseLong(options.get(option), "Generations") : defaultValue;
        if (parsed > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Generations must be an integer");
        }
        int value = (int) parsed;
        if (value < 0) {
            throw new IllegalArgumentException("Generations must not be negative");
        }
        return value;
    }

    private static long parseLong(String value, String label) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(label + " must be an integer");
        }
    }

    private static double parseDouble(String value, String label) {
        try {
            double parsed = Double.parseDouble(value);
            if (!Double.isFinite(parsed)) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(label + " must be a number");
        }
    }

    public static final class RunConfiguration {
        private final String problemClass;
        private final int populationSize;
        private final int generations;
        private final String selection;
        private final Long seed;
        private final double mutationRate;
        private final boolean helpRequested;

        public RunConfiguration(String problemClass, int populationSize, int generations, String selection, Long seed,
                double mutationRate) {
            this(problemClass, populationSize, generations, selection, seed, mutationRate, false);
        }

        private RunConfiguration(String problemClass, int populationSize, int generations, String selection, Long seed,
                double mutationRate, boolean helpRequested) {
            this.problemClass = problemClass;
            this.populationSize = populationSize;
            this.generations = generations;
            this.selection = selection;
            this.seed = seed;
            this.mutationRate = mutationRate;
            this.helpRequested = helpRequested;
        }

        private static RunConfiguration help() {
            return new RunConfiguration(null, 0, 0, null, null, 0.0, true);
        }

        public String getProblemClass() { return problemClass; }
        public int getPopulationSize() { return populationSize; }
        public int getGenerations() { return generations; }
        public String getSelection() { return selection; }
        public Long getSeed() { return seed; }
        public double getMutationRate() { return mutationRate; }
        public boolean isHelpRequested() { return helpRequested; }
    }
}
