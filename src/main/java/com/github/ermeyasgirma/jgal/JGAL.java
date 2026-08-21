package com.github.ermeyasgirma.jgal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
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
    private static final double DEFAULT_ELITE_RATE = 0.10;
    private static final double DEFAULT_CROSSOVER_RATE = 1.0;
    private static final int DEFAULT_TOURNAMENT_SIZE = 2;

    private JGAL() {
    }

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
            System.out.println("Fittest solution is: " + runConfiguredProblem(configuration));
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

        String problemJar = null;
        String problemClass = args[0];
        int index = 1;
        if (index < args.length && !args[index].startsWith("--")) {
            problemJar = problemClass;
            problemClass = args[index++];
        }
        Map<String, String> options = new HashMap<String, String>();
        for (; index < args.length; index++) {
            String option = args[index];
            if ("--quiet".equals(option)) {
                if (options.put(option, "true") != null) {
                    throw new IllegalArgumentException("Duplicate option: " + option);
                }
                continue;
            }
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
        int populationSize = positiveInt(options, "--population-size", "Population size", DEFAULT_POPULATION_SIZE);
        int generations = nonNegativeInt(options, "--generations", DEFAULT_GENERATIONS);
        String selection = options.containsKey("--selection") ? options.get("--selection").toLowerCase() : "rank";
        if (!"rank".equals(selection) && !"roulette".equals(selection) && !"boltzmann".equals(selection)
                && !"tournament".equals(selection)) {
            throw new IllegalArgumentException("Invalid selection method: " + selection);
        }
        Long seed = options.containsKey("--seed") ? Long.valueOf(parseLong(options.get("--seed"), "Seed")) : null;
        double mutationRate = boundedDouble(options, "--mutation-rate", "Mutation rate", DEFAULT_MUTATION_RATE);
        double eliteRate = boundedDouble(options, "--elite-rate", "Elite rate", DEFAULT_ELITE_RATE);
        double crossoverRate = boundedDouble(options, "--crossover-rate", "Crossover rate", DEFAULT_CROSSOVER_RATE);
        int tournamentSize = positiveInt(options, "--tournament-size", "Tournament size", DEFAULT_TOURNAMENT_SIZE);
        Double targetFitness = options.containsKey("--target-fitness")
                ? Double.valueOf(parseDouble(options.get("--target-fitness"), "Target fitness")) : null;
        return new RunConfiguration(problemJar, problemClass, populationSize, generations, selection, seed, mutationRate,
                eliteRate, crossoverRate, tournamentSize, targetFitness, options.containsKey("--quiet"), false);
    }

    public static String usage() {
        return "Usage: java -jar jgal.jar [<problem-jar>] <problem-class> [--population-size <positive integer>] "
                + "[--generations <non-negative integer>] [--selection <rank|roulette|boltzmann|tournament>] "
                + "[--seed <long>] [--mutation-rate <0.0-1.0>] [--elite-rate <0.0-1.0>] "
                + "[--crossover-rate <0.0-1.0>] [--tournament-size <positive integer>] "
                + "[--target-fitness <finite number>] [--quiet]";
    }

    public static <T> RunResult<T> run(GAProblem<T> problem, RunConfiguration configuration) {
        Random random = configuration.getSeed() == null ? new Random() : new Random(configuration.getSeed().longValue());
        Population<T> current = problem.createPrototype().createInitialPopulation(configuration.getPopulationSize(), random);
        if (current.size() != configuration.getPopulationSize()) {
            throw new IllegalArgumentException("Problem returned an unexpected population size");
        }
        List<Double> history = new ArrayList<Double>();
        Popmember<T> best = current.getFittest();
        history.add(Double.valueOf(best.getFitness()));
        printProgress(configuration, 0, best.getFitness());
        if (meetsTarget(best, configuration.getTargetFitness())) {
            return new RunResult<T>(best, history);
        }
        Selection<T> selection = selectionFor(configuration.getSelection(), configuration.getTournamentSize());
        Elitism<T> elitism = new Elitism<T>();
        for (int generation = 0; generation < configuration.getGenerations(); generation++) {
            int eliteCount = Math.max(1, (int) Math.ceil(current.size() * configuration.getEliteRate()));
            int childCount = current.size() - eliteCount;
            List<Popmember<T>> next = new ArrayList<Popmember<T>>(elitism.select(current, eliteCount, random));
            List<Popmember<T>> parents = selectParents(selection, current, childCount * 2,
                    boltzmannTemperature(generation, configuration.getGenerations()), random);
            for (int parent = 0; parent < parents.size(); parent += 2) {
                next.add(Crossover.crossGenes(parents.get(parent), parents.get(parent + 1),
                        configuration.getCrossoverRate(), configuration.getMutationRate(), random));
            }
            current = new Population<T>(next);
            best = current.getFittest();
            history.add(Double.valueOf(best.getFitness()));
            printProgress(configuration, generation + 1, best.getFitness());
            if (meetsTarget(best, configuration.getTargetFitness())) {
                break;
            }
        }
        return new RunResult<T>(best, history);
    }

    private static void printProgress(RunConfiguration configuration, int generation, double fitness) {
        if (!configuration.isQuiet()) {
            System.out.println("Generation " + generation + ": best fitness = " + fitness);
        }
    }

    private static <T> boolean meetsTarget(Popmember<T> member, Double targetFitness) {
        return targetFitness != null && member.getFitness() >= targetFitness.doubleValue();
    }

    private static Popmember<?> runConfiguredProblem(RunConfiguration configuration) {
        if (configuration.getProblemJar() == null) {
            return run(loadProblem(configuration.getProblemClass(), JGAL.class.getClassLoader()), configuration).getFittest();
        }
        File problemJar = new File(configuration.getProblemJar());
        if (!problemJar.isFile() || !problemJar.canRead()) {
            throw new IllegalArgumentException("Problem JAR is not readable: " + configuration.getProblemJar());
        }
        try (URLClassLoader loader = new URLClassLoader(new URL[] { problemJar.toURI().toURL() }, JGAL.class.getClassLoader())) {
            return run(loadProblem(configuration.getProblemClass(), loader), configuration).getFittest();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read problem JAR: " + configuration.getProblemJar());
        }
    }

    private static <T> List<Popmember<T>> selectParents(Selection<T> selection, Population<T> population,
            int count, double temperature, Random random) {
        if (selection instanceof Boltzmann) {
            return ((Boltzmann<T>) selection).select(population, count, temperature, random);
        }
        return selection.select(population, count, random);
    }

    private static <T> Selection<T> selectionFor(String name, int tournamentSize) {
        if ("roulette".equals(name)) {
            return new RouletteWheel<T>();
        }
        if ("boltzmann".equals(name)) {
            return new Boltzmann<T>();
        }
        if ("tournament".equals(name)) {
            return new TournamentSelection<T>(tournamentSize);
        }
        return new Rank<T>();
    }

    private static double boltzmannTemperature(int generation, int generations) {
        return 1.0 - 0.99 * generation / Math.max(1, generations - 1);
    }

    @SuppressWarnings("unchecked")
    private static <T> GAProblem<T> loadProblem(String className, ClassLoader loader) {
        try {
            Class<?> type = Class.forName(className, true, loader);
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
                || "--seed".equals(option) || "--mutation-rate".equals(option) || "--elite-rate".equals(option)
                || "--crossover-rate".equals(option) || "--tournament-size".equals(option)
                || "--target-fitness".equals(option);
    }

    private static int positiveInt(Map<String, String> options, String option, String label, int defaultValue) {
        long parsed = options.containsKey(option) ? parseLong(options.get(option), label) : defaultValue;
        if (parsed > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(label + " must be an integer");
        }
        int value = (int) parsed;
        if (value <= 0) {
            throw new IllegalArgumentException(label + " must be positive");
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

    private static double boundedDouble(Map<String, String> options, String option, String label, double defaultValue) {
        double value = options.containsKey(option) ? parseDouble(options.get(option), label) : defaultValue;
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(label + " must be between 0.0 and 1.0");
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
        private final String problemJar;
        private final String problemClass;
        private final int populationSize;
        private final int generations;
        private final String selection;
        private final Long seed;
        private final double mutationRate;
        private final double eliteRate;
        private final double crossoverRate;
        private final int tournamentSize;
        private final Double targetFitness;
        private final boolean quiet;
        private final boolean helpRequested;

        public RunConfiguration(String problemClass, int populationSize, int generations, String selection, Long seed,
                double mutationRate) {
            this(null, problemClass, populationSize, generations, selection, seed, mutationRate, DEFAULT_ELITE_RATE,
                    DEFAULT_CROSSOVER_RATE, DEFAULT_TOURNAMENT_SIZE, null, false, false);
        }

        public RunConfiguration(String problemClass, int populationSize, int generations, String selection, Long seed,
                double mutationRate, double eliteRate, double crossoverRate, int tournamentSize, Double targetFitness,
                boolean quiet) {
            this(null, problemClass, populationSize, generations, selection, seed, mutationRate, eliteRate,
                    crossoverRate, tournamentSize, targetFitness, quiet, false);
        }

        private RunConfiguration(String problemJar, String problemClass, int populationSize, int generations,
                String selection, Long seed, double mutationRate, double eliteRate, double crossoverRate,
                int tournamentSize, Double targetFitness, boolean quiet, boolean helpRequested) {
            this.problemJar = problemJar;
            this.problemClass = problemClass;
            this.populationSize = populationSize;
            this.generations = generations;
            this.selection = selection;
            this.seed = seed;
            this.mutationRate = mutationRate;
            this.eliteRate = eliteRate;
            this.crossoverRate = crossoverRate;
            this.tournamentSize = tournamentSize;
            this.targetFitness = targetFitness;
            this.quiet = quiet;
            this.helpRequested = helpRequested;
        }

        private static RunConfiguration help() {
            return new RunConfiguration(null, null, 0, 0, null, null, 0.0, 0.0, 0.0, 0, null, true, true);
        }

        public String getProblemJar() { return problemJar; }
        public String getProblemClass() { return problemClass; }
        public int getPopulationSize() { return populationSize; }
        public int getGenerations() { return generations; }
        public String getSelection() { return selection; }
        public Long getSeed() { return seed; }
        public double getMutationRate() { return mutationRate; }
        public double getEliteRate() { return eliteRate; }
        public double getCrossoverRate() { return crossoverRate; }
        public int getTournamentSize() { return tournamentSize; }
        public Double getTargetFitness() { return targetFitness; }
        public boolean isQuiet() { return quiet; }
        public boolean isHelpRequested() { return helpRequested; }
    }
}
