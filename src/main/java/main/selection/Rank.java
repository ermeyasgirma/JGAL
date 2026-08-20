package main.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.Popmember;
import main.Population;

public final class Rank<T> implements Selection<T> {
    @Override
    public List<Popmember<T>> select(Population<T> population, int count, Random random) {
        List<Popmember<T>> ranked = new ArrayList<Popmember<T>>(population.getIndividuals());
        ranked.sort(Comparator.reverseOrder());
        Map<Popmember<T>, Integer> weightsByMember = new HashMap<Popmember<T>, Integer>();
        for (int index = 0; index < ranked.size(); index++) {
            weightsByMember.put(ranked.get(index), ranked.size() - index);
        }
        double[] weights = new double[population.size()];
        for (int index = 0; index < population.size(); index++) {
            weights[index] = weightsByMember.get(population.getIndividuals().get(index));
        }
        return WeightedSelection.select(population, count, weights, random);
    }
}
