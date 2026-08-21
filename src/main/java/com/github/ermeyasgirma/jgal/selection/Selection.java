package com.github.ermeyasgirma.jgal.selection;

import java.util.List;
import java.util.Random;

import com.github.ermeyasgirma.jgal.Popmember;
import com.github.ermeyasgirma.jgal.Population;

public interface Selection<T> {
    List<Popmember<T>> select(Population<T> population, int count, Random random);
}
