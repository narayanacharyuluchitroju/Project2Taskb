import java.util.*;

public class PatternMining {

    public static List<Set<String>> apriori(List<List<String>> transactions, int classID, int supportThreshold, int k) {
        // Generate frequent itemsets with Apriori algorithm
        List<Set<String>> frequentItemsets = new ArrayList<>();
        Map<Set<String>, Integer> itemsetSupport = new HashMap<>();

        // Generate frequent itemsets of size 1
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).get(transactions.get(i).size() - 1).trim().equals(String.valueOf(classID))) {
                for (int j = 0; j < transactions.get(i).size() - 1; j++) {
                    Set<String> itemset = new HashSet<>();
                    itemset.add(transactions.get(i).get(j).trim());
                    itemsetSupport.put(itemset, itemsetSupport.getOrDefault(itemset, 0) + 1);
                }
            }
        }
        frequentItemsets.addAll(filterItemsets(itemsetSupport, supportThreshold));

        // Generate frequent itemsets of size > 1
        int i = 2;
        while (!frequentItemsets.isEmpty() && i <= k) {
            Map<Set<String>, Integer> candidateSupport = new HashMap<>();
            for (int j = 0; j < frequentItemsets.size(); j++) {
                for (int l = j + 1; l < frequentItemsets.size(); l++) {
                    Set<String> candidate = new HashSet<>(frequentItemsets.get(j));
                    candidate.addAll(frequentItemsets.get(l));
                    if (candidate.size() == i && isCandidateFrequent(candidate, itemsetSupport, i - 1, supportThreshold)) {
                        candidateSupport.put(candidate, candidateSupport.getOrDefault(candidate, 0) + 1);
                    }
                }
            }
            frequentItemsets = filterItemsets(candidateSupport, supportThreshold);
            itemsetSupport.putAll(candidateSupport);
            i++;
        }

        return itemsetSupport.entrySet().stream()
                .sorted(Map.Entry.<Set<String>, Integer>comparingByValue().reversed())
                .limit(k)
                .map(Map.Entry::getKey)
                .toList();
    }

    private static List<Set<String>> filterItemsets(Map<Set<String>, Integer> itemsetSupport, int supportThreshold) {
        return itemsetSupport.entrySet().stream()
                .filter(entry -> entry.getValue() >= supportThreshold)
                .map(Map.Entry::getKey)
                .toList();
    }

    private static boolean isCandidateFrequent(Set<String> candidate, Map<Set<String>, Integer> itemsetSupport, int k, int supportThreshold) {
        Set<Set<String>> subsets = generateSubsets(candidate, k);
        for (Set<String> subset : subsets) {
            if (!itemsetSupport.containsKey(subset) || itemsetSupport.get(subset) < supportThreshold) {
                return false;
            }
        }
        return true;
    }

    private static Set<Set<String>> generateSubsets(Set<String> set, int k) {
        Set<Set<String>> subsets = new HashSet<>();
        if (k == 0) {
            subsets.add(Collections.emptySet());
        } else {
            for (String element : set) {
                Set<String> subset = new HashSet<>(set);
                subset.remove(element);
                for (Set<String> subsetWithoutElement : generateSubsets(subset, k - 1)) {
                    Set<String> newSubset = new HashSet<>(subsetWithoutElement);
                    newSubset.add(element);
                    subsets.add(newSubset);
                }
            }
        }
        return subsets;
    }
}