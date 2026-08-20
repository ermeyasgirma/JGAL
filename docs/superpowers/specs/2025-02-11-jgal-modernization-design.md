# JGAL Modernization Design

## Goals

Make JGAL a reusable, Java 8-compatible genetic-algorithm command-line runner. Correct the existing mutation and population-aliasing behavior, finish advertised selection support, provide a maintained knapsack demonstration, and establish a conventional Maven and JUnit workflow.

## Build and Package Layout

Add Maven configuration with Java 8 compiler settings, JUnit 5 for tests, Surefire, and an executable JAR manifest pointing to `main.JGAL`. The library has no runtime dependencies. Move production sources to Maven's `src/main/java` layout and tests to `src/test/java`.

`mvn test` runs the test suite. `mvn package` creates an executable JAR.

## Extension API

Add a generic `GAProblem<T>` interface. A user-supplied implementation exposes a zero-argument constructor and returns a prototype `Popmember<T>` that supplies fitness evaluation, random initial-population creation, and mutation behavior.

`Popmember<T>` remains the abstract representation of a chromosome. Its concrete implementation must be able to create an equivalent member from genes so crossover can produce new children instead of changing its parents. Fitness remains lazily calculated and invalidated when genes change.

The bundled knapsack example becomes `main.demo.KnapsackProblem` with dedicated chromosome and fitness classes. It is a normal `GAProblem` implementation and demonstrates the same public extension path used by consumers.

## Command-Line Runner

`main.JGAL` loads a problem class named by `--problem <fully.qualified.ClassName>` and runs the algorithm. It accepts:

- `--population-size <positive integer>`; default 100.
- `--generations <non-negative integer>`; default 100.
- `--selection <rank|roulette|boltzmann|tournament>`; default `rank`.
- `--seed <long>`; default is a new random seed.
- `--mutation-rate <number from 0.0 through 1.0>`; default 0.01.
- `--help`; prints usage and exits successfully.

Unknown, duplicate, missing, malformed, or out-of-range arguments produce a concise error and usage, then return a nonzero exit status. A class that cannot be loaded or instantiated, lacks an accessible zero-argument constructor, or does not implement `GAProblem` is reported concisely without a default stack trace.

The runner uses one seeded `Random` per invocation. This makes a run deterministic when `--seed` is provided.

## Generation Lifecycle

For every generation, the runner retains the top 10 percent as elites, selects enough parents to replace all remaining members, generates distinct children with one-point crossover, applies per-gene mutation at the configured rate, and creates a new population. No crossover or combination operation mutates an input population or parent chromosome.

Each selection strategy is stateless for a selection call and draws with replacement. Rank, roulette, Boltzmann, and tournament selection each produce the requested parent count. Boltzmann selection receives a temperature derived from generation progress and cools linearly from its initial value to a low positive final value.

## Selection Behavior

- Elitism selects the highest-fitness 10 percent, rounding up to at least one individual for a nonempty population.
- Roulette selection samples proportionally to nonnegative fitness. If all effective weights are zero, it samples uniformly.
- Rank selection samples by rank rather than raw fitness.
- Boltzmann selection normalizes numerically stable exponential weights using the provided temperature.
- Tournament selection chooses the fittest member from randomly sampled tournament candidates.

## Testing

JUnit tests cover CLI parsing and validation, selection output sizes and membership, tournament selection, crossover independence, population cache behavior, deterministic seeded execution, and the knapsack demo. Randomized tests use fixed seeds and assert behavioral invariants rather than incidental random values.

## Documentation

Replace the existing README with Java and Maven prerequisites, test/package/run commands, CLI option reference, a knapsack example, problem-extension guidance, and a brief selection-method description.
