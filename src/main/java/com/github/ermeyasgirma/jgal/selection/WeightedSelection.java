package com.github.ermeyasgirma.jgal.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.ermeyasgirma.jgal.Popmember;
import com.github.ermeyasgirma.jgal.Population;

final class WeightedSelection {
    private WeightedSelection() {
    }

    static <T> List<Popmember<T>> select(Population<T> population, int count, double[] weights, Random random) {
        if (count < 0 || population.size() == 0 && count > 0) {
            throw new IllegalArgumentException("Cannot select from an empty population");
        }
        double total = 0.0;
        for (double weight : weights) {
            total += Math.max(0.0, weight);
        }
        List<Popmember<T>> selected = new ArrayList<Popmember<T>>(count);
        for (int draw = 0; draw < count; draw++) {
            if (total == 0.0) {
                selected.add(population.getIndividuals().get(random.nextInt(population.size())));
                continue;
            }
            double threshold = random.nextDouble() * total;
            double cumulative = 0.0;
            for (int index = 0; index < population.size(); index++) {
                cumulative += Math.max(0.0, weights[index]);
                if (cumulative >= threshold) {
                    selected.add(population.getIndividuals().get(index));
                    break;
                }
            }
        }
        return selected;
    }
}
