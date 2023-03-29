import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Task {
    String geneId;
    double r1;
    double r2;
    double split;
    double informationGain;

    public Task(String geneId, double r1, double r2, double split, double informationGain) {
        this.geneId = geneId;
        this.r1 = r1;
        this.r2 = r2;
        this.split = split;
        this.informationGain = informationGain;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public double getR1() {
        return r1;
    }

    public void setR1(double r1) {
        this.r1 = r1;
    }

    public double getR2() {
        return r2;
    }

    public void setR2(double r2) {
        this.r2 = r2;
    }

    public double getSplit() {
        return split;
    }

    public void setSplit(double split) {
        this.split = split;
    }

    public double getInformationGain() {
        return informationGain;
    }

    public void setInformationGain(double informationGain) {
        this.informationGain = informationGain;
    }
}

public class Main {
    public static Task entropyBasedBinning(String[] arr1, String[] arr2, int gene) {
        //Entropy of s
        int s_negative = 0;
        int s_positive = 0;
        for (int i = 0; i < arr1.length; i++) {
            if (arr2[i].trim().equals("2")) {
                s_negative += 1;
            } else {
                s_positive += 1;
            }
        }
        //System.out.println(s_negative+" "+s_positive);
        double entropy_s =
                -((double) s_positive / (arr1.length)) * (Math.log(((double) s_positive / (arr1.length))) / Math.log(2))
                        - ((double) s_negative / (arr1.length)) * (Math.log(((double) s_negative / (arr1.length))) / Math.log(2));
        //System.out.println(entropy_s);
        Arrays.sort(arr1, new Comparator<String>() {
            public int compare(String s1, String s2) {
                if (Double.parseDouble(s1) < (Double.parseDouble(s2))) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        double r1 = Double.parseDouble(arr1[0]);
        double r2 = Double.parseDouble(arr1[arr1.length - 1]);
        double split = (r2 + r1) / 2;
        LinkedList<String[]> s1 = new LinkedList<>();
        LinkedList<String[]> s2 = new LinkedList<>();
        for (int i = 0; i < arr1.length; i++) {
            if (Double.parseDouble(arr1[i]) <= split) {
                s1.add(new String[]{arr1[i], arr2[i]});
            } else {
                s2.add(new String[]{arr1[i], arr2[i]});
            }
        }
        //Entropy of s1
        int s1_negative = 0;
        int s1_positive = 0;
        for (int i = 0; i < s1.size(); i++) {
            if (s1.get(i)[1].trim().equals("2")) {
                s1_negative += 1;
            } else {
                s1_positive += 1;
            }
        }
        //System.out.println(s1_negative+" "+s1_positive+" "+(s1.size()));
        double entropy_s1 = -((double) s1_positive / (s1.size())) * (Math.log(((double) s1_positive / (s1.size()))) / Math.log(2))
                - ((double) s1_negative / (s1.size())) * (Math.log(((double) s1_negative / (s1.size()))) / Math.log(2));
        //System.out.print("entropy_s1: "+entropy_s1);
        //Entrophy of s2
        int s2_negative = 0;
        int s2_positive = 0;

        for (int i = 0; i < s2.size(); i++) {
            if (s2.get(i)[1].trim().equals("2")) {
                s2_negative += 1;
            } else {
                s2_positive += 1;
            }
        }
        //System.out.println();
        //System.out.print(s2_negative+" "+s2_positive+" "+(s2.size()));
        double entropy_s2 = -((double) s2_positive / (s2.size())) * (Math.log(((double) s2_positive / (s2.size()))) / Math.log(2)) -
                ((double) s2_negative / (s2.size())) * (Math.log(((double) s2_negative / (s2.size()))) / Math.log(2));
        //System.out.println("entropy_s2: "+entropy_s2);

        //Information gain:
        double informationGain_s1s2 = ((double) (s1.size()) / (arr1.length)) * entropy_s1 + ((double) (s2.size()) / (arr1.length)) * entropy_s2;

        //System.out.println("informationGain_s1s2: "+informationGain_s1s2);

        //Gain(split,s):
        double gain = entropy_s - informationGain_s1s2;
        //System.out.println("gain: "+gain);
        return new Task("p" + gene, r1, r2, split, informationGain_s1s2);
    }

    public static List<Map.Entry<String, double[]>>  findTopKgenes(List<String[]> ll)
    {
        String[] arr1 = new String[ll.size()];
        String[] arr2 = new String[ll.size()];
        HashMap<String, double[]> task1 = new HashMap<>();
        int counter1 = 0;
        for (int j = 0; j < ll.size(); j++) {
            arr2[j] = ll.get(j)[ll.get(j).length - 1].trim();
        }
        for (int i = 0; i < ll.get(0).length - 1; i++) {
            for (int j = 0; j < ll.size(); j++) {
                arr1[j] = ll.get(j)[i].trim();
            }
            Task a = entropyBasedBinning(arr1, arr2, i);
            //System.out.println("Information Gain "+a.informationGain);
            task1.put(a.geneId, new double[]{a.split, a.informationGain});
        }
        List<Map.Entry<String, double[]>> list = new LinkedList<Map.Entry<String, double[]>>(task1.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, double[]>>() {
            @Override
            public int compare(Map.Entry<String, double[]> o1, Map.Entry<String, double[]> o2) {
                if (o1.getValue()[1] < o2.getValue()[1]) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        return list;
    }
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("planning.csv"));
        String c = "1";
        List<String[]> ll = new LinkedList<>();
        sc.useDelimiter("\n");   //sets the delimiter pattern
        while (sc.hasNext())  //returns a boolean value
        {
            ll.add(sc.next().split(","));
        }
        sc.close();
        List<Map.Entry<String, double[]>> list = findTopKgenes(ll);

        for (Map.Entry<String, double[]> entry : list) {
            System.out.println(entry.getKey()+" "+entry.getValue()[0]+" "+entry.getValue()[1]);
        }


//        double avgGR = 0;
//        double avgSupp = 0;
//        double avgOvlp = 0;
//        double GR = 0;
//        double supp = 0;
//        for(int i=0;i<ll.get(i).length-1;i++)
//        {
//            double num = 0;
//            double denom = 0;
//            double pSupp = 0;
//            for(int j=0;j<ll.size();j++)
//            {
//                String cc = (ll.get(j)[ll.get(j).length-1]);
//                if((cc.trim()).equals(c))
//                {
//                    num+=Double.parseDouble(ll.get(j)[i]);
//                    pSupp+=Double.parseDouble(ll.get(j)[i]);
//                    System.out.println("1"+ Double.parseDouble(ll.get(j)[i]));
//                }
//                else {
//                    denom+=Double.parseDouble(ll.get(j)[i]);
//                    System.out.println("2 "+ Double.parseDouble(ll.get(j)[i]));
//                }
//            }
//            GR +=(double)((num+1)/(denom+1));
//            supp+=pSupp;
//        }
//        avgGR = (double)(GR/(ll.get(0).length-1));
//        avgSupp = (double)supp/(ll.get(0).length-1);
//        System.out.print(avgGR+" "+avgSupp);
    }
}
