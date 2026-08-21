package com.github.ermeyasgirma.jgal.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.ermeyasgirma.jgal.Popmember;
import com.github.ermeyasgirma.jgal.Population;

public final class TournamentSelection<T> implements Selection<T> {
    private final int tournamentSize;

    public TournamentSelection() {
        this(2);
    }

    public TournamentSelection(int tournamentSize) {
        if (tournamentSize <= 0) {
            throw new IllegalArgumentException("Tournament size must be positive");
        }
        this.tournamentSize = tournamentSize;
    }

    @Override
    public List<Popmember<T>> select(Population<T> population, int count, Random random) {
        if (count < 0 || population.size() == 0 && count > 0) {
            throw new IllegalArgumentException("Cannot select from an empty population");
        }
        List<Popmember<T>> selected = new ArrayList<Popmember<T>>(count);
        for (int index = 0; index < count; index++) {
            Popmember<T> winner = null;
            for (int draw = 0; draw < tournamentSize; draw++) {
                Popmember<T> candidate = population.getIndividuals().get(random.nextInt(population.size()));
                if (winner == null || candidate.compareTo(winner) > 0) {
                    winner = candidate;
                }
            }
            selected.add(winner);
        }
        return selected;
    }
}
