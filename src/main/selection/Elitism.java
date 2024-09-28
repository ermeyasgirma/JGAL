package main.selection;
import java.util.*;

import main.Popmember;
import main.Population;
/**
 * The {@code Elitism} class implements a selection strategy based on elitism, where the top
 * individuals with the highest fitness values are selected to form a new population.
 * This method ensures that the most "elite" individuals are preserved for the next generation.
 *
 * @param <T> The type of genes each {@code Popmember} contains.
 */
public class Elitism<T> implements Selection<T> {

    /**
     * Selects the top 10% of individuals from the population based on their fitness values.
     * The individuals with the highest fitness values are prioritized and selected to form
     * a new population of elites.
     *
     * @param population The population from which individuals are selected.
     * @return A new {@code Population<T>} containing the top individuals based on fitness.
     */
    public Population<T> select(Population<T> population) {
        // Calculate the number of top individuals to select (10% of the population size)
        int cohortSize = (int) Math.ceil((double) population.getIndividuals().size() / 10);

        // Use a priority queue to sort individuals in descending order of fitness
        PriorityQueue<Popmember<T>> queue = new PriorityQueue<>(1, (c1, c2) -> Double.compare(c2.getFitness(), c1.getFitness()));
        queue.addAll(population.getIndividuals());

        // Collect the top 'cohortSize' individuals
        List<Popmember<T>> elite = new ArrayList<>();
        for (int i = 0; i < cohortSize; i++) {
            elite.add(queue.poll());
        }

        // Return a new population consisting of the elite individuals
        Population<T> selectedPopulation = new Population<>(elite);
        return selectedPopulation;
    }
}
