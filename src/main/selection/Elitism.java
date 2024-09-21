package main.selection;
import java.util.*;

import main.Popmember;
import main.Population;
public class Elitism<T> implements Selection<T> {

    // This selection method as the name suggests choose the fittest 10% of the population and carries them over to the next generation

    public Population<T> select(Population<T> population) {
        int cohortSize = (int) Math.ceil((double) population.getIndividuals().size()/10);

        // We swap c2 and c1 when comparing to reverse order and get descending values, so highest fitness is prioritized
        PriorityQueue<Popmember<T>> queue = new PriorityQueue<>(1, (c1, c2) -> Double.compare(c2.getFitness(), c1.getFitness()));
        queue.addAll(population.getIndividuals());
        List<Popmember<T>> elite = new ArrayList<>();
        for (int i = 0; i < cohortSize; i++) {
            elite.add(queue.poll());
        }
        Population<T> selectedPopulation = new Population<>(elite);
        return selectedPopulation;
    }
}
