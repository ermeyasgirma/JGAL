# JGAL Modernization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver a Java 8-compatible, Maven-built genetic-algorithm CLI that runs user-provided `GAProblem` implementations safely and includes a tested knapsack demo.

**Architecture:** Retain generic `Population`, `Popmember`, and `FitnessFunc` domain concepts, but move them to Maven conventions and add a `GAProblem` extension boundary. A runner coordinates stateless selection strategies, pure child creation, per-gene mutation, and a validated CLI configuration. The knapsack code lives in a demo package and uses exactly the public extension API.

**Tech Stack:** Java 8, Maven, JUnit Jupiter 5, Maven Surefire Plugin.

## Global Constraints

- Java source and target compatibility is Java 8.
- The production library has no runtime dependencies.
- `GAProblem` is capitalized exactly as shown.
- Use one `java.util.Random` seeded per runner invocation; a supplied seed makes a run deterministic.
- Selection draws with replacement and does not mutate its input population.
- Crossover must create a distinct child and neither crossover nor population construction may mutate source populations or parents.
- Mutation rate is inclusive from `0.0` through `1.0`.
- Commits are small and independently testable.

## File Structure

- `pom.xml`: Java/Maven build, JUnit test dependency, executable main-class manifest.
- `src/main/java/main/GAProblem.java`: public problem extension contract.
- `src/main/java/main/Popmember.java`: abstract chromosome behavior, fitness caching, and child construction contract.
- `src/main/java/main/Population.java`: defensive population collection and aggregate fitness operations.
- `src/main/java/main/FitnessFunc.java`: generic fitness contract.
- `src/main/java/main/Crossover.java`: pure one-point child production and configured mutation.
- `src/main/java/main/selection/*.java`: shared selection contract and elitism, rank, roulette, Boltzmann, and tournament implementations.
- `src/main/java/main/JGAL.java`: option parsing, reflective problem loading, generation coordination, and executable entry point.
- `src/main/java/main/demo/*.java`: knapsack `GAProblem`, chromosome, and fitness demonstration.
- `src/test/java/main/**/*.java`: JUnit coverage for model, reproduction, selection, CLI, and demo behavior.
- `README.md`: prerequisites, build/test/run commands, CLI reference, demo, and extension guidance.

---

### Task 1: Establish Maven Build and Domain Model Contracts

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/main/FitnessFunc.java`
- Create: `src/main/java/main/GAProblem.java`
- Create: `src/main/java/main/Popmember.java`
- Create: `src/main/java/main/Population.java`
- Create: `src/test/java/main/PopulationTest.java`
- Delete: `src/main/FitnessFunc.java`
- Delete: `src/main/Popmember.java`
- Delete: `src/main/Population.java`

**Interfaces:**
- Produces: `public interface FitnessFunc<T> { double fitnessScore(T[] genes); }`
- Produces: `public interface GAProblem<T> { Popmember<T> createPrototype(); }`
- Produces: `public abstract class Popmember<T>` with `getGenes()`, `setGenes(T[])`, `getFitness()`, `createInitialPopulation(int, Random)`, `createChild(T[])`, and `mutate(T[], double, Random)`.
- Produces: `public final class Population<T>` with `Population(List<Popmember<T>>)`, `getIndividuals()`, `getTotalFitness()`, `getFittest()`, and `size()`.

- [ ] **Step 1: Write the failing population behavior tests**

```java
@Test
void copiesTheInputListAndFindsTheFittestMember() {
    List<Popmember<Integer>> members = new ArrayList<Popmember<Integer>>();
    members.add(member(new Integer[] { 0 }, 1.0));
    members.add(member(new Integer[] { 1 }, 2.0));
    Population<Integer> population = new Population<Integer>(members);

    members.clear();

    assertEquals(2, population.size());
    assertEquals(2.0, population.getFittest().getFitness(), 0.0);
}

@Test
void calculatesTotalFitnessFromMembers() {
    Population<Integer> population = new Population<Integer>(Arrays.asList(
        member(new Integer[] { 0 }, 2.0), member(new Integer[] { 1 }, 3.0)));

    assertEquals(5.0, population.getTotalFitness(), 0.0);
}
```

- [ ] **Step 2: Run the new test to confirm Maven is not configured**

Run: `mvn test -Dtest=PopulationTest`

Expected: FAIL because `pom.xml` does not exist.

- [ ] **Step 3: Add the Maven configuration and domain contracts**

```xml
<properties>
  <maven.compiler.source>1.8</maven.compiler.source>
  <maven.compiler.target>1.8</maven.compiler.target>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <junit.version>5.10.2</junit.version>
</properties>
```

```java
public interface GAProblem<T> {
    Popmember<T> createPrototype();
}
```

Implement `Population` with a copied, unmodifiable list. Implement `Popmember` so `setGenes` clones its input and invalidates cached fitness. Define `createChild` as the concrete-type factory used by crossover. Define mutation as `mutate(T[] genes, double mutationRate, Random random)` so each implementation can mutate a fresh gene array based on the requested rate.

- [ ] **Step 4: Run the population tests**

Run: `mvn test -Dtest=PopulationTest`

Expected: PASS.

- [ ] **Step 5: Commit the build and model foundation**

```bash
git add pom.xml src/main/java/main/FitnessFunc.java src/main/java/main/GAProblem.java src/main/java/main/Popmember.java src/main/java/main/Population.java src/test/java/main/PopulationTest.java src/main/FitnessFunc.java src/main/Popmember.java src/main/Population.java
```

### Task 2: Implement Pure Crossover and Selection Strategies

**Files:**
- Create: `src/main/java/main/Crossover.java`
- Create: `src/main/java/main/selection/Selection.java`
- Create: `src/main/java/main/selection/Elitism.java`
- Create: `src/main/java/main/selection/Rank.java`
- Create: `src/main/java/main/selection/RouletteWheel.java`
- Create: `src/main/java/main/selection/Boltzmann.java`
- Create: `src/main/java/main/selection/TournamentSelection.java`
- Create: `src/test/java/main/CrossoverTest.java`
- Create: `src/test/java/main/selection/SelectionTest.java`
- Delete: `src/main/Crossover.java`
- Delete: `src/main/selection/Selection.java`
- Delete: `src/main/selection/Elitism.java`
- Delete: `src/main/selection/Rank.java`
- Delete: `src/main/selection/RouletteWheel.java`
- Delete: `src/main/selection/Boltzmann.java`
- Delete: `src/main/selection/TournamentSelection.java`
- Delete: `src/main/selection/SteadyState.java`

**Interfaces:**
- Consumes: `Population<T>`, `Popmember<T>`, and `Popmember.mutate(T[], double, Random)` from Task 1.
- Produces: `public static <T> Popmember<T> Crossover.crossGenes(Popmember<T>, Popmember<T>, double, Random)`.
- Produces: `public interface Selection<T> { List<Popmember<T>> select(Population<T> population, int count, Random random); }`.
- Produces: `Boltzmann.select(Population<T> population, int count, double temperature, Random random)` and a `Selection` overload using temperature `1.0`.

- [ ] **Step 1: Write failing tests for offspring independence and selection invariants**

```java
@Test
void crossoverReturnsANewChildWithoutChangingEitherParent() {
    Popmember<Integer> first = member(new Integer[] { 0, 0, 0, 0 });
    Popmember<Integer> second = member(new Integer[] { 1, 1, 1, 1 });

    Popmember<Integer> child = Crossover.crossGenes(first, second, 0.0, new Random(3));

    assertNotSame(first, child);
    assertArrayEquals(new Integer[] { 0, 0, 0, 0 }, first.getGenes());
    assertArrayEquals(new Integer[] { 1, 1, 1, 1 }, second.getGenes());
}

@Test
void everySelectionReturnsRequestedMembersFromSourcePopulation() {
    Population<Integer> population = populationOfFitness(1.0, 2.0, 3.0, 4.0);
    for (Selection<Integer> selection : selections()) {
        List<Popmember<Integer>> selected = selection.select(population, 6, new Random(7));
        assertEquals(6, selected.size());
        assertTrue(population.getIndividuals().containsAll(selected));
    }
}
```

- [ ] **Step 2: Run the reproduction and selection tests to verify failure**

Run: `mvn test -Dtest=CrossoverTest,SelectionTest`

Expected: FAIL because crossover and the new selection APIs do not exist.

- [ ] **Step 3: Implement pure reproduction and stateless selection**

```java
public static <T> Popmember<T> crossGenes(
        Popmember<T> first, Popmember<T> second, double mutationRate, Random random) {
    T[] genes = first.getGenes();
    int point = random.nextInt(genes.length + 1);
    for (int index = point; index < genes.length; index++) {
        genes[index] = second.getGenes()[index];
    }
    return first.createChild(first.mutate(genes, mutationRate, random));
}
```

Implement each `Selection` as a local weighted draw with replacement. Elitism sorts a copied list descending and returns `ceil(size / 10.0)` members. Roulette uses nonnegative fitness weights and uniform fallback. Rank assigns descending ranks. Boltzmann computes stable weights with `exp((fitness - maximumFitness) / temperature)` and clamps temperature to a small positive value. Tournament draws two random candidates and returns the fitter one for each requested selection.

- [ ] **Step 4: Run selection and crossover tests**

Run: `mvn test -Dtest=CrossoverTest,SelectionTest`

Expected: PASS.

- [ ] **Step 5: Commit the reproduction and selection layer**

```bash
git add src/main/java/main/Crossover.java src/main/java/main/selection src/test/java/main/CrossoverTest.java src/test/java/main/selection/SelectionTest.java src/main/Crossover.java src/main/selection
```

### Task 3: Add Knapsack Demo Through the Public Extension API

**Files:**
- Create: `src/main/java/main/demo/KnapsackFitnessFunc.java`
- Create: `src/main/java/main/demo/KnapsackPopmember.java`
- Create: `src/main/java/main/demo/KnapsackProblem.java`
- Create: `src/test/java/main/demo/KnapsackProblemTest.java`
- Delete: `src/main/ExampleFitnessFunc.java`
- Delete: `src/main/ExamplePopmember.java`

**Interfaces:**
- Consumes: `GAProblem<Integer>`, `Popmember<Integer>`, `FitnessFunc<Integer>`, and `Population<Integer>` from Task 1.
- Produces: `public final class KnapsackProblem implements GAProblem<Integer>` with a public zero-argument constructor.
- Produces: `KnapsackPopmember.createInitialPopulation(int, Random)` and bit-flip `mutate(Integer[], double, Random)`.

- [ ] **Step 1: Write failing tests for the demo problem**

```java
@Test
void returnsAPrototypeThatCreatesTheRequestedBinaryPopulation() {
    Popmember<Integer> prototype = new KnapsackProblem().createPrototype();
    Population<Integer> population = prototype.createInitialPopulation(8, new Random(4));

    assertEquals(8, population.size());
    for (Popmember<Integer> member : population.getIndividuals()) {
        assertEquals(10, member.getGenes().length);
        assertTrue(Arrays.stream(member.getGenes()).allMatch(gene -> gene == 0 || gene == 1));
    }
}

@Test
void givesOverweightSolutionsZeroFitness() {
    Popmember<Integer> member = new KnapsackProblem().createPrototype()
        .createChild(new Integer[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });

    assertEquals(0.0, member.getFitness(), 0.0);
}
```

- [ ] **Step 2: Run the demo tests to verify failure**

Run: `mvn test -Dtest=KnapsackProblemTest`

Expected: FAIL because demo classes do not exist.

- [ ] **Step 3: Implement the knapsack `GAProblem` demo**

```java
public final class KnapsackProblem implements GAProblem<Integer> {
    @Override
    public Popmember<Integer> createPrototype() {
        return new KnapsackPopmember(new KnapsackFitnessFunc());
    }
}
```

Use the current ten item values and weights with capacity 12. Generate exactly the requested number of binary chromosomes. Mutate each binary gene independently when `random.nextDouble() < mutationRate`. `createChild` returns a new `KnapsackPopmember` using the same fitness function.

- [ ] **Step 4: Run the demo tests**

Run: `mvn test -Dtest=KnapsackProblemTest`

Expected: PASS.

- [ ] **Step 5: Commit the demo implementation**

```bash
git add src/main/java/main/demo src/test/java/main/demo/KnapsackProblemTest.java src/main/ExampleFitnessFunc.java src/main/ExamplePopmember.java
```

### Task 4: Implement Validated CLI and Generation Runner

**Files:**
- Create: `src/main/java/main/JGAL.java`
- Create: `src/test/java/main/JGALTest.java`
- Delete: `src/main/JGAL.java`

**Interfaces:**
- Consumes: `GAProblem<T>`, `Crossover.crossGenes`, `Population<T>`, all selection implementations, and `KnapsackProblem` from prior tasks.
- Produces: `public static RunConfiguration parseArguments(String[] args)` and `public static <T> Popmember<T> run(GAProblem<T> problem, RunConfiguration configuration)`.
- Produces: `RunConfiguration` with problem class name, population size, generations, selection name, seed, and mutation rate.

- [ ] **Step 1: Write failing CLI and deterministic-run tests**

```java
@Test
void parsesAllSupportedOptions() {
    RunConfiguration configuration = JGAL.parseArguments(new String[] {
        "--problem", "main.demo.KnapsackProblem", "--population-size", "20",
        "--generations", "5", "--selection", "tournament", "--seed", "9",
        "--mutation-rate", "0.25"
    });

    assertEquals(20, configuration.getPopulationSize());
    assertEquals(5, configuration.getGenerations());
    assertEquals("tournament", configuration.getSelection());
    assertEquals(Long.valueOf(9L), configuration.getSeed());
    assertEquals(0.25, configuration.getMutationRate(), 0.0);
}

@Test
void repeatsASeededRunExactly() {
    RunConfiguration configuration = configuration(24, 8, "rank", 42L, 0.05);
    Popmember<Integer> first = JGAL.run(new KnapsackProblem(), configuration);
    Popmember<Integer> second = JGAL.run(new KnapsackProblem(), configuration);

    assertArrayEquals(first.getGenes(), second.getGenes());
    assertEquals(first.getFitness(), second.getFitness());
}
```

- [ ] **Step 2: Run CLI tests to verify failure**

Run: `mvn test -Dtest=JGALTest`

Expected: FAIL because the new runner API does not exist.

- [ ] **Step 3: Implement parsing, reflective loading, and generation coordination**

```java
int eliteCount = Math.max(1, (int) Math.ceil(population.size() / 10.0));
List<Popmember<T>> nextMembers = new ArrayList<Popmember<T>>(
    elitism.select(population, eliteCount, random));
List<Popmember<T>> parents = selection.select(population,
    population.size() - eliteCount + ((population.size() - eliteCount) % 2), random);
for (int index = 0; nextMembers.size() < population.size(); index += 2) {
    nextMembers.add(Crossover.crossGenes(parents.get(index), parents.get(index + 1),
        configuration.getMutationRate(), random));
}
```

Parse options as explicit pairs, reject every invalid form listed in the design, and expose a `usage()` string. `main` catches validation and reflection errors, prints the message plus usage to `System.err`, and returns nonzero through a small `runMain` method that is unit-testable. Use `Class.forName`, verify `GAProblem.class.isAssignableFrom`, then invoke the public zero-argument constructor. Select Boltzmann temperature as `1.0 - (0.99 * generation / Math.max(1, generations - 1))`.

- [ ] **Step 4: Run CLI tests and the full test suite**

Run: `mvn test -Dtest=JGALTest && mvn test`

Expected: PASS.

- [ ] **Step 5: Verify the packaged CLI runs the demo**

Run: `mvn package && java -jar target/jgal-*.jar --problem main.demo.KnapsackProblem --population-size 20 --generations 5 --selection tournament --seed 9 --mutation-rate 0.1`

Expected: exits successfully and prints `Fittest solution is:`.

- [ ] **Step 6: Commit the reusable runner**

```bash
git add src/main/java/main/JGAL.java src/test/java/main/JGALTest.java src/main/JGAL.java
```

### Task 5: Replace README and Perform Full Verification

**Files:**
- Modify: `README.md`
- Test: `src/test/java/main/JGALTest.java`

**Interfaces:**
- Consumes: actual Maven artifact name, CLI options, and `GAProblem` API from Tasks 1-4.
- Produces: accurate user documentation for installation, build, execution, customization, and selection strategies.

- [ ] **Step 1: Write README acceptance assertions in the CLI documentation test**

```java
@Test
void helpListsEverySupportedOption() {
    String usage = JGAL.usage();

    assertTrue(usage.contains("--problem"));
    assertTrue(usage.contains("--population-size"));
    assertTrue(usage.contains("--generations"));
    assertTrue(usage.contains("--selection"));
    assertTrue(usage.contains("--seed"));
    assertTrue(usage.contains("--mutation-rate"));
}
```

- [ ] **Step 2: Run the help test**

Run: `mvn test -Dtest=JGALTest#helpListsEverySupportedOption`

Expected: PASS because the runner supplies the documented interface.

- [ ] **Step 3: Replace README with current usage documentation**

Include these exact sections: Overview, Prerequisites, Build and Test, Run the Knapsack Demo, CLI Options, Create a Custom Problem, Selection Methods, and License. State Java 8+ and Maven 3.6+ prerequisites. Show `mvn test`, `mvn package`, and a `java -jar target/jgal-...jar --problem main.demo.KnapsackProblem` invocation. Document defaults and allowed selection values. Show a minimal `GAProblem` implementation that returns a custom `Popmember` prototype.

- [ ] **Step 4: Run all tests and verify the working tree**

Run: `mvn clean test && mvn package && git diff --check && git status --short`

Expected: Maven commands PASS, diff check has no output, and status lists only the README change before commit.

- [ ] **Step 5: Commit the documentation**

```bash
git add README.md src/test/java/main/JGALTest.java
```

- [ ] **Step 6: Push the verified commits**

```bash
git push origin main
```

Expected: push succeeds without force options.
