package main.selection;

import java.util.List;
import java.util.Random;

import main.Popmember;
import main.Population;

public final class RouletteWheel<T> implements Selection<T> {
    @Override
    public List<Popmember<T>> select(Population<T> population, int count, Random random) {
        double[] weights = new double[population.size()];
        for (int index = 0; index < population.size(); index++) {
            weights[index] = Math.max(0.0, population.getIndividuals().get(index).getFitness());
        }
        return WeightedSelection.select(population, count, weights, random);
    }
}
