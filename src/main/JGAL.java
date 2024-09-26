package main;

import java.util.*;

import main.selection.Boltzmann;
import main.selection.Elitism;
import main.selection.Rank;
import main.selection.Selection;

public class JGAL {

    /*
     * 
     * Steps:
     * 1. create initial generation of possible solutions
     * 2. perform tournament selection, crossover between winners and carry over top k candidates to next generation with no crossover
     * 3. now we have our new generation
     * 4. apply step 2 again with our new generation
     * 5. do this 10,000 times 
     * 
     */

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

        String input = args[0].toLowerCase();
        Selection selectionType = getSelection(input);
        Selection elitism = new Elitism<>();

        /* TODO:  */
        Class c = Object.class;
        
        ExamplePopmember<Integer> epm = new ExamplePopmember<>(c);
        Population firstGeneration =  epm.createInitialPopulation(250);
        Population currentGeneration = firstGeneration;
        for (int i = 0; i < generations/2; i++) {
            Population eliteCohort = elitism.select(currentGeneration);
            Population selectedMembers = selectionType.select(currentGeneration);

            Population offspringPopulation = Crossover.crossPopulation(selectedMembers);
            Population newGen = combinePopulations(eliteCohort, offspringPopulation);

            currentGeneration = newGen;
        }

        currentGeneration.getFittest();

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
        Selection<T> selection = null;
        switch (input) {
            case "elitism":
                
                break;
            
            case "rank":
                
                break;

            case "roulette wheel":

                break;

            case "boltzmann":

                break;

            case "tournament":

                break;

            case "steadystate":

                break;
        
            default:
                break;
        }
        return selection;
    }
    
}
