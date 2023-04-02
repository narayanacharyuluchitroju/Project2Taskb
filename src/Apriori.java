import java.util.*;

class Apriori {
    private static final int MIN_SUPPORT = 4; // Minimum support for an itemset to be considered frequent

    public static List<List<String>> generateFrequentItemsets(List<List<String>> transactions) {
        List<List<String>> frequentItemsets = new ArrayList<>();
        Map<List<String>, Integer> candidateItemsets = new HashMap<>();

        // Generate frequent itemsets of size 1
        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                List<String> itemset = Arrays.asList(item);
                candidateItemsets.put(itemset, candidateItemsets.getOrDefault(itemset, 0) + 1);
            }
        }
        frequentItemsets.addAll(filterCandidates(candidateItemsets, transactions));

        // Generate frequent itemsets of size > 1
        int k = 2;
        while (frequentItemsets.size() > 0) {
            Map<List<String>, Integer> candidateItemsetsK = new HashMap<>();

            // Generate candidate itemsets of size k
            for (int i = 0; i < frequentItemsets.size() - 1; i++) {
                List<String> itemset1 = frequentItemsets.get(i);
                for (int j = i + 1; j < frequentItemsets.size(); j++) {
                    List<String> itemset2 = frequentItemsets.get(j);
                    if (isJoinable(itemset1, itemset2)) {
                        List<String> candidateItemset = joinItemsets(itemset1, itemset2);

                        if (candidateItemsetsK.containsKey(candidateItemset)) {
                            candidateItemsetsK.put(candidateItemset, candidateItemsetsK.get(candidateItemset) + 1);
                        } else {
                            candidateItemsetsK.put(candidateItemset, 1);
                        }
                    }
                }
            }

            // Filter candidate itemsets of size k and add to frequent itemsets
            frequentItemsets.addAll(filterCandidates(candidateItemsetsK, transactions));
            k++;
        }

        return frequentItemsets;
    }

    public static List<List<String>> filterCandidates(Map<List<String>, Integer> candidateItemsets, List<List<String>> transactions) {
        List<List<String>> frequentItemsets = new ArrayList<>();
        for (List<String> itemset : candidateItemsets.keySet()) {
            int support = calculateSupport(itemset, transactions);
            if (support >= MIN_SUPPORT) {
                frequentItemsets.add(itemset);
            }
        }
        return frequentItemsets;
    }

    public static int calculateSupport(List<String> itemset, List<List<String>> transactions) {
        int count = 0;
        for (List<String> transaction : transactions) {
            if (transaction.containsAll(itemset)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isJoinable(List<String> itemset1, List<String> itemset2) {
        if (itemset1.size() != itemset2.size()) {
            return false;
        }
        for (int i = 0; i < itemset1.size() - 1; i++) {
            if (!itemset1.get(i).equals(itemset2.get(i))) {
                return false;
            }
        }
        return !itemset1.get(itemset1.size() - 1).equals(itemset2.get(itemset2.size() - 1));
    }

    public static List<String> joinItemsets(List<String> itemset1, List<String> itemset2) {
        List<String> joinedItemset = new ArrayList<>(itemset1);
        joinedItemset.add(itemset2.get(itemset2.size() - 1));
        return joinedItemset;
    }
}