# JGAL

JGAL is a Java 8+ library and command-line runner for genetic algorithms. It provides a reusable generation loop with elitism, one-point crossover, per-gene mutation, and four parent-selection strategies. Applications supply their own `GAProblem` implementation.

## What Is a Genetic Algorithm?

A genetic algorithm is a stochastic optimization technique that evolves a population of candidate solutions over successive generations. Candidates are evaluated by a fitness function; selection favors stronger candidates, crossover combines parent genes, and mutation introduces variation to explore the search space.

## Prerequisites

- Java Development Kit 8 or later
- Maven 3.6 or later

JGAL has no runtime dependencies. Maven downloads JUnit 5 only to run the test suite.

## Build and Test

Run the test suite:

```sh
mvn test
```

Create the executable JAR:

```sh
mvn package
```

## Run the Knapsack Demo

The bundled demo solves a 0/1 knapsack problem with ten items and a capacity of 12:

```sh
java -jar jgal.jar main.demo.KnapsackProblem
```

Customized execution:

```sh
java -jar jgal.jar main.demo.KnapsackProblem \
  --population-size 100 \
  --generations 100 \
  --selection rank \
  --seed 42 \
  --mutation-rate 0.01
```

Providing `--seed` makes the run reproducible. Omit it to use a new random seed each run.

## CLI Options

| Option | Required | Default | Description |
| --- | --- | --- | --- |
| `<problem-class>` | Yes | None | Fully qualified class implementing `GAProblem` with a public zero-argument constructor. |
| `--population-size <positive integer>` | No | `100` | Number of members in every generation. |
| `--generations <non-negative integer>` | No | `100` | Number of generations to run. |
| `--selection <method>` | No | `rank` | `rank`, `roulette`, `boltzmann`, or `tournament`. |
| `--seed <long>` | No | Random | Seed for reproducible runs. |
| `--mutation-rate <0.0-1.0>` | No | `0.01` | Independent probability that each child gene is mutated. |
| `--help` | No | None | Print command usage. |

Invalid, duplicate, unknown, or incomplete options produce an error and usage text.

## Create a Custom Problem

Implement `GAProblem<T>` and return a `Popmember<T>` prototype. The concrete member evaluates fitness, creates random initial members, creates a new child of the same type, and mutates the child gene array.

```java
package example;

import main.GAProblem;
import main.Popmember;

public final class MyProblem implements GAProblem<Integer> {
    @Override
    public Popmember<Integer> createPrototype() {
        return new MyPopmember();
    }
}
```

`MyPopmember` must implement these methods:

```java
Population<Integer> createInitialPopulation(int size, Random random);
Popmember<Integer> createChild(Integer[] genes);
Integer[] mutate(Integer[] genes, double mutationRate, Random random);
```

Use the supplied `Random` rather than creating a new one so `--seed` remains deterministic. `Crossover` creates a separate child and never changes either selected parent. A generation retains the highest-fitness 10% as elites and fills the remaining slots with children.

## Selection Methods

- **Rank** weights members by fitness rank rather than fitness magnitude.
- **Roulette** weights members by nonnegative fitness; it falls back to uniform sampling when every weight is zero.
- **Boltzmann** uses normalized exponential fitness weights and cools its temperature across generations.
- **Tournament** samples two members and selects the fitter one.

All parent selection is with replacement, so a strong member may be chosen more than once.

## License

JGAL is available under the [MIT License](LICENSE).
