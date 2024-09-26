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

    public static <T> Population<T> crossPopulation(Population<T> population) {
        Population<T> offspringPop = null;
        List<Popmember<T>> offspringLists = new ArrayList<>();

        for (int i = 0; i < population.getIndividuals().size(); i++) {
            // cross p(i) and p(i+1)
            // iclude mutation
        }

        return offspringPop;
    }
}
