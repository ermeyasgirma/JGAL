package main;

import java.util.*;

import main.selection.Boltzmann;
import main.selection.Elitism;
import main.selection.Rank;
import main.selection.RouletteWheel;
import main.selection.Selection;

/* TODO:
 * 
 * - Update readme to include changes 
 */

public class JGAL {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void main(String[] args) {

        long generations = 4;

        Selection selectionType = getSelection(args);
        Selection elitism = new Elitism<>();

        FitnessFunc ff = new ExampleFitnessFunc();
        ExamplePopmember<Integer> epm = new ExamplePopmember(ff);

        // Population size must be multiple of 10 && greater than 10
        Population firstGeneration =  epm.createInitialPopulation(100);
        Population currentGeneration = firstGeneration;

        for (int i = 0; i < generations; i++) {
            Population eliteCohort = elitism.select(currentGeneration);
            Population selectedMembers = selectionType.select(currentGeneration);

            Population offspringPopulation = Crossover.crossPopulation(selectedMembers);
            Population newGen = combinePopulations(eliteCohort, offspringPopulation);

            currentGeneration = newGen;
        }

        Popmember solution = currentGeneration.getFittest();

        System.out.println("Fittest solution is: " + solution);

    }

    public static <T> Population<T> combinePopulations(Population<T> p1, Population<T> p2) {
        Population<T> combined = null;
        List<Popmember<T>> combinedList = p1.getIndividuals();
        combinedList.addAll(p2.getIndividuals());
        combined = new Population<T>(combinedList);
        return combined;
    }

    public static <T> Selection<T> getSelection(String[] args) {
        if (args.length == 0) {
            return new Rank<>();
        }
        if (args.length > 1) {
            throw new IllegalArgumentException("Invalid number of arguments: " + args.length);
        }

        String input = args[0];
        
        switch (input.toLowerCase()) {
            case "rank":
                
                return new Rank<>();

            case "roulette":

                return new RouletteWheel<>();

            case "boltzmann":

                return new Boltzmann<>();

            default:

                throw new IllegalArgumentException("Invalid selection method: " + input);
        }
    }
    
}
