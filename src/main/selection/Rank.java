package main.selection;
import java.util.*;

import main.Popmember;
import main.Population;

/**
 * The {@code Rank} class implements a rank-based selection method where individuals in a population
 * are ranked based on their fitness, and selection probabilities are assigned based on these ranks.
 * This class uses a "rank roulette" mechanism to select individuals, with selection pressure controlled
 * by a configurable parameter.
 *
 * @param <T> The type of genes each {@code Popmember} contains.
 */
public class Rank<T> implements Selection<T> {

    /**
     * The selection pressure, controlling how much selection favors individuals with higher ranks.
     * A value of 1.0 implies no selection pressure (equal probabilities), while 2.0 implies high selection
     * pressure, where higher-ranked individuals have a much higher chance of being selected.
     */
    private double sp = 2;

    /**
     * A priority queue that ranks individuals based on their fitness. The queue sorts individuals
     * in descending order of fitness, with the most fit individuals at the top.
     */
    private PriorityQueue<Popmember<T>> rankQueue;

    private Map<Popmember<T>, Integer> memberToRank;;

    /**
     * Default constructor for the {@code Rank} class.
     */
    public Rank() {}

    /**
     * Selects approximately 90% of the individuals from the population using rank-based roulette selection.
     * Individuals are ranked based on fitness, and their selection probability is determined by their rank.
     *
     * @param population The population from which individuals are selected.
     * @return A new {@code Population<T>} containing the selected individuals.
     */
    public Population<T> select(Population<T> population) {
        List<Popmember<T>> luckyPopmembers = new ArrayList<>();
        int popSize = population.getIndividuals().size();
        
        // Rank individuals in the population based on fitness
        rankQueue(population);
        
        // Select 90% of the population based on rank-roulette selection
        for (int i = 0; i < popSize * 0.9; i++) {
            Popmember<T> selected = runRankRoulette(population);
            if (selected != null) {
                luckyPopmembers.add(selected);
            }
            
        }

        // Return the selected individuals as a new population
        Population<T> selectedPopulation = new Population<>(luckyPopmembers);
        return selectedPopulation;
    }

    /**
     * Runs a rank-based roulette selection for one individual. The probability of selection is based
     * on the individual's rank, with higher-ranked individuals (those with better fitness) having a higher
     * chance of being selected.
     *
     * @param population The population from which an individual is selected.
     * @return The {@code Popmember<T>} selected based on rank-based probabilities.
     */
    public Popmember<T> runRankRoulette(Population<T> population) {
        Popmember<T> luckyMember = null;
        int popSize = population.getIndividuals().size();        


        // Calculate the selection probability (rank fitness) for each individual
        double rankFitness;
        Map<Popmember<T>, Double> memberToRankFitness = new HashMap<>();
        for (Popmember<T> member : population.getIndividuals()) {
            int rank = memberToRank.get(member);
            rankFitness = (1 / (double) popSize) * (sp - (2 * sp - 2) * ((rank - 1) / (double) (popSize - 1)));
            memberToRankFitness.put(member, rankFitness);
        }

        // Perform rank-based roulette selection
        double randomRF = (new Random()).nextDouble();
        double cumulativeRF = 0;
        for (Popmember<T> pm : population.getIndividuals()) {
            cumulativeRF += memberToRankFitness.get(pm);
            if (cumulativeRF >= randomRF) {
                luckyMember = pm;
                break; // Stop once the lucky member is selected
            }
        }

        return luckyMember;
    }

    /**
     * Initializes the priority queue that ranks individuals in descending order of fitness.
     * The method ensures that the queue is initialized only once per population.
     *
     * @param population The population whose individuals are ranked by fitness.
     */
    public void rankQueue(Population<T> population) {
        if (rankQueue != null) {
            return;
        }

        // Rank individuals by descending fitness
        rankQueue = new PriorityQueue<>(1, (c1, c2) -> Double.compare(c2.getFitness(), c1.getFitness()));
        rankQueue.addAll(population.getIndividuals());

        int rankCounter = population.getIndividuals().size();
        memberToRank = new HashMap<>();

        while (rankCounter > 0) {
            Popmember<T> pm = rankQueue.poll();
            memberToRank.put(pm, rankCounter);
            rankCounter -= 1;
        }

    }

}