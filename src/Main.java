import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("planning.csv"));
        List<List<String>> ll = new LinkedList<>();
        sc.useDelimiter("\n");   //sets the delimiter pattern
        while (sc.hasNext())  //returns a boolean value
        {
            ll.add(Stream.of(sc.next().trim().split(",")).toList());
        }
        sc.close();

//        for(List<String> i : ll)
//        {
//            for(String j : i)
//            {
//                System.out.print(j+" ");
//            }
//            System.out.println();
//        }

        List<Set<String>> frequentPatternsOfClass1 = PatternMining.apriori(ll, 1, 2, 5);
//        for(Set<String> i : frequentPatternsOfClass1)
//        {
//            for(String j : i)
//            {
//                System.out.print(j+" ");
//            }
//            System.out.println();
//        }
        List<Set<String>> frequentPatternsOfClass2 = PatternMining.apriori(ll, 2, 2, 5);
//        for(Set<String> i : frequentPatternsOfClass2)
//        {
//            for(String j : i)
//            {
//                System.out.print(j+" ");
//            }
//            System.out.println();
//        }
        CAEPClassifier classifiedData1 = new CAEPClassifier(frequentPatternsOfClass1, ll);
        CAEPClassifier classifiedData2 = new CAEPClassifier(frequentPatternsOfClass2, ll);


        int count = 0;
        for (List<String> i : ll) {
            System.out.println("Actual class: " + i.get(i.size() - 1) + ", Predicted class" + classifiedData1.predict(i));
            if(i.get(i.size() - 1).equals(String.valueOf(classifiedData1.predict(i))))
            {
                count++;
            }
        }
        System.out.println();
    }
}
