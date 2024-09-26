package main;

import java.util.*;

import main.selection.Boltzmann;
import main.selection.Elitism;
import main.selection.ExampleFitnessFunc;
import main.selection.Rank;
import main.selection.RouletteWheel;
import main.selection.Selection;

public class JGAL {

    /*
     * TO-DO:
     *  Add comments to each file
     *  Commit changes, then try to remove duplicate code across selectMethods
     *  Parse user input for selection method
     *  Implement beginning of genetic algorithm
     *  Insert TO-DO comments everywhere the user is expected to add their own implementation
     */

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void main(String[] args) {

        long generations = 1000;

        Selection selectionType = getSelection(args[0]);
        Selection elitism = new Elitism<>();

        /* TODO:  */
        Class c = Object.class;
        FitnessFunc ff = new ExampleFitnessFunc();
        ExamplePopmember<Integer> epm = new ExamplePopmember(c, ff);

        Population firstGeneration =  epm.createInitialPopulation(250);
        Population currentGeneration = firstGeneration;
        for (int i = 0; i < generations; i++) {
            Population eliteCohort = elitism.select(currentGeneration);
            Population selectedMembers = selectionType.select(currentGeneration);

            /* TODO:  */
            Mutation mutation = new ExampleMutation();
            Population offspringPopulation = Crossover.crossPopulation(selectedMembers, mutation);
            Population newGen = combinePopulations(eliteCohort, offspringPopulation);

            currentGeneration = newGen;
        }

        Popmember solution = currentGeneration.getFittest();

        System.out.println(solution);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Population combinePopulations(Population p1, Population p2) {
        Population combined = null;
        List<Popmember> combinedList = p1.getIndividuals();
        combinedList.addAll(p2.getIndividuals());
        combined = new Population(combinedList);
        return combined;
    }

    public static <T> Selection<T> getSelection(String input) {
        switch (input.toLowerCase()) {
            case "rank":
                
                return new Rank<>();

            case "roulette wheel":

                return new RouletteWheel<>();

            case "boltzmann":

                return new Boltzmann<>();

            default:

                throw new IllegalArgumentException("Invalid selection method: " + input);
        }
    }
    
}
