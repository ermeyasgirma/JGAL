# Genetic Algorithm Library

The Genetic Algorithm Library is a Java-based framework designed to simplify the use of genetic algorithms (GA) for solving optimization and search problems.

### What are Genetic Algorithms?

A genetic algorithm (GA) is a search and optimization technique inspired by the process of natural selection. It works by creating a population of possible solutions (called chromosomes), which evolve over time to find better solutions to a given problem. The process involves selecting the best-performing individuals based on a fitness function, combining them through crossover (like gene recombination), and introducing random mutations to introduce variability. Over multiple generations, the population improves, converging toward an optimal or near-optimal solution. GAs are particularly useful for solving complex problems where traditional methods are inefficient. One example is the travelling salesman problem.

Here's a link to a video that explains a bit more: https://www.youtube.com/watch?v=MacVqujSXWE&t=311s

# Usage

## Build

    $ cd main

    $ javac *.java selection/*.java

    $ java JGAL <selection_method>


The genetic algorithm library uses elitism in each generation in combiniation with another selection method, which you are expected to pass in as a parameter.
The options are the selection methods listed below.

## Custom Implementation

You will notice that the following classes were left unimplemented

    - ExamplePopmember.java
    - ExampleFitnessFunc.java
    - ExampleMutation.java

They each contain TODO comments indicate where implementation is necessary

    public class ExamplePopmember<T> extends Popmember<T> {

        public ExamplePopmember(Class<T> className, FitnessFunc<T> ff) {
            super(className, ff);
        }

        public ExamplePopmember(T[] genes, FitnessFunc<T> ff) {
            super(genes, ff);
        }

        /* TODO: Implement function */
        @Override
        public Population<T> createInitialPopulation(long size) {
            List<Popmember> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Popmember<Integer> pm = new ExamplePopmember(randomArray(), super.ff);
                list.add(pm);
            }

            return new Population(list);
        }

        public Integer[] randomArray() {
            return null;
        }

    }

The above is a mock implementation of ExamplePopmember. You would need to write randomArray() to return random integer arrays here. But you could choose to return String arrays, character arrays or another custom type.


For ExampleFitnessFunc, choosing a good fitness function will determine how successful you're genetic algorithm is on finding a near optimal solution.

For ExampleMutation, you simple need to add some random variation to you Popmember. Below you can find various mutation types, or alternatively create your own.


## Selection Methods

### Roulette Wheel Selection:
- Also known as fitness-proportionate selection.
- Individuals are selected based on their fitness values, with higher fitness individuals having a greater chance of being chosen.
- It’s like a weighted lottery where better solutions get more “tickets.”

### Rank-based Selection
- Individuals are ranked based on their fitness rather than selecting directly from fitness values.
- The probability of selection is based on an individual’s rank, not fitness magnitude, which reduces the impact of highly dominant individuals.

### Boltzmann Selection
- Selection probabilities are adjusted based on the “temperature” of the system, inspired by simulated annealing.
- Initially, individuals with lower fitness still have a decent chance of being selected to maintain diversity (higher temperature).
- As the algorithm progresses, the temperature decreases, making fitness values more critical and narrowing the selection to the best individuals.
- Useful for balancing exploration and exploitation during different stages of the genetic algorithm.

### Elitism
- A small percentage of the best-performing individuals are automatically passed to the next generation without any changes.
- Ensures that the top solutions are always retained, preventing regression in performance.

### Tournament Selection
- A few individuals are randomly selected from the population, and the one with the highest fitness is chosen.
- The process is repeated until the desired number of individuals is selected.
- Can control the selection pressure by adjusting the size of the tournament group.


## Mutation

### Bit Flip Mutation

In this bit flip mutation, we select one or more random bits and flip them. This is used for binary encoded GAs. If not binary encoded you can change the sign of the value eg. 5 -> 5, or if that is not appicable invert, eg. 5 -> 1/5 

### Swap Mutation
In swap mutation, we select two positions on the chromosome at random, and interchange the values. This is common in permutation based encodings.

### Scramble Mutation
Scramble mutation is also popular with permutation representations. In this, from the entire chromosome, a subset of genes is chosen and their values are scrambled or shuffled randomly.

## Requirements

Java version: Java 8 or higher

Dependencies: No external dependencies required


