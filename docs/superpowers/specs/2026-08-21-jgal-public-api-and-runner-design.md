# JGAL Public API and Runner Design

## Goal

Turn JGAL into a more practical reusable runner by adopting a conventional public package, supporting simple external problem JARs, enforcing algorithm contracts, exposing useful runtime controls and results, and documenting and testing the complete workflow.

## Scope

This design includes every improvement identified in the repository review except CI and release automation.

## Public Package Migration

All production and test code moves from `main.*` to `com.github.ermeyasgirma.jgal.*`. The executable entry point becomes `com.github.ermeyasgirma.jgal.JGAL`, and Maven's manifest is updated to reference it.

This is an intentional breaking change. No compatibility wrappers for `main.*` will be retained. The existing project is small, and a clean migration avoids a duplicate API surface.

## Problem Loading

JGAL supports two invocation forms:

```sh
java -jar jgal.jar <problem-class> [options]
java -jar jgal.jar <problem-jar> <problem-class> [options]
```

The one-argument form loads a bundled problem class with JGAL's normal application class loader. The two-argument form treats the first positional argument as an external JAR and loads the supplied problem class from it through a child `URLClassLoader`.

The external JAR must be a readable file. Its problem class must implement the renamed `GAProblem` interface and have a public zero-argument constructor. Invalid JAR paths, unreadable JARs, non-problem classes, and construction errors produce concise error messages and the normal usage text with exit status `1`.

The class loader remains open while the algorithm runs and closes afterward, including when the run fails. The existing classpath form remains usable for advanced scenarios but is not the primary documented path.

## Fitness and Custom-Problem Contract

JGAL maximizes fitness. `Popmember.getFitness()` rejects `NaN`, positive infinity, and negative infinity with an `IllegalArgumentException`, so all selection strategies receive finite values.

The README explicitly documents these requirements for custom `Popmember` implementations:

- Fitness must be finite and is maximized.
- `createInitialPopulation(size, random)` must return exactly `size` members.
- All random choices use the supplied `Random` for seeded reproducibility.
- `createChild` returns a new equivalent member for supplied genes.
- `mutate` may modify only the supplied child gene array and returns the resulting array.

## Runtime Configuration

Existing options retain their values. These optional controls are added:

| Option | Default | Behavior |
| --- | --- | --- |
| `--elite-rate <0.0-1.0>` | `0.10` | Fraction of each nonempty population retained as top-ranked elites, rounded up to at least one. |
| `--crossover-rate <0.0-1.0>` | `1.0` | Probability a selected parent pair receives one-point crossover. |
| `--tournament-size <positive integer>` | `2` | Number of sampled members in tournament selection. |
| `--target-fitness <finite number>` | Disabled | Ends the maximization run when the best fitness is greater than or equal to the target. |
| `--quiet` | Disabled | Suppresses per-generation progress output. |

Argument parsing still rejects unknown, malformed, duplicate, out-of-range, and incomplete options. `--quiet` is a valueless flag and may appear at most once.

Elitism uses the configured rate. Crossover at rate `0.0` still creates a distinct child from one selected parent's cloned genes and applies normal mutation; rate `1.0` retains the current crossover behavior. Intermediate rates make one seeded random draw for each child.

Tournament selection samples the configured number of members with replacement and returns the fittest sampled member.

## Results, Progress, and Stopping

`JGAL.run` returns `RunResult<T>` rather than a raw `Popmember<T>`. It exposes:

- `getFittest()` for the final best member.
- `getBestFitnessHistory()` for an immutable list containing the best fitness at generation 0 and after every completed generation.

The runner tests target fitness at generation 0 before attempting reproduction, then after each completed generation. It stops as soon as the best fitness meets or exceeds the finite target.

Unless `--quiet` is supplied, the CLI writes one line for generation 0 and each completed generation containing its generation number and best fitness. It always writes the final `Fittest solution is:` line on successful completion.

## Testing

Tests migrate with the public package. New deterministic coverage verifies:

- rejection of non-finite fitness;
- all-zero roulette fallback, rank preference, and Boltzmann low-temperature preference;
- tournament behavior with configurable sizes;
- configurable elite rate and crossover rates zero and one;
- target-fitness stopping at generation 0 and after a later generation;
- progress output, quiet output, and immutable `RunResult` history;
- bundled and external problem loading through the packaged root JAR;
- empty invocation and invalid loading paths;
- `mvn package` producing both JAR paths and `mvn clean` removing generated output.

The unchecked generic-array warning in `SelectionTest` is removed by using a typed collection rather than an unchecked generic array.

## Documentation

The README updates package names, build and external-problem examples, every CLI option and default, progress output, and custom-problem contracts. It identifies the single-argument bundled and two-argument external-JAR invocation forms.

## Out of Scope

Continuous integration and release automation are intentionally excluded from this change.
