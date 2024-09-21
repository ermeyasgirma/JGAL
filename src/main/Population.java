package main;
import java.util.List;

public class Population<T> {
    private List<Popmember<T>> members;
    private Double totalFitness;

    public Population(List<Popmember<T>> members) {
        this.members = members;
    }

    public List<Popmember<T>> getIndividuals() {
        return members;
    }

    public void removeMember(Popmember<T> pm) {
        members.remove(pm);
        totalFitness -= pm.getFitness();
    }

    public double getTotalFitness() {
        if (totalFitness == null) {
            totalFitness = members.stream().mapToDouble(pm -> pm.getFitness()).sum();
        }
        return totalFitness;
    }

    public Popmember<T> getFittest() {
        return members.stream()
            .max((c1, c2) -> Double.compare(c1.getFitness(), c2.getFitness()))
            .orElse(null);
    }
}
