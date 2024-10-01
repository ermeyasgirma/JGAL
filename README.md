# Genetic Algorithm Library

The Genetic Algorithm Library is a Java-based framework designed to simplify the use of genetic algorithms (GA) for solving optimization and search problems.

### What are Genetic Algorithms?

A genetic algorithm (GA) is a search and optimization technique inspired by the process of natural selection. It works by creating a population of possible solutions (called chromosomes), which evolve over time to find better solutions to a given problem. The process involves selecting the best-performing individuals based on a fitness function, combining them through crossover (like gene recombination), and introducing random mutations to introduce variability. Over multiple generations, the population improves, converging toward an optimal or near-optimal solution. GAs are particularly useful for solving complex problems where traditional methods are inefficient. 

Here's a link to a video that explains a bit more: https://www.youtube.com/watch?v=MacVqujSXWE&t=311s

# Usage

## Build

    $ cd src

    $ javac main/*.java main/selection/*.java

    $ java main/JGAL <selection_method>


The genetic algorithm library uses elitism in each generation in combiniation with another selection method, which you are expected to pass in as a parameter.
The options are the selection methods listed below. Tournament selection is not implemented, so for now use selection methods: rank, roulette, and boltzmann.
If you choose not to pass a selection method as a parameter you will

## Custom Implementation

You will notice that the following classes were left unimplemented

    - ExamplePopmember.java
    - ExampleFitnessFunc.java

 You'll find implementations of  both of these which I used to solve the Knapsack problem. You can implement the 2 classes to solve an appropriate problem of your choice. One example is the traveling salesman problem.

Choosing a good fitness function will determine how successful your genetic algorithm is on finding a near optimal solution, so it is important to consider your implementation carefully.

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


