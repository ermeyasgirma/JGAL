package main.selection;
import java.util.*;

import main.Popmember;
import main.Population;


/**
 * The {@code RouletteWheel} class implements a selection method based on fitness-proportionate selection,
 * also known as roulette wheel selection. In this method, individuals are selected based on their relative
 * fitness values, where those with higher fitness have a greater chance of being selected.
 *
 * @param <T> The type of genes each {@code Popmember} contains.
 */
public class RouletteWheel<T> implements Selection<T> {

    /**
     * Default constructor for the {@code RouletteWheel} class.
     */
    public RouletteWheel() {}

    /**
     * Selects approximately 90% of the individuals from the population using the roulette wheel selection method.
     * Each individual is selected based on its relative fitness, meaning individuals with higher fitness have a
     * higher probability of being selected.
     *
     * @param population The population from which individuals are selected.
     * @return A new {@code Population<T>} containing the selected individuals.
     */
    public Population<T> select(Population<T> population) {
        List<Popmember<T>> luckyPopmembers = new ArrayList<>();
        int popSize = population.getIndividuals().size();

        // Select 90% of the population using the roulette wheel selection method
        for (int i = 0; i < popSize * 0.9; i++) {
            Popmember<T> selected = runRoulette(population);
            luckyPopmembers.add(selected);
        }

        // Return the selected individuals as a new population
        Population<T> selectedPopulation = new Population<>(luckyPopmembers);
        return selectedPopulation;
    }

    /**
     * Executes a single iteration of roulette wheel selection, where one individual is selected based on
     * their relative fitness in the population. Individuals with higher fitness are more likely to be selected.
     *
     * @param population The population from which an individual is selected.
     * @return The {@code Popmember<T>} selected based on the roulette wheel selection process.
     */
    public Popmember<T> runRoulette(Population<T> population) {
        Popmember<T> luckyMember = null;

        // Calculate the total fitness of the population
        double totalFitness = population.getTotalFitness();
        Random random = new Random();

        // Select a random fitness value within the range of the total fitness
        double randomFitness = random.nextDouble() * totalFitness;
        double cumulativeFitness = 0;

        // Iterate through the population, accumulating fitness values, and select the individual
        // whose cumulative fitness exceeds the random fitness value
        for (Popmember<T> member : population.getIndividuals()) {
            cumulativeFitness += member.getFitness();
            if (cumulativeFitness >= randomFitness) {
                luckyMember = member;
                break; // Stop once the individual is selected
            }
        }

        return luckyMember;
    }
}