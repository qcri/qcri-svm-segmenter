/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newarabicsegmenter;

import com.qcri.farasa.segmenter.Farasa;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import static newarabicsegmenter.ArabicUtils.prefixes;
import static newarabicsegmenter.ArabicUtils.suffixes;
import static newarabicsegmenter.NBTokenizer.getProperSegmentation;

/**
 *
 * @author kareemdarwish
 */
public class NewArabicSegmenter {

    private static HashMap<String, String> hmFunctionWords = new HashMap<String, String>();
    private static HashMap<String, ArrayList<String>> hmPreviouslySeenTokenizations = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, ArrayList<String>> hmWordPossibleSplits = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, Integer> hmListMorph = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmListGaz = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> hPrefixes = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> hSuffixes = new HashMap<String, Integer>();
    private static HashMap<String, Boolean> hmValidSuffixes = new HashMap<String, Boolean>();
    private static HashMap<String, Boolean> hmValidPrefixes = new HashMap<String, Boolean>();

    private static HashMap<String, Boolean> hmValidSuffixesSegmented = new HashMap<String, Boolean>();
    private static HashMap<String, Boolean> hmValidPrefixesSegmented = new HashMap<String, Boolean>();

    public static void main(String[] args) throws UnsupportedEncodingException, IOException, FileNotFoundException, ClassNotFoundException, InterruptedException {
        //String inputDir = args[0]; // "/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/POSandNERData/";
        String mode = "test";
        String inputDir = "/home/disooqi/Dropbox/most_cited/POSandNERDataArz__/";
        // init();
        NBTokenizer nbt = null;

        nbt = new NBTokenizer(inputDir);
        //nbt.train(args[1]); // ("/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/ARZ/train.lang.ar-arz.sample");

//        nbt.train("/home/disooqi/qcri/egy_seg/disooqi__/arz.paper.train03");
        nbt.train("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/joint/egy_400K_LDC");
//        nbt.train("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/egy.trg");
        if (mode == "test") {
//        BufferedReader br = openFileForReading("/home/disooqi/qcri/egy_seg/LDC-ALL.tok.txt");
//        BufferedWriter bw = openFileForWriting("/home/disooqi/qcri/egy_seg/disooqi/LDC.tok.proper");
//        String line = "";
//	
//        //Farasa segmenter = new Farasa();
//	while ((line = br.readLine()) != null)
//        {
//            String[] words = line.split(" +");
//            for (String word : words)
//	    {
//                String w = ArabicUtils.buck2utf8(word);
//                String propSeg = getProperSegmentation(w);
//                
//                //String[] parts = (" " + word + " ").split(";");
//                //System.err.println(w +"\t"+ propSeg.replace(";", "").trim());
//                
//                bw.write(propSeg.replace(";", "").trim());
//                bw.write("\n");
//            }
//            //bw.write("\n");
//        }
//        bw.close();
            //loadSeenBefore("/home/disooqi/qcri/egy_seg/disooqi/arz.7000.seen");
            //segmentFile(args[2], nbt);
            //segmentFile("/home/disooqi/qcri/egy_seg/POSandNERDataArz/test.txt", nbt);

            //scoreTestFile("/home/disooqi/qcri/egy_seg/abdelali/test_utf8.txt", nbt);
            //scoreTestFile("/home/disooqi/qcri/egy_seg/disooqi/EGY-test-set-words-only-arb.txt", nbt);
            loadFunctionWords(inputDir);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/egy_seg/abdelali/test_utf8.txt", nbt);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/joint/data_1.test.trg", nbt);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/lev.trg", nbt);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/glf.trg", nbt);
            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/magh.trg", nbt);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/joint/glf_testfold_01.trg", nbt);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/joint/magh_1.test.trg", nbt);
            
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/gulf_seg/splits/glf_testfold_01.trg", nbt);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/joint/selectedAs_4001_5_egy", nbt);
        } else if (mode == "train") {
            nbt.writeOutSVMRankFeatureFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/egy.trg", "/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/egy.svm");

//            nbt.writeOutSVMRankFeatureFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/egy_seg/splits/data_5.train", "/home/disooqi/Dropbox/most_cited/final_splits_all_data/egy_seg/splits/data_5.train.svm");
            //nbt.writeOutSVMRankFeatureFile("/home/disooqi/qcri/egy_seg/ARZ/test.lang.arz", "/home/disooqi/qcri/egy_seg/ARZ/test.lang.arz.svm");
            //nbt.writeOutSVMRankFeatureFile("/home/disooqi/qcri/egy_seg/disooqi/arz.disooqi.train", "/home/disooqi/qcri/egy_seg/disooqi/arz.disooqi.svm");
            //nbt.writeOutSVMRankFeatureFile("/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/ARZ/train.lang.ar-arz.sample", "/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/ARZ/train.lang.ar-arz.sample.svm");
        }
    }

    private static void scoreTestFile(String filename, NBTokenizer nb) throws FileNotFoundException, IOException, ClassNotFoundException {
        BufferedReader br = openFileForReading(filename);
        String line = "";
        double correct = 0;
        double total = 0;
        //Farasa segmenter = new Farasa();
        BufferedWriter bf =  openFileForWriting("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/xxx");

        while ((line = br.readLine()) != null) {
            String[] words = line.split(" +");
            for (String w : words) {
                if (w.startsWith("@") || w.startsWith("http") || w.startsWith("#") || w.contains("/")|| w.startsWith("EOTWEET")) {
                    bf.write(w);
                    bf.write('\n');
                }

                if (!w.startsWith("@") && !w.startsWith("http") && !w.startsWith("#") && !w.contains("/")&& !w.startsWith("EOTWEET")) //&& !w.contains("Q")&& !w.contains("c") && !w.contains("O")&& !w.contains("C")&& !w.contains("V")
                {
                    String s = w.replace("+", "");

//                    s = ArabicUtils.buck2utf8(s);
                    s = ArabicUtils.removeDiacritics(s);

//                    ArrayList<String> possiblePartitions = nb.getAllPossiblePartitionsOfString(s2);
                    if (hmFunctionWords.containsKey(s)) {
                        s = hmFunctionWords.get(s);//.keySet().iterator().next();
                    } else {

                        TreeMap<Double, String> sol = nb.mostLikelyPartition(s, 1);
                        if (sol.size() > 0) {
                            if (sol.size() > 1) {
                                System.out.println("vvvvvvvvvvvvvvvv");
                            }
                            for (double d : sol.keySet()) {
                                s = sol.get(d);
                            }
                        }
                    }
                    s = s.trim().replace(";", "");

//                    ArrayList<String> ss = segmenter.segmentLine(s);
//                    s = ss.get(0);
                    w = w.trim();
                    if (s.startsWith("+")) {
                        s = s.substring(1);
                    }
                    if (s.endsWith("+")) {
                        s = s.substring(0, s.length() - 1);
                    }
                    if (s.startsWith("ال") && s.length() > 5) {
                        if (!s.startsWith("ال+")) {
                            s = s.replace("ال", "ال+");
                        }
                    }

                    bf.write(s);
                    bf.write('\n');
                    if (w.startsWith("+")) {
                        w = w.substring(1);
                    }
                    if (w.endsWith("+")) {
                        w = w.substring(0, w.length() - 1);
                    }
                    if (s.replace(";", "").replace("++", "+").equals(ArabicUtils.removeDiacritics(w))) {
                        correct++;
                    } else {
                        System.err.println(s.replace(";", "").replace("++", "+") + "\t" + ArabicUtils.removeDiacritics(w) + "\t" + w);
                        //System.err.println(s2);
                    }

                    total++;
                }else{
                    correct++;
                    total++;
                }
            }
        }
        bf.close();
        System.err.println((correct / total));
        System.err.println((correct));
        System.err.println((total));
    }

//    private static void loadSeenBefore(String BinDir) throws FileNotFoundException, IOException {
//        BufferedReader brSeenInTraining = openFileForReading(BinDir);
//        //BufferedReader brSeenInTraining = openFileForReading(BinDir + "seen-before.txt");
//        //BufferedReader brSeenInTraining = openFileForReading("/home/disooqi/qcri/egy_seg/ARZ/test.lang.arz");
//        String line = "";
//        while ((line = brSeenInTraining.readLine()) != null) {
//            String[] parts = line.trim().split("[ \t]+");
//            for (String w : parts) {
//
//                String word = ArabicUtils.removeDiacritics(ArabicUtils.buck2utf8(w).replace("+", ""));
////                if(word.equals("هتموت"))
////                        System.out.println("vvvvvvvvvvvvvvvv");
//                String segmented = ArabicUtils.removeDiacritics(ArabicUtils.buck2utf8(w));
//                segmented = getProperSegmentation(segmented);
//                if (!hmPreviouslySeenTokenizations.containsKey(word)) {
//                    hmPreviouslySeenTokenizations.put(word, new ArrayList<String>());
//                }
//                ArrayList<String> list = hmPreviouslySeenTokenizations.get(word);
//                if (!list.contains(segmented)) {
//                    list.add(segmented);
//                }
//                hmPreviouslySeenTokenizations.put(word, list);
//            }
//        }
//
////	brSeenInTraining = openFileForReading(BinDir + "seen-before.txt.dialect.txt");
////        line = "";
////        while ((line = brSeenInTraining.readLine()) != null)
////        {
////            String[] parts = line.trim().split("[ \t]+");
////            if (parts.length == 3)
////            {
////                String word = parts[0];
////                String segmented = parts[2];
////                segmented = getProperSegmentation(segmented);
////                if (!hmPreviouslySeenTokenizations.containsKey(word))
////                    hmPreviouslySeenTokenizations.put(word, new HashMap<String, Double>());
////                HashMap<String, Double> list = hmPreviouslySeenTokenizations.get(word);
////                if (list.containsKey(segmented))
////                    list.put(segmented, list.get(segmented) + Double.parseDouble(parts[1]));
////                else
////                    list.put(segmented, Double.parseDouble(parts[1]));
////                hmPreviouslySeenTokenizations.put(word, list);
////            }
////        }
//    }

    /**
     * *
     * private static void segmentFile(String filename, NBTokenizer nb) throws
     * FileNotFoundException, IOException { BufferedReader br =
     * openFileForReading(filename); BufferedWriter bw =
     * openFileForWriting(filename + ".out"); String line = ""; while ((line =
     * br.readLine()) != null) { ArrayList<String> words =
     * ArabicUtils.tokenize(line); String output = ""; for (String w : words) {
     * if (!w.startsWith("@") && !w.startsWith("http") && !w.contains("/")) {
     * String s = w; s = ArabicUtils.buck2utf8(s); if
     * (hmPreviouslySeenTokenizations.containsKey(s)) { s =
     * hmPreviouslySeenTokenizations.get(s).keySet().iterator().next(); } else {
     * TreeMap<Double, String> sol = nb.mostLikelyPartition(s, 1); if
     * (sol.size() > 0) { for (double d : sol.keySet()) s = sol.get(d); } } s =
     * s.trim().replace(";", ""); if (s.startsWith("+")) s = s.substring(1); if
     * (s.endsWith("+")) s = s.substring(0, s.length() - 1); output += s + " ";
     * } else { output += w + " "; } } bw.write(output.trim() + "\n");
     * bw.flush(); } bw.close(); }
     *
     *
     *
     * private static HashMap<String, String> loadSeenBeforeAndStore(String
     * BinDir) throws FileNotFoundException, IOException { BufferedReader
     * brSeenInTraining = openFileForReading(BinDir + "seen-before.txt");
     * BufferedWriter bw = openFileForWriting(BinDir + "seen-before.trim.txt");
     * String line = ""; while ((line = brSeenInTraining.readLine()) != null) {
     * String[] parts = line.trim().split("[ \t]+"); if (parts.length == 2) {
     * String word = ArabicUtils.buck2utf8(parts[1]).replace("+", ""); String
     * segmented = ArabicUtils.buck2utf8(parts[1]); segmented =
     * getProperSegmentation(segmented); if
     * (!hmPreviouslySeenTokenizations.containsKey(word))
     * hmPreviouslySeenTokenizations.put(word, new HashMap<String, Double>());
     * HashMap<String, Double> list = hmPreviouslySeenTokenizations.get(word);
     * if (list.containsKey(segmented)) list.put(segmented, list.get(segmented)
     * + Double.parseDouble(parts[0])); else list.put(segmented,
     * Double.parseDouble(parts[0])); hmPreviouslySeenTokenizations.put(word,
     * list); } } HashMap<String, String> seenBefore = new
     * HashMap<String, String>(); for (String w :
     * hmPreviouslySeenTokenizations.keySet()) { String topSolution = "skip"; if
     * (hmPreviouslySeenTokenizations.containsKey(w)) { if
     * (hmPreviouslySeenTokenizations.get(w).size() == 1) { for (String ss :
     * hmPreviouslySeenTokenizations.get(w).keySet()) topSolution = ss; } else {
     * double count = 0d; for (String ss :
     * hmPreviouslySeenTokenizations.get(w).keySet()) count +=
     * hmPreviouslySeenTokenizations.get(w).get(ss); if (count >= 5) {
     * topSolution = "skip"; for (String ss :
     * hmPreviouslySeenTokenizations.get(w).keySet()) if
     * (hmPreviouslySeenTokenizations.get(w).get(ss)/count > 0.7) topSolution =
     * ss; } else { topSolution = "skip"; } } } if (!topSolution.equals("skip"))
     * { bw.write(w + "\t" + topSolution + "\n"); seenBefore.put(w,
     * topSolution); } } bw.close(); return seenBefore; }
     *
     * private static String getMostLikely(HashMap<String, Double> input) {
     *
     * String output = "";
     *
     * return output; }
     *
     * private static void processRefFile(String filename, NBTokenizer nbt)
     * throws FileNotFoundException, IOException { BufferedReader br =
     * openFileForReading(filename); BufferedWriter bw =
     * openFileForWriting(filename + ".out"); String line = "";
     *
     * HashMap<String, String> seenBefore = new HashMap<String, String>();
     *
     * while ((line = br.readLine()) != null) { String[] words =
     * ArabicUtils.removeDiacritics(line).split("\t"); if (words.length == 2) {
     * // for (String w : words) { String w = words[0].replace("+", "").trim();
     * words[1] = getProperSegmentation(words[1]).trim(); // if
     * (!seenBefore.containsKey(w)) { String topSolution = "skip"; if
     * (w.equals("وهي")) System.err.println(); if
     * (hmPreviouslySeenTokenizations.containsKey(w)) { if
     * (hmPreviouslySeenTokenizations.get(w).size() == 1) { for (String ss :
     * hmPreviouslySeenTokenizations.get(w).keySet()) topSolution = ss; } else {
     * double count = 0d; for (String ss :
     * hmPreviouslySeenTokenizations.get(w).keySet()) count +=
     * hmPreviouslySeenTokenizations.get(w).get(ss); if (count >= 5) {
     * topSolution = "skip"; for (String ss :
     * hmPreviouslySeenTokenizations.get(w).keySet()) if
     * (hmPreviouslySeenTokenizations.get(w).get(ss)/count > 0.7) topSolution =
     * ss; } else { topSolution = "skip"; } } } if (topSolution.equals("skip"))
     * { TreeMap<Double, String> solutions =
     * nbt.mostLikelyPartition(ArabicUtils.buck2utf8(w), 1);
     *
     * if (solutions.size() > 0) topSolution =
     * solutions.get(solutions.firstKey()).replace(";", ""); if
     * (topSolution.endsWith("+")) topSolution = topSolution.substring(0,
     * topSolution.length() - 1); if (topSolution.startsWith("+")) topSolution =
     * topSolution.substring(1); if (words[1].endsWith("+")) words[1] =
     * words[1].substring(0, words[1].length() - 1); if
     * (words[1].startsWith("+")) words[1] = words[1].substring(1);
     *
     * }
     * if (topSolution.endsWith("ة") && !topSolution.endsWith("+ة")) topSolution
     * = topSolution.replace("ة", "+ة"); bw.write(line + "\t" +
     * topSolution.replace(";", "").replace("++", "+")); bw.flush(); if
     * (!w.matches(".*[a-zA-Z].*") &&
     * !ArabicUtils.normalize(topSolution).replace(";", "").replace("++",
     * "+").equals(ArabicUtils.normalize(words[1]))) System.err.println(words[1]
     * + "\t" + topSolution.replace(";", "").replace("++", "+"));
     * seenBefore.put(words[1], topSolution.replace(";", "").replace("++",
     * "+")); // } // else // { // bw.write(seenBefore.get(w).replace(";",
     * "").replace("++", "+") + " "); // } } bw.write("\n"); } bw.close(); }
     *
     * private static void serializeNTB(NBTokenizer nbt, String outputDir)
     * throws FileNotFoundException, IOException { FileOutputStream fileOut =
     * new FileOutputStream(outputDir + "/NBTokenizer.ser"); ObjectOutputStream
     * out = new ObjectOutputStream(fileOut); out.writeObject(nbt); out.close();
     * fileOut.close(); }
     *
     * private static NBTokenizer deserializeNTB(String inputDir) throws
     * FileNotFoundException, IOException, ClassNotFoundException { NBTokenizer
     * ntb = null; FileInputStream fileIn = new FileInputStream(inputDir +
     * "/NBTokenizer.ser"); ObjectInputStream in = new
     * ObjectInputStream(fileIn); ntb = (NBTokenizer) in.readObject();
     * in.close(); fileIn.close(); return ntb; }
     *
     * private static void init() throws FileNotFoundException,
     * UnsupportedEncodingException, IOException, ClassNotFoundException,
     * InterruptedException { String BinDir =
     * "/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/POSandNERData/";
     * for (int i = 0; i < prefixes.length; i++) {
     * hPrefixes.put(prefixes[i].toString(), 1); } for (int i = 0; i <
     * suffixes.length; i++) { hSuffixes.put(suffixes[i].toString(), 1); } if
     * (!BinDir.endsWith("/")) { BinDir += "/"; } // load previously seen
     * tokenizations BufferedReader brSeenInTraining = openFileForReading(BinDir
     * + "seen-before.txt"); String line = ""; while ((line =
     * brSeenInTraining.readLine()) != null) { String[] parts =
     * line.trim().split("[ \t]+"); if (parts.length == 2) { String word =
     * ArabicUtils.buck2utf8(parts[1]).replace("+", ""); String segmented =
     * ArabicUtils.buck2utf8(parts[1]); segmented =
     * getProperSegmentation(segmented); if
     * (!hmPreviouslySeenTokenizations.containsKey(word))
     * hmPreviouslySeenTokenizations.put(word, new HashMap<String, Double>());
     * HashMap<String, Double> list = hmPreviouslySeenTokenizations.get(word);
     * list.put(segmented, 1d); hmPreviouslySeenTokenizations.put(word, list); }
     * }
     *
     * // load lists BufferedReader br = openFileForReading(BinDir +
     * "generated-stems.morph.txt"); line = ""; while ((line = br.readLine()) !=
     * null) { hmListMorph.put(line, 1); }
     *
     * br = openFileForReading(BinDir + "generated-stems.loc-pers.txt"); line =
     * ""; while ((line = br.readLine()) != null) { hmListGaz.put(line, 1); }
     *
     * populatePossibleAffixes(); populatePossibleAffixesSegmented();
     *
     * }
     *
     *
     * private static void processFile(String filename, NBTokenizer nbt) throws
     * FileNotFoundException, IOException { BufferedReader br =
     * openFileForReading(filename); BufferedWriter bw =
     * openFileForWriting(filename + ".out"); String line = "";
     *
     * HashMap<String, String> seenBefore = new HashMap<String, String>();
     *
     * while ((line = br.readLine()) != null) { String[] words =
     * ArabicUtils.removeDiacritics(line).split(" +"); for (String w : words) {
     * if (!seenBefore.containsKey(w)) { TreeMap<Double, String> solutions =
     * nbt.mostLikelyPartition(ArabicUtils.buck2utf8(w), 1); String topSolution
     * = w; if (solutions.size() > 0) topSolution =
     * solutions.get(solutions.firstKey()); bw.write(topSolution.replace(";",
     * "").replace("++", "+") + " "); bw.flush(); seenBefore.put(w,
     * topSolution); } else { bw.write(seenBefore.get(w).replace(";",
     * "").replace("++", "+") + " "); } } bw.write("\n"); } bw.close(); }
     *
     *
     * private static void getFailedWords(String filename, NBTokenizer nbt)
     * throws FileNotFoundException, IOException { BufferedReader br =
     * openFileForReading(filename); BufferedWriter bw =
     * openFileForWriting(filename + ".errors"); String line = "";
     *
     * HashMap<String, Boolean> seenBefore = new HashMap<String, Boolean>();
     *
     * while ((line = br.readLine()) != null) { String[] words = line.split("
     * +"); // for (String w : words) // { String w = line.replaceFirst(".*\t",
     * "").trim(); w = ArabicUtils.removeDiacritics(w); if
     * (!seenBefore.containsKey(w)) { TreeMap<Double, String> solutions =
     * nbt.mostLikelyPartition(ArabicUtils.buck2utf8(w), 1); String word =
     * getProperSegmentation(ArabicUtils.buck2utf8(w)).replace(";", "").trim();
     * String topSolution = solutions.get(solutions.firstKey()).replace(";",
     * "").trim(); while (word.startsWith("+")) word = word.substring(1); while
     * (topSolution.startsWith("+")) topSolution = topSolution.substring(1);
     * while (word.endsWith("+")) word = word.substring(0, word.length() - 1);
     * while (topSolution.endsWith("+")) topSolution = topSolution.substring(0,
     * topSolution.length() - 1);
     *
     * if (!word.replace(";", "").replace("++",
     * "+").equals(topSolution.replace(";", "").replace("++", "+"))) {
     * bw.write(word.replace(";", "").replace("++", "+") + "\t" +
     * topSolution.replace(";", "").replace("++", "+") + "\n"); bw.flush(); }
     * seenBefore.put(w, Boolean.TRUE); } // } } bw.close(); }
     *
     *
     * private static void populatePossibleAffixes () { // populate prefixes //
     * if (head.matches("(و|ف)?(ب|ك|ل)?(ال)?") || head.equals("س") ||
     * head.equals("وس") || head.equals("فس"))
     *
     * String[] SetPreA = {"", "و", "ف"}; String[] SetPreB = {"", "ب", "ك",
     * "ل"}; String[] SetPreC = {"", "ال"};
     *
     * for (String a : SetPreA) { for (String b : SetPreB) { for (String c :
     * SetPreC) { String suf = a + b + c; if (suf.trim().length() > 0)
     * hmValidPrefixes.put(suf, Boolean.TRUE); } } } hmValidPrefixes.put("",
     * Boolean.TRUE); hmValidPrefixes.put("س", Boolean.TRUE);
     * hmValidPrefixes.put("وس", Boolean.TRUE); hmValidPrefixes.put("فس",
     * Boolean.TRUE);
     *
     * // p.matches("(ات|ون|ين)?,(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?") -- done //
     * || p.matches("[ويا],(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?") -- done // ||
     * p.matches("(ن|ت),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)") -- done // ||
     * p.matches("(نا),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)") -- done // ||
     * p.matches("(ون|ين|ات|ان|ا|ي|و|ت|ة|ن|وا)") -- done // ||
     * p.matches("(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)") -- done // ||
     * p.matches("(ون|ين|ي|و|ا),(نا|ن),(ه|ها|هما|هم|هن|ك|كما|كم|كن)")
     *
     * String[] SetA1 = {"", "ات", "ون", "ين", "ان"}; String[] SetA2 = {"", "ه",
     * "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "نا", "ي"};
     *
     * for (String s : SetA1) { for (String ss : SetA2) { hmValidSuffixes.put(s
     * + ss, Boolean.TRUE); } }
     *
     * String[] SetB1 = {"ا", "و", "ي"}; for (String s : SetB1) { for (String ss
     * : SetA2) { hmValidSuffixes.put(s + ss, Boolean.TRUE); } }
     *
     * String[] SetC1 = {"", "ن", "ت", "نا"}; String[] SetC2 = {"", "ه", "ها",
     * "هما", "هم", "هن", "ك", "كما", "كم", "كن", "كي", "ي"}; for (String s :
     * SetC1) { for (String ss : SetC2) { hmValidSuffixes.put(s + ss,
     * Boolean.TRUE); } }
     *
     * String[] SetD1 = {"ون", "ين", "ات", "ان", "ا", "ي", "و", "ت", "ة", "ن",
     * "وا", "ي", "و", "ا"}; for (String s : SetD1) { hmValidSuffixes.put(s,
     * Boolean.TRUE); }
     *
     * String[] setA = {"ا", "و", "ي", "ين", "ون"}; String[] setB = {"ن", "نا"};
     * String[] setC = {"ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن"};
     * for (String a : setA) { for (String b : setB) { for (String c : setC) {
     * String suf = a + b + c; hmValidSuffixes.put(suf, Boolean.TRUE); } } }
     * hmValidSuffixes.put("", Boolean.TRUE);
     *
     * // add Arz negation hmValidSuffixes.put("ش", Boolean.TRUE); String[] neg
     * =
     * "ش,تش,كش,يش,تهاش,توش,ناش,نيش,هاش,همش,هوش,واش,وتش,ييش,واهاش,واهوش,هومش,هاش,واش".split(",");
     * for (String n : neg) hmValidSuffixes.put(n, Boolean.TRUE); }
     *
     * private static void populatePossibleAffixesSegmented() { // populate
     * prefixes // if (head.matches("(و|ف)?(ب|ك|ل)?(ال)?") || head.equals("س")
     * || head.equals("وس") || head.equals("فس"))
     *
     * String[] SetPreA = {"", "و", "ف"}; String[] SetPreB = {"", "ب", "ك",
     * "ل"}; String[] SetPreC = {"", "ال"};
     *
     * for (String a : SetPreA) { for (String b : SetPreB) { for (String c :
     * SetPreC) { String separator1 = ""; String separator2 = ""; if (a.length()
     * > 0 && b.length() > 0) separator1 = ","; if (b.length() > 0 && c.length()
     * > 0) separator2 = ","; String suf = a + separator1 + b + separator2 + c;
     * if (suf.trim().length() > 0) { hmValidPrefixesSegmented.put(suf,
     * Boolean.TRUE); hmValidSuffixes.put(suf.replace(",", ""), Boolean.TRUE); }
     * } } } hmValidPrefixesSegmented.put("س", Boolean.TRUE);
     * hmValidPrefixesSegmented.put("و,س", Boolean.TRUE);
     * hmValidPrefixesSegmented.put("ف,س", Boolean.TRUE);
     *
     * // p.matches("(ات|ون|ين)?,(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?") -- done //
     * || p.matches("[ويا],(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?") -- done // ||
     * p.matches("(ن|ت),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)") -- done // ||
     * p.matches("(نا),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)") -- done // ||
     * p.matches("(ون|ين|ات|ان|ا|ي|و|ت|ة|ن|وا)") -- done // ||
     * p.matches("(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)") -- done // ||
     * p.matches("(ون|ين|ي|و|ا),(نا|ن),(ه|ها|هما|هم|هن|ك|كما|كم|كن)")
     *
     * String[] SetA1 = {"", "ات", "ون", "ين", "ان"}; String[] SetA2 = {"", "ه",
     * "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "نا", "ي"};
     *
     * for (String s : SetA1) { for (String ss : SetA2) { String separator = "";
     * if (s.trim().length() > 0 && s.trim().length() > 0) separator = ",";
     * hmValidSuffixesSegmented.put(s + separator + ss, Boolean.TRUE);
     * hmValidSuffixes.put(s + ss, Boolean.TRUE); } }
     *
     * String[] SetB1 = {"ا", "و", "ي"}; for (String s : SetB1) { for (String ss
     * : SetA2) { String separator = ""; if (s.trim().length() > 0 &&
     * s.trim().length() > 0) separator = ","; hmValidSuffixesSegmented.put(s +
     * separator + ss, Boolean.TRUE); hmValidSuffixes.put(s + ss, Boolean.TRUE);
     * } }
     *
     * String[] SetC1 = {"", "ن", "ت", "نا"}; String[] SetC2 = {"", "ه", "ها",
     * "هما", "هم", "هن", "ك", "كما", "كم", "كن", "كي", "ي"}; for (String s :
     * SetC1) { for (String ss : SetC2) { String separator = ""; if
     * (s.trim().length() > 0 && s.trim().length() > 0) separator = ",";
     * hmValidSuffixesSegmented.put(s + separator + ss, Boolean.TRUE);
     * hmValidSuffixes.put(s + ss, Boolean.TRUE); } }
     *
     * String[] SetD1 = {"ون", "ين", "ات", "ان", "ا", "ي", "و", "ت", "ة", "ن",
     * "وا", "ي", "و", "ا"}; for (String s : SetD1) {
     * hmValidSuffixesSegmented.put(s, Boolean.TRUE); }
     *
     * String[] setA = {"ا", "و", "ي", "ين", "ون"}; String[] setB = {"ن", "نا"};
     * String[] setC = {"ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن"};
     * for (String a : setA) { for (String b : setB) { for (String c : setC) {
     * String separator1 = ""; String separator2 = ""; if (a.length() > 0 &&
     * b.length() > 0) separator1 = ","; if (b.length() > 0 && c.length() > 0)
     * separator2 = ","; String suf = a + separator1 + b + separator2 + c;
     * hmValidSuffixesSegmented.put(suf, Boolean.TRUE);
     * hmValidSuffixes.put(suf.replace(",", ""), Boolean.TRUE); } } }
     *
     * String[] setA3 = {"ت"}; String[] setB3 = {"", "ا", "ي", "ين", "ان"};
     * String[] setC3 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم",
     * "كن"}; for (String a : setA3) { for (String b : setB3) { for (String c :
     * setC3) { String separator1 = ""; String separator2 = ""; if (a.length() >
     * 0 && b.length() > 0) separator1 = ","; if (b.length() > 0 && c.length() >
     * 0) separator2 = ","; String suf = a + separator1 + b + separator2 + c;
     * hmValidSuffixesSegmented.put(suf, Boolean.TRUE);
     * hmValidSuffixes.put(suf.replace(",", ""), Boolean.TRUE); } } } }
     */
    public static String getProperSegmentation(String input) {
        if (hPrefixes.isEmpty()) {
            for (int i = 0; i < prefixes.length; i++) {
                hPrefixes.put(prefixes[i].toString(), 1);
            }

            hPrefixes.put("ح", 1);
            hPrefixes.put("ه", 1);
            hPrefixes.put("م", 1);
            hPrefixes.put("ما", 1);
        }
        if (hSuffixes.isEmpty()) {
            for (int i = 0; i < suffixes.length; i++) {
                hSuffixes.put(suffixes[i].toString(), 1);
            }

            hSuffixes.put("ش", 1);
            hSuffixes.put("و", 1);
            hSuffixes.put("اه", 1);
            hSuffixes.put("يا", 1);
            hSuffixes.put("كي", 1);
            hSuffixes.put("ني", 1);
            hSuffixes.put("تي", 1);
            hSuffixes.put("اها", 1);
            hSuffixes.put("اهم", 1);

        }
        String output = "";
        String[] word = input.split("\\+");
        String currentPrefix = "";
        String currentSuffix = "";
        int iValidPrefix = -1;
        while (iValidPrefix + 1 < word.length && hPrefixes.containsKey(word[iValidPrefix + 1])) {
            iValidPrefix++;
        }

        int iValidSuffix = word.length;

        while (iValidSuffix > Math.max(iValidPrefix, 0) && (hSuffixes.containsKey(word[iValidSuffix - 1])
                || word[iValidSuffix - 1].equals("_"))) {
            iValidSuffix--;
        }

        for (int i = 0; i <= iValidPrefix; i++) {
            currentPrefix += word[i] + "+";
        }
        String stemPart = "";
        for (int i = iValidPrefix + 1; i < iValidSuffix; i++) {
            stemPart += word[i];
        }

        if (iValidSuffix == iValidPrefix) {
            iValidSuffix++;
        }

        for (int i = iValidSuffix; i < word.length && iValidSuffix != iValidPrefix; i++) {
            currentSuffix += "+" + word[i];
        }

        if (currentPrefix.endsWith("س+") && !stemPart.matches("^[ينأت].*")) {
            currentPrefix = currentPrefix.substring(0, currentPrefix.length() - 2);
            stemPart = "س" + stemPart;
        }
        output = currentPrefix + ";" + stemPart + ";" + currentSuffix;
        output = output.replaceFirst("^\\+", "");
        output = output.replaceFirst("\\+$", "");
        return output.replace("++", "+");
    }

    private static boolean checkIfLeadingLettersCouldBePrefixes(String head) {
        if (head.matches("(و|ف)?(ب|ك|ل)?(ال)?") || head.matches("[هحمس]") || head.matches("[وف][همحس]")
                || head.matches("ما") || head.matches("وما") || head.matches("فما")) {
            return true;
        } else {
            return false;
        }
    }

    private static String getPrefixSplit(String head) {
        String output = "";
        if (head.startsWith("و") || head.startsWith("ف")) {
            output += head.substring(0, 1) + ",";
            head = head.substring(1);
        }
        if (head.startsWith("ب") || head.startsWith("ك") || head.startsWith("ل") || head.startsWith("س")) {
            output += head.substring(0, 1) + ",";
            head = head.substring(1);
        }
        if (head.startsWith("ال")) {
            output += head.substring(0, 2) + ",";
        }
        output = output.replaceFirst(",$", "");
        return output;
    }

    public static ArrayList<String> getAllPossiblePartitionsOfString(String s) {
        ArrayList<String> output = new ArrayList<String>();
        s = s.trim();
        if (s.length() > 0) {
            String fullPartition = s.substring(0, 1);
            for (int i = 1; i < s.length(); i++) {
                fullPartition += "," + s.substring(i, i + 1);
            }
            String correctFullPartition = getProperSegmentation(fullPartition.replace(",", "+"));
            if (!output.contains(correctFullPartition)) {
                output.add(correctFullPartition);
            }
            // output.add(fullPartition);
            if (fullPartition.contains(",")) {
                output = getSubPartitions(fullPartition, output);
            }
        }
        return output;
    }

    private static ArrayList<String> getSubPartitions(String s, ArrayList<String> output) {
        // ArrayList<String> output = new ArrayList<String>();
        if (s.contains(",")) {
            String[] parts = s.split(",");
            for (int i = 0; i < parts.length - 1; i++) {
                String ss = "";
                // construct string with 1 units until i
                for (int j = 0; j < i; j++) {
                    if (j == 0) {
                        ss = parts[j];
                    } else {
                        ss += "," + parts[j];
                    }
                }
                // put 2 units
                if (i == 0) {
                    ss = parts[i] + parts[i + 1];
                } else {
                    ss += "," + parts[i] + parts[i + 1];
                }
                // put remaining 1 units until end of string
                for (int k = i + 2; k < parts.length; k++) {
                    if (k == 0) {
                        ss = parts[k];
                    } else {
                        ss += "," + parts[k];
                    }
                }
                if (!output.contains(getProperSegmentation(ss.replace(",", "+")))) {
                    output.add(getProperSegmentation(ss.replace(",", "+")));
                    if (ss.contains(",")) {
                        output = getSubPartitions(ss, output);
                    }
                }
            }
        }
        return output;
    }

    private static boolean checkIfRemainingLettersCouldBeSuffixes(String trail) {
        if (hmValidSuffixes.containsKey(trail)) {
            return true;
        } else {
            return false;
        }
    }

    private static String checkIfRemainingLettersCouldBeSuffixesString(String trail) {
        String output = "notFound";
        if (!checkIfRemainingLettersCouldBeSuffixes(trail)) {
            return output;
        }
        ArrayList<String> parts = getAllPossiblePartitionsOfString(trail);
        for (String p : parts) {
            if (hmValidSuffixesSegmented.containsKey(p)) {
                output = p;
            }
        }
        return output;
    }

    public static ArrayList<String> findAllPossibleSplits(String input, HashMap<String, Integer> list1, HashMap<String, Integer> list2) {
        if (hmWordPossibleSplits.containsKey(input)) {
            return hmWordPossibleSplits.get(input);
        }

        ArrayList<String> possibleSplits = possibleSplits = new ArrayList<String>();

        if (list1.containsKey(input) || list2.containsKey(input)) {
            possibleSplits.add(input);
        }

        for (int j = 1; j < input.length(); j++) {
            String head = input.substring(0, j);
            String trail = input.substring(j);

            if (checkIfLeadingLettersCouldBePrefixes(head)) {
                // get prefix split
                String prefixSplits = getPrefixSplit(head);
                // check if the rest is stem + suffixes
                if (trail.length() >= 2) {
                    for (int i = 0; i <= trail.length(); i++) {
                        String tok = trail.substring(0, i);
                        String remain = trail.substring(i);
                        String key = "";
                        if (tok.length() > 0) {
                            if (remain.trim().length() == 0) {
                                if (tok.endsWith("ة")) {
                                    key = prefixSplits + ";" + tok.substring(0, tok.length() - 1) + ";" + "ة";
                                } else {
                                    key = prefixSplits + ";" + tok + ";";
                                }
                            } else {
                                key = prefixSplits + ";" + tok + ";" + checkIfRemainingLettersCouldBeSuffixesString(remain);
                            }
                        } else if (remain.trim().length() == 0) {
                            key = prefixSplits;
                        } else {
                            key = prefixSplits + ";;" + checkIfRemainingLettersCouldBeSuffixesString(remain);
                        }
                        if ((list1.containsKey(tok) || list2.containsKey(tok))
                                && (checkIfRemainingLettersCouldBeSuffixes(remain) || remain.trim().length() == 0)
                                && !possibleSplits.contains(key)) {
                            possibleSplits.add(key);
                        }
                    }
                }
            } else if (checkIfRemainingLettersCouldBeSuffixes(trail)) {
                // check if rest is prefixes + stem
                if (head.length() >= 2) {
                    for (int i = 0; i <= head.length(); i++) {
                        if (i == 0 && (list1.containsKey(head) || list2.containsKey(head))
                                && !possibleSplits.contains(head + ";" + checkIfRemainingLettersCouldBeSuffixesString(trail))) {
                            possibleSplits.add(";" + head + ";" + checkIfRemainingLettersCouldBeSuffixesString(trail));
                        } else {
                            String prefix = head.substring(0, i);
                            String tok = head.substring(i);
                            String key = "";
                            if (tok.length() > 0) {
                                key = getPrefixSplit(prefix) + ";" + tok + ";" + checkIfRemainingLettersCouldBeSuffixesString(trail);
                            } else {
                                key = getPrefixSplit(prefix) + ";;" + checkIfRemainingLettersCouldBeSuffixesString(trail);
                            }
                            if ((list1.containsKey(tok) || list2.containsKey(tok))
                                    && checkIfLeadingLettersCouldBePrefixes(prefix)
                                    && !possibleSplits.contains(key)) {
                                possibleSplits.add(key);
                            }
                        }
                    }
                }
            }
        }
        hmWordPossibleSplits.put(input, possibleSplits);
        return possibleSplits;
    }

    public static BufferedReader openFileForReading(String filename) throws FileNotFoundException {
        BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
        return sr;
    }

    public static BufferedWriter openFileForWriting(String filename) throws FileNotFoundException {
        BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
        return sw;
    }

    private static void loadFunctionWords(String BinDir) throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException {

        if (!BinDir.endsWith("/")) {
            BinDir += "/";
        }
        // load previously seen tokenizations
        BufferedReader brFunctionWords = openFileForReading(BinDir + "levFuncWords");
        String line = "";
        while ((line = brFunctionWords.readLine()) != null) {
            String word = line.trim().replace("+", "");

            if (!hmFunctionWords.containsKey(word)) {
                hmFunctionWords.put(word, line.trim());
            }
        }
    }
}
