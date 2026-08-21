# Root JAR Convenience Design

## Goal

Allow users to run the packaged application from the repository root with:

```sh
java -jar jgal.jar main.demo.KnapsackProblem
```

while retaining Maven's conventional `target/jgal.jar` build artifact.

## Build Artifact Flow

`mvn package` continues to produce `target/jgal.jar` as the canonical Maven artifact. A Maven package-phase copy step creates an identical generated convenience copy at `jgal.jar` in the repository root.

The root-level JAR is not a source or release-controlled file. It is listed in `.gitignore`, and `mvn clean` removes it along with Maven's `target` directory.

## Documentation

The README build and run examples will state that `mvn package` creates both `target/jgal.jar` and the root-level `jgal.jar` convenience copy. Its runnable examples will use `java -jar jgal.jar <problem-class>`.

## Verification

Verification will run the test suite and package the project, confirm both JAR paths exist, execute the root-level JAR with the Knapsack demo, then run `mvn clean` and confirm both generated artifacts are absent.
