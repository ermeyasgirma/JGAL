package main.selection;

import java.util.*;
import main.Population;
import main.Popmember;

/**
 * The {@code Boltzmann} class implements a selection method based on the Boltzmann selection mechanism,
 * which is inspired by thermodynamic principles. This class selects individuals from a population based on
 * their fitness, with selection probabilities determined using a temperature-like parameter to influence
 * selection pressure.
 *
 * @param <T> The type of genes each {@code Popmember} contains.
 */
public class Boltzmann<T> implements Selection<T> {

    /**
     * A map that associates each individual {@code Popmember} in the population with its selection probability.
     */
    private Map<Popmember<T>, Double> memberToProbability;

    /**
     * Default constructor for the {@code Boltzmann} class.
     */
    public Boltzmann() {}

    /**
     * Selects a subset of individuals from the given population based on the Boltzmann selection mechanism.
     * Approximately 90% of the population is selected for the new population.
     *
     * @param population The population from which individuals are selected.
     * @return A new {@code Population<T>} containing the selected individuals.
     */
    public Population<T> select(Population<T> population) {
        List<Popmember<T>> luckyPopmembers = new ArrayList<>();
        int popSize = population.getIndividuals().size();
        for (int i = 0; i < popSize * 0.9; i++) {
            Popmember<T> selected = runBoltzmann(population);
            if (selected != null) {
                luckyPopmembers.add(selected);
            }
        }
        Population<T> selectedPopulation = new Population<>(luckyPopmembers);
        return selectedPopulation;
    }

    /**
     * Runs the Boltzmann selection algorithm for a single iteration on the given population.
     * This method calculates the selection probability for each individual using the Boltzmann
     * equation, and then randomly selects one individual based on their probabilities.
     *
     * @param population The population of individuals to select from.
     * @return The {@code Popmember<T>} selected based on the Boltzmann probabilities.
     */
    public Popmember<T> runBoltzmann(Population<T> population) {
        Popmember<T> luckyMember = null;

        double avgFitness = population.getTotalFitness() / population.getIndividuals().size();
        double initialTemp = avgFitness;

        double probabilityDenominator = getDenominator(population, initialTemp);

        if (memberToProbability == null) {
            memberToProbability = new HashMap<>();
            for (Popmember<T> pm : population.getIndividuals()) {
                double numerator = Math.exp(pm.getFitness() / initialTemp);
                double probability = numerator / probabilityDenominator;
                memberToProbability.put(pm, probability);
            }
        }

        Random random = new Random();
        double randomProbability = random.nextDouble();
        double cumulativeProbability = 0;
        for (Popmember<T> pm : population.getIndividuals()) {
            cumulativeProbability += memberToProbability.get(pm);
            if (cumulativeProbability >= randomProbability) {
                luckyMember = pm;
                break; // To ensure the loop terminates once the member is selected
            }
        }

        return luckyMember;
    }

    /**
     * Calculates the denominator in the Boltzmann selection probability formula, which is the sum of the
     * exponentiated fitness values of all individuals in the population.
     *
     * @param population The population whose denominator is being calculated.
     * @param initialTemp A temperature-like parameter that influences selection pressure.
     * @return The sum of exponentiated fitness values, used as the denominator for the selection probabilities.
     */
    public double getDenominator(Population<T> population, double initialTemp) {
        // Some check should be in place to ensure the value of sum doesn't exceed Double.MAX_VALUE
        double sum = 0;
        for (Popmember<T> member : population.getIndividuals()) {
            double numerator = Math.exp(member.getFitness() / initialTemp);
            sum += numerator;
        }
        return sum;
    }
}