package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class Population<T> {
    private final List<Popmember<T>> members;

    public Population(List<Popmember<T>> members) {
        this.members = Collections.unmodifiableList(new ArrayList<Popmember<T>>(members));
    }

    public List<Popmember<T>> getIndividuals() {
        return members;
    }

    public int size() {
        return members.size();
    }

    public double getTotalFitness() {
        double total = 0.0;
        for (Popmember<T> member : members) {
            total += member.getFitness();
        }
        return total;
    }

    public Popmember<T> getFittest() {
        return members.stream().max(Comparator.naturalOrder()).orElse(null);
    }
}
