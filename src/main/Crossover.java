package main;
import java.util.*;

public class Crossover {

    public static <T> Popmember<T> crossGenes(Popmember<T> parent1, Popmember<T> parent2) {
        Random random = new Random();
        int crossoverPoint = random.nextInt(parent1.getGenes().length);

        T[] offspringGenes = parent1.getGenes().clone();

        for (int i = crossoverPoint; i < parent2.getGenes().length; i++) {
            offspringGenes[i] = parent2.getGenes()[i];
        }

        return parent1.createInstance(offspringGenes); 
    }

    public static <T> Population<T> crossPopulation(Population<T> population, Mutation<T> mutation) {
        Population<T> offspringPop = null;
        List<Popmember<T>> childrenList = new ArrayList<>();

        for (int i = 0; i < population.getIndividuals().size(); i+=2) {
            Popmember<T> parent1 = population.getIndividuals().get(i);
            Popmember<T> parent2 = population.getIndividuals().get(i+1);

            Popmember<T> child = crossGenes(parent1, parent2);
            T[] mutatedGenes = mutation.mutate(child.getGenes());

            child.setGenes(mutatedGenes);
            childrenList.add(child);
        }
        offspringPop = new Population<>(childrenList);
        return offspringPop;
    }
}
