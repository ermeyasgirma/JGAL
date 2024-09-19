package main.selection;
import java.util.*;

import main.Popmember;
import main.Population;

public class RouletteWheel<T> implements Selection<T> {

    public Population<T> select(Population<T> population) {
        List<Popmember<T>> luckyPopmembers = new ArrayList<>();
        /* TODO: Figure out what proportion of the population we select */
        double proportion = 0.5;
        int popSize = population.getIndividuals().size();
        for (int i = 0; i < popSize*proportion; i++) {
            Popmember<T> selected = runRoulette(population);
            luckyPopmembers.add(selected);
            population.removeMember(selected);
        }
        Population<T> selectePopulation = new Population<>(luckyPopmembers);
        return selectePopulation;
    }

    public Popmember<T> runRoulette(Population<T> population) {
        Popmember<T> luckyMember = null;
        /* TODO: Figure out if fitness values need to be normalized */
        double totalFitness = population.getTotalFitness();
        Random random = new Random();
        double randomFitness = random.nextDouble() * totalFitness;
        double cumulativeFitness = 0;
        for (Popmember<T> member : population.getIndividuals()) {
            cumulativeFitness += member.getFitness();
            if (cumulativeFitness >= randomFitness) {
                luckyMember = member;
            }
        }
        return luckyMember;
    }

}
