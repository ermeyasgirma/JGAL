package main.selection;

import java.util.List;
import java.util.Random;

import main.Popmember;
import main.Population;

public interface Selection<T> {
    List<Popmember<T>> select(Population<T> population, int count, Random random);
}
