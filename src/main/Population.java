package main;
import java.util.List;

/**
 * The {@code Population} class represents a collection of individuals (represented by {@code Popmember<T>})
 * in a genetic algorithm. It provides functionality for managing and evaluating the population, such as
 * retrieving individuals, calculating total fitness, and finding the fittest individual.
 *
 * @param <T> The type of the genes contained within each {@code Popmember} in the population.
 */
public class Population<T> {

    /**
     * The list of individuals in this population.
     */
    private List<Popmember<T>> members;

    /**
     * The total fitness of the population, calculated as the sum of the fitness values of all individuals.
     * It is lazily evaluated and cached.
     */
    private Double totalFitness;

    private Popmember<T> fittest;

    /**
     * Constructs a {@code Population} with a specified list of members.
     *
     * @param members The list of individuals that make up this population.
     */
    public Population(List<Popmember<T>> members) {
        this.members = members;
    }

    /**
     * Returns the list of individuals in this population.
     *
     * @return The list of {@code Popmember<T>} instances representing the population members.
     */
    public List<Popmember<T>> getIndividuals() {
        return members;
    }

    /**
     * Removes a specific individual from the population and adjusts the total fitness accordingly.
     *
     * @param pm The individual ({@code Popmember<T>}) to be removed from the population.
     */
    public void removeMember(Popmember<T> pm) {
        members.remove(pm);
        totalFitness -= pm.getFitness(); // Update total fitness when a member is removed
    }

    /**
     * Returns the total fitness of the population. If the total fitness has not been calculated yet,
     * it will be calculated as the sum of the fitness values of all individuals in the population.
     *
     * @return The total fitness of the population.
     */
    public double getTotalFitness() {
        if (totalFitness == null) {
            // Lazily calculate total fitness if not already computed
            totalFitness = members.stream().mapToDouble(pm -> pm.getFitness()).sum();
        }
        return totalFitness;
    }

    /**
     * Returns the fittest individual in the population, i.e., the individual with the highest fitness score.
     * If the population is empty, {@code null} is returned.
     *
     * @return The fittest {@code Popmember<T>} in the population, or {@code null} if the population is empty.
     */
    public Popmember<T> getFittest() {
        if (fittest == null) {
            fittest = members.stream()
            .max((c1, c2) -> Double.compare(c1.getFitness(), c2.getFitness()))
            .orElse(null); // Return null if there are no individuals in the population
        }
        return fittest;
    }
}
