package main.selection;

import main.Population;
public interface Selection<T> {

    public Population<T> select(Population<T> population);

}
