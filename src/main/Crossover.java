package main;
import java.util.*;

/**
 * The {@code Crossover} class provides methods to perform genetic crossover operations
 * on individuals within a population. These methods enable the generation of offspring
 * by recombining genes from two parents, and optionally applying mutations to the offspring.
 */
public class Crossover {

    /**
     * Performs a crossover between two parents by combining their genes. A crossover point
     * is randomly selected, and genes from the first parent are combined with genes from
     * the second parent to produce an offspring. The offspring inherits genes from the first parent
     * up to the crossover point, and from the second parent after that.
     * 
     * @param <T> The type of genes that each {@code Popmember} contains.
     * @param parent1 The first parent for the crossover operation.
     * @param parent2 The second parent for the crossover operation.
     * @return A new {@code Popmember<T>} that represents the child produced by the crossover.
     */

    public static <T> Popmember<T> crossGenes(Popmember<T> parent1, Popmember<T> parent2) {
        Random random = new Random();
        int crossoverPoint = random.nextInt(parent1.getGenes().length);

        T[] offspringGenes = parent1.getGenes().clone();

        for (int i = crossoverPoint; i < parent2.getGenes().length; i++) {
            offspringGenes[i] = parent2.getGenes()[i];
        }

        Popmember<T> child = parent1;
        child.setGenes(offspringGenes);
        return child; 
    }

    /**
     * Performs crossover operations on an entire population of individuals. Each pair of
     * parents in the population undergoes crossover to produce offspring, and mutations
     * are applied to the offspring before adding them to the new population.
     * 
     * @param <T> The type of genes that each {@code Popmember} in the population contains.
     * @param population The population of individuals that will undergo crossover.
     * @param mutation An instance of the {@code Mutation} class that is used to apply mutations
     *                 to the offspring after crossover.
     * @return A new {@code Population<T>} containing the offspring generated by crossover and mutation.
     */

    public static <T> Population<T> crossPopulation(Population<T> population) {
        Population<T> offspringPop = null;
        List<Popmember<T>> childrenList = new ArrayList<>();

        for (int i = 0; i < population.getIndividuals().size(); i+=2) {
            Popmember<T> parent1 = population.getIndividuals().get(i);
            Popmember<T> parent2 = population.getIndividuals().get(i+1);

            Popmember<T> child = crossGenes(parent1, parent2);
            T[] mutatedGenes = parent1.mutate(child.getGenes());

            child.setGenes(mutatedGenes);
            childrenList.add(child);
        }
        offspringPop = new Population<>(childrenList);
        return offspringPop;
    }
}
