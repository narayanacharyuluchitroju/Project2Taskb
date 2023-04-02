import java.util.*;

public class CAEPClassifier {
    private List<Set<String>> patterns;
    private List<Map<Set<String>, Integer>> patternSupports;
    private List<List<String>> dataset;

    public CAEPClassifier(List<Set<String>> patterns, List<List<String>> dataset) {
        this.patterns = patterns;
        this.dataset = dataset;
        this.patternSupports = new ArrayList<>();
        for (int c = 1; c <= 2; c++) {
            Map<Set<String>, Integer> patternSupport = new HashMap<>();
            for (Set<String> pattern : patterns) {
                int support = 0;
                for (List<String> instance : dataset) {
                    if (Integer.parseInt(instance.get(instance.size() - 1)) == c && matchesPattern(instance, pattern)) {
                        support++;
                    }
                }
                patternSupport.put(pattern, support);
            }
            patternSupports.add(patternSupport);
        }
    }

    public int predict(List<String> instance) {
        double score1 = score(instance, 1);
        double score2 = score(instance, 2);
        return score1 >= score2 ? 1 : 2;
    }

    private double score(List<String> instance, int c) {
        double score = 0.0;
        for (Set<String> pattern : patterns) {
            if (patternSupports.get(c - 1).get(pattern) > 0 && matchesPattern(instance, pattern)) {
                double supp = patternSupports.get(c - 1).get(pattern);
                double Dc = countClassInstances(c);
                double term = supp * (supp + 1) / (Dc * supp);
                score += term;
            }
        }
        return score;
    }

    private int countClassInstances(int c) {
        int count = 0;
        for (List<String> instance : dataset) {
            if (Integer.parseInt(instance.get(instance.size() - 1)) == c) {
                count++;
            }
        }
        return count;
    }

    private boolean matchesPattern(List<String> instance, Set<String> pattern) {
        for (String feature : pattern) {
            if (!instance.contains(feature)) {
                return false;
            }
        }
        return true;
    }
}
