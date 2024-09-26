package main.selection;

import main.Population;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import main.Popmember;
import main.Crossover;
import main.ExampleMutation;
import main.Mutation;

public class SteadyState<T> implements Selection<T> {

    @SuppressWarnings("unchecked")
    public Population<T> select(Population<T> population) {
        @SuppressWarnings("rawtypes")
        List<Popmember<T>> elite = new Elitism().select(population).getIndividuals();

        int cohortSize = (int) Math.ceil((double) population.getIndividuals().size()/10);

        // We swap c2 and c1 when comparing to reverse order and get descending values, so highest fitness is prioritized
        PriorityQueue<Popmember<T>> queue = new PriorityQueue<>(1, (c1, c2) -> Double.compare(c1.getFitness(), c2.getFitness()));
        queue.addAll(population.getIndividuals());
        List<Popmember<T>> weakest = new ArrayList<>();
        for (int i = 0; i < cohortSize; i++) {
            weakest.add(queue.poll());
        }

        population.getIndividuals().removeAll(weakest);

        for (int i = 0; i < cohortSize - 2; i+=2) {
            Popmember<T> child = Crossover.crossGenes(elite.get(i), elite.get(i+1));
            population.getIndividuals().add(child);
        }

        return population;
    }

}
