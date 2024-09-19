package main.selection;
import java.util.*;

import main.Popmember;
import main.Population;
public class TournamentSelection<T> implements Selection<T> {
    
    public Population<T> select(Population<T> population) {
        
        // ensure that the list we pass to candidates has even number of elements
        return null;
    }

    public Popmember<T> runTournament(Population<T> candidates) {
        if (candidates.getIndividuals().size() == 2) {
            if (candidates.getIndividuals().get(0).compareTo(candidates.getIndividuals().get(1)) > 0) {
                return candidates.getIndividuals().get(0);
            } else {
                return candidates.getIndividuals().get(1);
            }
        }
        Population<T> winners = new Population<T>(new ArrayList<Popmember<T>>());
        List<Popmember<T>> winnersList = winners.getIndividuals();
        for (int i = 0; i < candidates.getIndividuals().size(); i++) {
            Popmember<T> first  = candidates.getIndividuals().get(i);
            Popmember<T> second  = candidates.getIndividuals().get(i+1);
            if (first.compareTo(second) > 0) {
                winnersList.add(first);
            } else {
                winnersList.add(second);
            }
        }
        runTournament(new Population<>(winnersList));
        return null;
    }

}