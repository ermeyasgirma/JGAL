package main;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.Random;

import org.junit.jupiter.api.Test;

class CrossoverTest {
    @Test
    void returnsANewChildWithoutChangingEitherParent() {
        Popmember<Integer> first = new BinaryPopmember(new Integer[] { 0, 0, 0, 0 });
        Popmember<Integer> second = new BinaryPopmember(new Integer[] { 1, 1, 1, 1 });

        Popmember<Integer> child = Crossover.crossGenes(first, second, 0.0, new Random(3));

        assertNotSame(first, child);
        assertArrayEquals(new Integer[] { 0, 0, 0, 0 }, first.getGenes());
        assertArrayEquals(new Integer[] { 1, 1, 1, 1 }, second.getGenes());
    }

    static final class BinaryPopmember extends Popmember<Integer> {
        BinaryPopmember(Integer[] genes) {
            super(genes, values -> 0.0);
        }

        @Override
        public Population<Integer> createInitialPopulation(int size, Random random) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Popmember<Integer> createChild(Integer[] genes) {
            return new BinaryPopmember(genes);
        }

        @Override
        public Integer[] mutate(Integer[] genes, double mutationRate, Random random) {
            return genes;
        }
    }
}
