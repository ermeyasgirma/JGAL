package main.selection;
import java.util.*;

import main.Popmember;
import main.Population;

public class Rank<T> implements Selection<T> {

    // sp represents selection pressure: 1.0 (no selection pressure) and 2.0 (high selection pressure)
    private double sp = 1.5;

    private PriorityQueue<Popmember<T>> rankQueue;

    public Population<T> select(Population<T> population) {
        List<Popmember<T>> luckyPopmembers = new ArrayList<>();
        double proportion = 0.5;
        int popSize = population.getIndividuals().size();
        for (int i = 0; i < popSize*proportion; i++) {
            Popmember<T> selected = runRankRoulette(population);
            luckyPopmembers.add(selected);
            population.removeMember(selected);
            rankQueue.remove(selected);
        }
        Population<T> selectedPopulation = new Population<>(luckyPopmembers);
        return selectedPopulation;
    }

    public Popmember<T> runRankRoulette(Population<T> population) {
        Popmember<T> luckyMember = null; 
        int popSize = population.getIndividuals().size();

        /* TODO: Confirm that rank n is higher than rank 1 */
        Map<Popmember<T>, Integer> memberToRank = new HashMap<>();
        int rankCounter = popSize;
        for (Popmember<T> pm : population.getIndividuals()) {
            memberToRank.put(pm, rankCounter);
            rankCounter -= 1;
        }

        double rankFitness;
        Map<Popmember<T>, Double> memberToRankFitness = new HashMap<>();
        for (Popmember<T> member : population.getIndividuals()) {
            int rank = memberToRank.get(member);
            rankFitness = (1/popSize)*(sp - (2*sp - 2)*((rank - 1)/(popSize - 1)));
            memberToRankFitness.put(member, rankFitness);
        }
        
        double randomRF = (new Random()).nextDouble();
        double cumulativeRF = 0;
        for (Popmember<T> pm : population.getIndividuals()) {
            cumulativeRF += memberToRankFitness.get(pm);
            if (cumulativeRF >= randomRF) {
                luckyMember = pm;
            }
        }

        return luckyMember;
    }

    public void rankQueue(Population<T> population) {
        if (rankQueue != null) {
            return;
        }
        rankQueue = new PriorityQueue<>(1, (c1, c2) -> Double.compare(c2.getFitness(), c1.getFitness()));
        rankQueue.addAll(population.getIndividuals());
    }


}
