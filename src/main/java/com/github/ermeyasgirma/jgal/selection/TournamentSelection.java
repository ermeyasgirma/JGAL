package com.github.ermeyasgirma.jgal.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.ermeyasgirma.jgal.Popmember;
import com.github.ermeyasgirma.jgal.Population;

public final class TournamentSelection<T> implements Selection<T> {
    @Override
    public List<Popmember<T>> select(Population<T> population, int count, Random random) {
        if (count < 0 || population.size() == 0 && count > 0) {
            throw new IllegalArgumentException("Cannot select from an empty population");
        }
        List<Popmember<T>> selected = new ArrayList<Popmember<T>>(count);
        for (int index = 0; index < count; index++) {
            Popmember<T> first = population.getIndividuals().get(random.nextInt(population.size()));
            Popmember<T> second = population.getIndividuals().get(random.nextInt(population.size()));
            selected.add(first.compareTo(second) >= 0 ? first : second);
        }
        return selected;
    }
}
