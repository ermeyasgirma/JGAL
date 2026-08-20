package main.selection;

import java.util.List;
import java.util.Random;

import main.Popmember;
import main.Population;

public final class Boltzmann<T> implements Selection<T> {
    @Override
    public List<Popmember<T>> select(Population<T> population, int count, Random random) {
        return select(population, count, 1.0, random);
    }

    public List<Popmember<T>> select(Population<T> population, int count, double temperature, Random random) {
        double safeTemperature = Math.max(temperature, 0.000001);
        double maximumFitness = Double.NEGATIVE_INFINITY;
        for (Popmember<T> member : population.getIndividuals()) {
            maximumFitness = Math.max(maximumFitness, member.getFitness());
        }
        double[] weights = new double[population.size()];
        for (int index = 0; index < population.size(); index++) {
            weights[index] = Math.exp((population.getIndividuals().get(index).getFitness() - maximumFitness)
                    / safeTemperature);
        }
        return WeightedSelection.select(population, count, weights, random);
    }
}
