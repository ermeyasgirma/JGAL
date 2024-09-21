package main;

import java.util.*;

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
     * TODO:
     *  Add comments to each file
     *  Commit changes, then try to remove duplicate code across selectMethods
     *  Parse user input for selection method
     *  Implement beginning of genetic algorithm
     */

    private long generations;

    private int mutattionRate;

    public static void main(String[] args) {

        String selection = args[0].toLowerCase();
        switch (selection) {
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
    }
    
}
