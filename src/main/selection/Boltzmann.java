package main.selection;

import java.util.*;
import main.Population;
import main.Popmember;

public class Boltzmann<T> implements Selection<T> {

    private Map<Popmember<T>, Double> memberToProbability;

    public Boltzmann() {}

    public Population<T> select(Population<T> population) {
        List<Popmember<T>> luckyPopmembers = new ArrayList<>();
        int popSize = population.getIndividuals().size();
        for (int i = 0; i < popSize*0.9; i++) {
            Popmember<T> selected = runBoltzmann(population);
            luckyPopmembers.add(selected);
        }
        Population<T> selectedPopulation = new Population<>(luckyPopmembers);
        return selectedPopulation;
    }

    public Popmember<T> runBoltzmann(Population<T> population) {
        Popmember<T> luckyMember = null;

        double avgFitness = population.getTotalFitness() / population.getIndividuals().size();
        double initialTemp = avgFitness;

        double probabilityDenominator = getDenominator(population, initialTemp);

        if (memberToProbability == null) {
            Map<Popmember<T>, Double> memberToProbability = new HashMap<>();
            for (Popmember<T> pm : population.getIndividuals()) {
                double numerator = Math.exp(pm.getFitness()/initialTemp);
                double probability = numerator / probabilityDenominator;
                memberToProbability.put(pm, probability);
            }
        }

        Random random = new Random();
        double randomProbability = random.nextDouble();
        double cumulativeProbability = 0;
        for (Popmember<T> pm : population.getIndividuals()) {
            cumulativeProbability += memberToProbability.get(pm);
            if (cumulativeProbability >= randomProbability) {
                luckyMember = pm;
            }
        }

        return luckyMember;
    }

    public double getDenominator(Population<T> population, double initialTemp) {
        // Some check should be in place to ensure the value of sum doesn't exceed Double.MAX_VALUE
        double sum = 0;
        for (Popmember<T> member : population.getIndividuals()) {
            double numerator = Math.exp(member.getFitness()/initialTemp);
            sum += numerator;

        }
        return sum;
    }

}
