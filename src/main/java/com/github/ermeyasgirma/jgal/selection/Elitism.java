package com.github.ermeyasgirma.jgal.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.github.ermeyasgirma.jgal.Popmember;
import com.github.ermeyasgirma.jgal.Population;

public final class Elitism<T> implements Selection<T> {
    @Override
    public List<Popmember<T>> select(Population<T> population, int count, Random random) {
        if (count < 0 || count > population.size()) {
            throw new IllegalArgumentException("Elite count must be between zero and population size");
        }
        List<Popmember<T>> members = new ArrayList<Popmember<T>>(population.getIndividuals());
        members.sort(Comparator.reverseOrder());
        return new ArrayList<Popmember<T>>(members.subList(0, count));
    }
}
