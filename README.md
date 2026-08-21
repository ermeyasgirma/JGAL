# JGAL

JGAL is a Java 8+ library and command-line runner for maximization genetic algorithms. It provides a reusable generation loop with configurable elitism, one-point crossover, per-gene mutation, and four parent-selection strategies. Applications supply a `GAProblem` implementation.

## What Is a Genetic Algorithm?

A genetic algorithm evolves a population of candidate solutions over generations. Candidates receive finite fitness scores; stronger candidates are more likely to be selected, crossover combines parent genes, and mutation introduces variation.

## Prerequisites

- Java Development Kit 8 or later
- Maven 3.6 or later

JGAL has no runtime dependencies. Maven downloads JUnit 5 only for tests.

## Build and Test

Run all unit and packaged-JAR integration tests:

```sh
mvn verify
```

Create the executable JAR:

```sh
mvn package
```

Maven creates the canonical artifact at `target/jgal.jar` and a convenience copy at `jgal.jar` in the repository root. `mvn clean` removes both generated artifacts.

## Run the Knapsack Demo

The bundled demo solves a 0/1 knapsack problem with ten items and a capacity of 12:

```sh
java -jar jgal.jar com.github.ermeyasgirma.jgal.demo.KnapsackProblem
```

Customized execution:

```sh
java -jar jgal.jar com.github.ermeyasgirma.jgal.demo.KnapsackProblem \
  --population-size 100 \
  --generations 100 \
  --selection tournament \
  --tournament-size 3 \
  --elite-rate 0.2 \
  --crossover-rate 0.5 \
  --seed 42 \
  --mutation-rate 0.01
```

By default, JGAL prints generation 0 and each completed generation's best fitness, then prints the final solution. Use `--quiet` to suppress progress lines. Providing `--seed` makes a run reproducible.

## Run an External Problem

Compile a problem JAR against JGAL, then provide the problem JAR before its fully qualified class:

```sh
java -jar jgal.jar my-problem.jar example.MyProblem
```

For advanced classpath setups, run the entry point directly:

```sh
java -cp "jgal.jar:my-problem.jar" com.github.ermeyasgirma.jgal.JGAL example.MyProblem
```

## CLI Options

| Option | Required | Default | Description |
| --- | --- | --- | --- |
| `<problem-class>` | Yes | None | Fully qualified class implementing `GAProblem` with a public zero-argument constructor. |
| `<problem-jar>` | No | None | Readable JAR containing the problem class; when present, it precedes `<problem-class>`. |
| `--population-size <positive integer>` | No | `100` | Number of members in every generation. |
| `--generations <non-negative integer>` | No | `100` | Maximum number of generations to run. |
| `--selection <method>` | No | `rank` | `rank`, `roulette`, `boltzmann`, or `tournament`. |
| `--seed <long>` | No | Random | Seed for reproducible runs. |
| `--mutation-rate <0.0-1.0>` | No | `0.01` | Independent probability that each child gene is mutated. |
| `--elite-rate <0.0-1.0>` | No | `0.10` | Fraction retained as the fittest elites, rounded up to at least one. |
| `--crossover-rate <0.0-1.0>` | No | `1.0` | Probability each child receives one-point crossover. |
| `--tournament-size <positive integer>` | No | `2` | Number of members sampled by tournament selection. |
| `--target-fitness <finite number>` | No | None | Stop when best fitness reaches or exceeds this value. |
| `--quiet` | No | Off | Suppress generation progress lines. |
| `--help` | No | None | Print command usage. |

Invalid, duplicate, unknown, incomplete, or out-of-range options produce an error and usage text. A target fitness is checked against the initial population and after every completed generation.

## Create a Custom Problem

Implement `GAProblem<T>` and return a `Popmember<T>` prototype:

```java
package example;

import com.github.ermeyasgirma.jgal.GAProblem;
import com.github.ermeyasgirma.jgal.Popmember;
import com.github.ermeyasgirma.jgal.Population;

public final class MyProblem implements GAProblem<Integer> {
    @Override
    public Popmember<Integer> createPrototype() {
        return new MyPopmember();
    }
}
```

`MyPopmember` must implement:

```java
Population<Integer> createInitialPopulation(int size, Random random);
Popmember<Integer> createChild(Integer[] genes);
Integer[] mutate(Integer[] genes, double mutationRate, Random random);
```

JGAL maximizes fitness. `fitnessScore` must return a finite number; `NaN` and infinities are rejected. `createInitialPopulation(size, random)` must return exactly `size` members. Use only the supplied `Random` for stochastic decisions so `--seed` stays reproducible. `createChild` must return a new member, and `mutate` may modify only its supplied child gene array.

`JGAL.run` returns a `RunResult<T>` with the final fittest member and an immutable best-fitness history beginning at generation 0.

## Selection Methods

- **Rank** weights members by fitness rank rather than fitness magnitude.
- **Roulette** weights members by nonnegative fitness and falls back to uniform sampling when every weight is zero.
- **Boltzmann** uses normalized exponential fitness weights and cools its temperature across generations.
- **Tournament** samples the configured number of members and selects the fittest one.

All parent selection is with replacement, so a strong member may be selected more than once. Elitism retains the configured top fraction, while crossover is applied independently to each child at the configured rate.

## License

JGAL is available under the [MIT License](LICENSE).
