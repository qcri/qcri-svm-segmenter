/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jointdialsegmenter;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import static jointdialsegmenter.ArabicUtils.prefixes;
import static jointdialsegmenter.ArabicUtils.suffixes;

/**
 *
 * @author disooqi
 */
public class NBTokenizer implements java.io.Serializable {
    private int ProperCount = 0;
    private int allCount = 0;
    private static HashMap<String, ArrayList<String>> hmPreviouslySeenTokenizations = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, ArrayList<String>> hmWordPossibleSplits = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, Integer> hmListMorph = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmListGaz = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmAraLexCom = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmBuck = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmLocations = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmPeople = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hmStop = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hPrefixes = new HashMap<String, Integer>();
    private static HashMap<String, Integer> hSuffixes = new HashMap<String, Integer>();
    private static HashMap<String, Boolean> hmValidSuffixes = new HashMap<String, Boolean>();
    private static HashMap<String, Boolean> hmValidPrefixes = new HashMap<String, Boolean>();
    private static HashMap<String, Double> hmTemplateCount = new HashMap<String, Double>();

    private static HashMap<String, Boolean> hmValidSuffixesSegmented = new HashMap<String, Boolean>();
    private static HashMap<String, Boolean> hmValidPrefixesSegmented = new HashMap<String, Boolean>();
    private static HashMap<String, Double> wordCount = new HashMap<String, Double>();
    private static HashMap<String, Double> wordCountDialect = new HashMap<String, Double>();

    private static HashMap<String, Double> probPrefixes = new HashMap<String, Double>();
    private static HashMap<String, Double> probSuffixes = new HashMap<String, Double>();

    private static HashMap<String, Double> probCondPrefixes = new HashMap<String, Double>();
    private static HashMap<String, Double> probCondSuffixes = new HashMap<String, Double>();

    private static HashMap<String, Double> seenTemplates = new HashMap<String, Double>();

    private static HashMap<String, HashMap<String, Double>> probPrefixSuffix = new HashMap<String, HashMap<String, Double>>();
    private static HashMap<String, HashMap<String, Double>> probSuffixPrefix = new HashMap<String, HashMap<String, Double>>();

    private static HashMap<String, Double> generalVariables = new HashMap<String, Double>();

    private static FitTemplateClass ft = null;
    private static String dialect = "None";
    private static int foldId = 0;

    /**
     *
     * @param BinDir
     */
    public NBTokenizer(String BinDir, String dial, int fold) throws FileNotFoundException, IOException, ClassNotFoundException {
        dialect = dial;
        foldId = fold;
        ft = new FitTemplateClass();
        // String BinDir = "/Users/kareemdarwish/RESEARCH/ArabicProcessingTools-master/POSandNERData/";

        File file = new File(BinDir + "NTBdata.generalVariables.ser");
//        if (file.exists()) {
//            loadStoredData(BinDir);
//        } else 
	{

            generalVariables.put("hasTemplate", 0d);
            generalVariables.put("inMorphList", 0d);
            generalVariables.put("inGazList", 0d);
            generalVariables.put("allWordCount", 0d);
            generalVariables.put("averageStemLength", 0d);

//            for (int i = 0; i < prefixes.length; i++) {
//                hPrefixes.put(prefixes[i].toString(), 1);
//            }

if(dialect == "egy"){
            for (int i = 0; i < prefixes.length; i++) {
                hPrefixes.put(prefixes[i].toString(), 1);
            }
	    hPrefixes.put("ع", 1);
	    hPrefixes.put("ح", 1);
	    hPrefixes.put("ه", 1);
	    hPrefixes.put("م", 1);
	    hPrefixes.put("ما", 1);
            hPrefixes.put("ها", 1);
            
	    for (int i = 0; i < suffixes.length; i++) {
                hSuffixes.put(suffixes[i].toString(), 1);
            }
            hSuffixes.put("ى", 1);
	    hSuffixes.put("ش", 1);
            hSuffixes.put("و", 1);
            hSuffixes.put("ل", 1);
	    hSuffixes.put("اه", 1);
            hSuffixes.put("يا", 1);
            hSuffixes.put("كي", 1);
            hSuffixes.put("ني", 1);
            hSuffixes.put("نى", 1);
            hSuffixes.put("تي", 1);
            hSuffixes.put("ت", 1);
            hSuffixes.put("ي", 1);
	    hSuffixes.put("اها", 1);
	    hSuffixes.put("اهم", 1);
            hSuffixes.put("توا", 1);//خليتوا
            
        }else if(dialect == "lev"){
            hPrefixes.put("يآآ", 1);
            hPrefixes.put("يا", 1); 
            hPrefixes.put("شو", 1);
	    hPrefixes.put("عن", 1);
            hPrefixes.put("عم", 1);
	    hPrefixes.put("مو", 1);
	    hPrefixes.put("ال", 1);
            hPrefixes.put("لا", 1);
            hPrefixes.put("ما", 1);
            hPrefixes.put("شاء", 1);
	    hPrefixes.put("ب", 1);
            hPrefixes.put("ك", 1);
            hPrefixes.put("ح", 1);
            hPrefixes.put("ف", 1); 
            hPrefixes.put("ل", 1);
            hPrefixes.put("ه", 1);
            hPrefixes.put("م", 1);
            hPrefixes.put("ش", 1);//وان+ش+الله
            hPrefixes.put("ع", 1);
	    hPrefixes.put("و", 1);

            hSuffixes.put("كون", 1);
            hSuffixes.put("وا", 1);
            hSuffixes.put("ات", 1);
	    hSuffixes.put("گ",1);
            hSuffixes.put("لن", 1);//hSuffixes.put("ين", 1);
            hSuffixes.put("ين", 1);
            hSuffixes.put("ون", 1);
            hSuffixes.put("هم", 1);
            hSuffixes.put("هن", 1);
            hSuffixes.put("نا", 1);
            hSuffixes.put("كن", 1);
            hSuffixes.put("اش", 1);
            hSuffixes.put("تي", 1);
            hSuffixes.put("يت", 1);
	    hSuffixes.put("ها", 1);
	    hSuffixes.put("ني", 1);
            hSuffixes.put("كم", 1);//خليتوا
            hSuffixes.put("ة", 1);
	    hSuffixes.put("ت", 1);
            hSuffixes.put("و", 1);
            hSuffixes.put("ه", 1);
            hSuffixes.put("ا", 1);
            hSuffixes.put("ل", 1);
            hSuffixes.put("ي", 1);
            hSuffixes.put("ش", 1);
            hSuffixes.put("ك", 1);
            hSuffixes.put("ن", 1);//وحياتن
            hSuffixes.put("ہ", 1);
        }else if(dialect == "glf"){
            hPrefixes.put("يا", 1); 
	    hPrefixes.put("مو", 1);
	    hPrefixes.put("ال", 1);
            hPrefixes.put("إل", 1);
            hPrefixes.put("لا", 1);
            hPrefixes.put("ما", 1);
	    hPrefixes.put("ب", 1);
            hPrefixes.put("ك", 1);
            hPrefixes.put("ح", 1);
            hPrefixes.put("ف", 1); 
            hPrefixes.put("ل", 1);
            hPrefixes.put("ه", 1);
            hPrefixes.put("م", 1);
            hPrefixes.put("ش", 1);//وان+ش+الله
            hPrefixes.put("ع", 1);
	    hPrefixes.put("و", 1);
            
            hSuffixes.put("وا", 1);
            hSuffixes.put("ات", 1);
	    hSuffixes.put("گ",1);
            hSuffixes.put("ک",1);
            hSuffixes.put("ين", 1);
            hSuffixes.put("ون", 1);
            hSuffixes.put("هم", 1);
            hSuffixes.put("نا", 1);
            hSuffixes.put("هن", 1);
            hSuffixes.put("يت", 1);
	    hSuffixes.put("ها", 1);
	    hSuffixes.put("ني", 1);
            hSuffixes.put("يا", 1);
            hSuffixes.put("كم", 1);//خليتوا
            hSuffixes.put("ة", 1);
	    hSuffixes.put("ت", 1);
            hSuffixes.put("و", 1);
            hSuffixes.put("ه", 1);
            hSuffixes.put("ا", 1);
            hSuffixes.put("ل", 1);
            hSuffixes.put("ي", 1);
            hSuffixes.put("ج", 1);
            hSuffixes.put("ك", 1);
            hSuffixes.put("هہ", 1);
            hSuffixes.put("ن", 1);//وحياتن
            hSuffixes.put("ہ", 1);
            hSuffixes.put("ھ",1);
        }else if(dialect == "magh"){
            hPrefixes.put("ال", 1);
            hPrefixes.put("لا", 1);
            hPrefixes.put("يا", 1);
            hPrefixes.put("ما", 1);
            hPrefixes.put("كا", 1);
            hPrefixes.put("تا", 1);
	    hPrefixes.put("ب", 1);
            hPrefixes.put("ك", 1);
            hPrefixes.put("أ", 1);
            hPrefixes.put("ف", 1); 
            hPrefixes.put("ل", 1);
            hPrefixes.put("ت", 1);
            hPrefixes.put("ه", 1);
            hPrefixes.put("م", 1);
            hPrefixes.put("ش", 1);//وان+ش+الله
            hPrefixes.put("ح", 1); 
            hPrefixes.put("ع", 1);
	    hPrefixes.put("و", 1); 

            hSuffixes.put("وا", 1);
            hSuffixes.put("ات", 1);
	    hSuffixes.put("اش",1);
            hSuffixes.put("ين", 1);
            hSuffixes.put("ون", 1);
            hSuffixes.put("هم", 1);
            hSuffixes.put("نا", 1);
	    hSuffixes.put("ها", 1);
            hSuffixes.put("يا", 1);
	    hSuffixes.put("ني", 1);
            hSuffixes.put("كم", 1);//خليتوا
            hSuffixes.put("ة", 1);
	    hSuffixes.put("ت", 1);
            hSuffixes.put("و", 1);
            hSuffixes.put("ه", 1);
            hSuffixes.put("ا", 1);
            hSuffixes.put("ل", 1);
            hSuffixes.put("ي", 1);
            hSuffixes.put("ش", 1);
            hSuffixes.put("ك", 1);
        }

//            hPrefixes.put("يآآ", 1);
//            hPrefixes.put("يا", 1); 
//            hPrefixes.put("شو", 1);
//	    hPrefixes.put("عن", 1);
//            hPrefixes.put("عم", 1);
//	    hPrefixes.put("مو", 1);
//	    hPrefixes.put("ال", 1);
//            hPrefixes.put("لا", 1);
//            hPrefixes.put("ما", 1);
//            hPrefixes.put("شاء", 1);
//	    hPrefixes.put("ب", 1);
//            hPrefixes.put("ك", 1);
//            hPrefixes.put("ح", 1);
//            hPrefixes.put("ف", 1); 
//            hPrefixes.put("ل", 1);
//            hPrefixes.put("ه", 1);
//            hPrefixes.put("م", 1);
//            hPrefixes.put("ش", 1);//وان+ش+الله
//
//            hPrefixes.put("ع", 1);
//	    hPrefixes.put("و", 1);
////
////                hPrefixes.put("أ", 1);
////                hPrefixes.put("ا", 1);
////                hPrefixes.put("ال", 1);
////                hPrefixes.put("ان", 1);
////                hPrefixes.put("ب", 1);
////                hPrefixes.put("ت", 1);
////                hPrefixes.put("تا", 1);
////                hPrefixes.put("ح", 1);
////                hPrefixes.put("ش", 1);
////                hPrefixes.put("ع", 1);
////                hPrefixes.put("غ", 1);
////                hPrefixes.put("غا", 1);
////                hPrefixes.put("ف", 1);
////                hPrefixes.put("ك", 1);
////                hPrefixes.put("كا", 1);
////                hPrefixes.put("ل", 1);
////                hPrefixes.put("لا", 1);
////                hPrefixes.put("لو", 1);
////                hPrefixes.put("لي", 1);
////                hPrefixes.put("م", 1);
////                hPrefixes.put("ما", 1);
////                hPrefixes.put("ه", 1);
////                hPrefixes.put("ها", 1);
////                hPrefixes.put("و", 1);
////                hPrefixes.put("ي", 1);
////                hPrefixes.put("يا", 1);
////                hPrefixes.put("شو", 1);
////                hPrefixes.put("مو", 1);
////                hPrefixes.put("عم", 1);
////                hPrefixes.put("ام", 1);
////                hPrefixes.put("مآ", 1);
////                hPrefixes.put("اش", 1);
//
//
//            
//            
////	    for (int i = 0; i < suffixes.length; i++) {
////                hSuffixes.put(suffixes[i].toString(), 1);
////            }
//            hSuffixes.put("كون", 1);
//            hSuffixes.put("وا", 1);
//            hSuffixes.put("ات", 1);
//	    hSuffixes.put("گ",1);
//            hSuffixes.put("لن", 1);//hSuffixes.put("ين", 1);
//            hSuffixes.put("ين", 1);
//            hSuffixes.put("ون", 1);
//            hSuffixes.put("هم", 1);
//            hSuffixes.put("هن", 1);
//            hSuffixes.put("نا", 1);
//            hSuffixes.put("كن", 1);hSuffixes.put("اش", 1);
//            hSuffixes.put("تي", 1);
//            hSuffixes.put("يت", 1);
//	    hSuffixes.put("ها", 1);
//	    hSuffixes.put("ني", 1);
//            hSuffixes.put("كم", 1);//خليتوا
//            hSuffixes.put("ة", 1);
//	    hSuffixes.put("ت", 1);
//            hSuffixes.put("و", 1);
//            hSuffixes.put("ه", 1);
//            hSuffixes.put("ا", 1);
//            hSuffixes.put("ل", 1);
//            hSuffixes.put("ي", 1);
//            hSuffixes.put("ش", 1);
//            hSuffixes.put("ك", 1);
//            hSuffixes.put("ن", 1);//وحياتن
//            hSuffixes.put("ہ", 1);
////                    hSuffixes.put("ة", 1);
////                    hSuffixes.put("و", 1);
////                    hSuffixes.put("ي", 1);
////                    hSuffixes.put("ه", 1);
////                    hSuffixes.put("ش", 1);
////                    hSuffixes.put("ك", 1);
////                    hSuffixes.put("ات", 1);
////                    hSuffixes.put("ت", 1);
////                    hSuffixes.put("ها", 1);
////                    hSuffixes.put("ا", 1);
////                    hSuffixes.put("ون", 1);
////                    hSuffixes.put("كم", 1);
////                    hSuffixes.put("هم", 1);
////                    hSuffixes.put("نا", 1);
////                    hSuffixes.put("ين", 1);
////                    hSuffixes.put("ني", 1);
////                    hSuffixes.put("وا", 1);
////                    hSuffixes.put("تي", 1);
////                    hSuffixes.put("هن", 1);
////                    hSuffixes.put("ى", 1);
////                    hSuffixes.put("يا", 1);
////                    hSuffixes.put("كي", 1);
////                    hSuffixes.put("كوم", 1);
////                    hSuffixes.put("ن", 1);
////                    hSuffixes.put("هات", 1);
////                    hSuffixes.put("ہ", 1);
////                    hSuffixes.put("كوا", 1);
////                    hSuffixes.put("يش", 1);
////                    hSuffixes.put("ج", 1);
////                    hSuffixes.put("يين", 1);
////                    hSuffixes.put("وش", 1);
////                    hSuffixes.put("ية", 1);
////                    hSuffixes.put("تنا", 1);
////                    hSuffixes.put("وو", 1);
////                    hSuffixes.put("نى", 1);
////                    hSuffixes.put("تو", 1);
////                    hSuffixes.put("كن", 1);
////                    hSuffixes.put("ھ", 1);
////                    hSuffixes.put("يك", 1);
////                    hSuffixes.put("كك", 1);
////                    hSuffixes.put("اي", 1);
////                    hSuffixes.put("آ", 1);
////                    hSuffixes.put("او", 1);
////                    hSuffixes.put("لو", 1);
////                    hSuffixes.put("م", 1);
////                    hSuffixes.put("ان", 1);
////                    hSuffixes.put("اش", 1);
////                    hSuffixes.put("اه", 1);
////                    hSuffixes.put("وه", 1);
            
//            
            
            BufferedReader br = openFileForReading(BinDir + "generated-stems.morph.txt");
            String line = "";
            while ((line = br.readLine()) != null) {
                hmListMorph.put(line, 1);
            }

            br = openFileForReading(BinDir + "generated-stems.loc-pers.txt");
            line = "";
            while ((line = br.readLine()) != null) {
                hmListGaz.put(line, 1);
            }

            br = openFileForReading(BinDir + "AraComLex.verb");
            line = "";
            while ((line = br.readLine()) != null) {
                hmAraLexCom.put(line, 1);
            }
            br = openFileForReading(BinDir + "AraComLex.noun");
            line = "";
            while ((line = br.readLine()) != null) {
                hmAraLexCom.put(line, 1);
            }

            br = openFileForReading(BinDir + "lav.120k.prop");//wordcountAJ.arpa
            line = "";
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2 && parts[0].startsWith("-") && !parts[0].contains("inf")) {
                    wordCount.put(parts[1], Double.parseDouble(parts[0]));
                }
            }
	    
//	    br = openFileForReading(BinDir + "DialectTweetsEG.txt.lm");
//            line = "";
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split("\t");
//                if (parts.length == 2) // && parts[0].startsWith("-") && !parts[0].contains("inf")) {
//		{
//                    wordCountDialect.put(parts[0].trim(), Double.parseDouble(parts[1])/1000000d);
//                }
//            }
            populatePossibleAffixes();
            populatePossibleAffixesSegmented();



            br = openFileForReading(BinDir + "buckwalterStems.txt");
            line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(";")) {
                    String[] parts = line.split("\t");
                    if (parts.length == 4 && !line.contains("ABBREV")) {
                        hmBuck.put(ArabicUtils.buck2utf8(parts[0]), 1);
                    }
                    if (parts.length == 1 && !parts[0].matches(".*[a-zA-Z].*")  && !line.contains("ABBREV")) {
                        hmBuck.put(line.trim(), 1);
                    }
                }
            }

            br = openFileForReading(BinDir + "locations.txt");
            line = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                line = ArabicUtils.removeDiacritics(line);
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    int val = Integer.parseInt(parts[0]);
                    if (val == 1 && parts[1].startsWith("ال")) {
                        parts[1] = parts[1].substring(2);
                    }
                    if (!hmLocations.containsKey(parts[1]) && parts[1].length() > 2
                            && (parts[1].startsWith("ال") || parts[1].endsWith("ا") || parts[1].endsWith("ان") || parts[1].endsWith("ون") || parts[1].endsWith("ين")
                            || parts[1].startsWith("ب") || parts[1].startsWith("ك") || parts[1].startsWith("ف") || parts[1].startsWith("ل") || parts[1].startsWith("و"))) {
                        hmLocations.put(parts[1], val);
                    }
                }
            }

            br = openFileForReading(BinDir + "people.txt");
            line = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                line = ArabicUtils.removeDiacritics(line);
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    int val = Integer.parseInt(parts[0]);

                    if (!hmPeople.containsKey(parts[1]) && parts[1].length() > 2
                            && (parts[1].startsWith("ال") || parts[1].endsWith("ا") || parts[1].endsWith("ان") || parts[1].endsWith("ون") || parts[1].endsWith("ين")
                            || parts[1].startsWith("ب") || parts[1].startsWith("ك") || parts[1].startsWith("ف") || parts[1].startsWith("ل") || parts[1].startsWith("و"))) {
                        hmPeople.put(parts[1], val);
                    }
                }
            }

            br = openFileForReading(BinDir + "stoplist.txt");
            line = "";
            while ((line = br.readLine()) != null) {
                line = ArabicUtils.buck2utf8(line.trim());
                if (!line.startsWith("#")) {
                    hmStop.put(line, Integer.SIZE);
                }
            }
            // storeDataSources(BinDir);
        }
    }
       
    public void train(String filename) throws FileNotFoundException, IOException
    {
//            BufferedReader brSeenInTraining = openFileForReading("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/joint/joint.trian.5.400K_LDC");//_400K_LDC
            BufferedReader brSeenInTraining = openFileForReading(filename);
            String line = "";
            while ((line = brSeenInTraining.readLine()) != null) {
                String[] parts = line.trim().split("[ \t]+");
                
//                String[] words1 = line.trim().split("\t+");            
//                if(words1.length != 2)
//                    continue;
//            
//                String[] parts = words1[1].split(" +");
                
                for(String w : parts){
                //if (parts.length == 2 && Integer.parseInt(parts[0]) > 3) {
                    String word = ArabicUtils.buck2utf8(w).replace("+", "");
                    String segmented = ArabicUtils.buck2utf8(w);
                    segmented = getProperSegmentation(segmented);
                    if (!hmPreviouslySeenTokenizations.containsKey(word)) {
                        hmPreviouslySeenTokenizations.put(word, new ArrayList<String>());
                    }
                    ArrayList<String> list = hmPreviouslySeenTokenizations.get(word);
                    if (!list.contains(segmented)) {
                        list.add(segmented);
                    }
                    hmPreviouslySeenTokenizations.put(word, list);
                }
            }
        BufferedReader br = openFileForReading(filename);
//        BufferedWriter bw = openFileForWriting(filename + ".singleChar");
        line = "";
        HashMap<String, Double> prefixCount = new HashMap<String, Double>();
        HashMap<String, Double> suffixCount = new HashMap<String, Double>();
        
        HashMap<Integer, Integer> tmpStemLength = new HashMap<>();
        
        while ((line = br.readLine()) != null)
        {
            String[] words = line.split(" +");
//            String[] words1 = line.trim().split("\t+");            
//            if(words1.length != 2)
//                continue;
//            
//            String[] words = words1[1].split(" +");
            
            for (String w : words)
            {
                // if (w.length() >= 6)
                {
                    String word = getProperSegmentation(ArabicUtils.buck2utf8(w));
                    String[] parts = (" " + word + " ").split(";");
                    if (parts.length == 3)
                    {
                        // sum lengths of stems
                        generalVariables.put("averageStemLength", generalVariables.get("averageStemLength") + parts[1].length());
                        // averageStemLength += parts[1].length();

                        if (tmpStemLength.containsKey(parts[1].length()))
                            tmpStemLength.put(parts[1].length(), tmpStemLength.get(parts[1].length()) + 1);
                        else
                            tmpStemLength.put(parts[1].length(), 1);
    //                    if (parts[1].length() == 2)
    //                        bw.write(word + "\n");

                        String p = parts[0].trim();
                        String s = parts[2].trim();
                        generalVariables.put("allWordCount", generalVariables.get("allWordCount") + 1);
                        // allWordCount++;
                        if (probPrefixes.containsKey(parts[0].trim()))
                            probPrefixes.put(p, 1 + probPrefixes.get(p));
                        else
                            probPrefixes.put(p, 1d);

                        if (probSuffixes.containsKey(s.trim()))
                            probSuffixes.put(s, 1 + probSuffixes.get(s));
                        else
                            probSuffixes.put(s, 1d);

                        if (!probPrefixSuffix.containsKey(p))
                            probPrefixSuffix.put(p, new HashMap<String, Double>());
                        if (!probPrefixSuffix.get(p).containsKey(s))
                            probPrefixSuffix.get(p).put(s, 1d);
                        else
                            probPrefixSuffix.get(p).put(s, 1d + probPrefixSuffix.get(p).get(s)); 

                        if (!probSuffixPrefix.containsKey(s))
                            probSuffixPrefix.put(s, new HashMap<String, Double>());
                        if (!probSuffixPrefix.get(s).containsKey(p))
                            probSuffixPrefix.get(s).put(p, 1d);
                        else
                            probSuffixPrefix.get(s).put(p, 1d + probSuffixPrefix.get(s).get(p)); 

                        String template = ft.fitTemplate(parts[1]);
                        if (hmTemplateCount.containsKey(template))
                            hmTemplateCount.put(template, hmTemplateCount.get(template) + 1d);
                        else
                            hmTemplateCount.put(template, 1d);

                        if (!template.equals("Y"))
                            generalVariables.put("hasTemplate", generalVariables.get("hasTemplate") + 1);
                            // hasTemplate++;
                        if (hmListMorph.containsKey(parts[1]))
                            generalVariables.put("inMorphList", generalVariables.get("inMorphList") + 1);
                            // inMorphList++;
                        if (hmListGaz.containsKey(parts[1]))
                            generalVariables.put("inGazList", generalVariables.get("inGazList") + 1);
                            // inGazList++;

                        // full template with prefixes and suffixes
                        String fullTemplate = "";
                        fullTemplate = (parts[0] + template + parts[2]).replace(";", "").replace("++", "+").trim();

                        if (!seenTemplates.containsKey(fullTemplate))
                            seenTemplates.put(fullTemplate, 1d);
                        else
                            seenTemplates.put(fullTemplate, 1d + seenTemplates.get(fullTemplate));

                        // get all possible prefixes and suffixes
                        // maximum prefix length is 4 and maximum suffix length is 7
                        String unsegmentedWord = ArabicUtils.buck2utf8(w.replace("+", ""));
                        for (int i = 0; i <= 4 && i <= unsegmentedWord.length(); i++)
                        {
                            String possiblePrefix = unsegmentedWord.substring(0, i);
                            if (hmValidPrefixes.containsKey(possiblePrefix))
                            {
                                if (!prefixCount.containsKey(possiblePrefix))
                                    prefixCount.put(possiblePrefix, 1d);
                                else
                                    prefixCount.put(possiblePrefix, 1d + prefixCount.get(possiblePrefix));
                            }
                        }
                        for (int i = 0; i <= 7 && i <= unsegmentedWord.length(); i++)
                        {
                            String possibleSuffix = unsegmentedWord.substring(unsegmentedWord.length() - i);
                            if (hmValidSuffixes.containsKey(possibleSuffix))
                            {
                                if (!suffixCount.containsKey(possibleSuffix))
                                    suffixCount.put(possibleSuffix, 1d);
                                else
                                    suffixCount.put(possibleSuffix, 1d + suffixCount.get(possibleSuffix));
                            }
                        }
                    }
                }
            }
        }
        
//        for (int i : tmpStemLength.keySet())
//            System.err.println(i + "\t" + tmpStemLength.get(i));
        
        // normalize to get average length of stems
        generalVariables.put("averageStemLength", generalVariables.get("averageStemLength") / generalVariables.get("allWordCount"));
        // averageStemLength = averageStemLength / allWordCount;
        
        // add probability to conditional prefix and suffix probabilities
        for (String w : probPrefixes.keySet())
        {
            if (probPrefixes.containsKey(w) && prefixCount.containsKey(w.replace("+", "")))
                probCondPrefixes.put(w, probPrefixes.get(w)/prefixCount.get(w.replace("+", "")));
        }
        
        for (String w : probSuffixes.keySet())
        {
            if (probSuffixes.containsKey(w) && suffixCount.containsKey(w.replace("+", "")))
                probCondSuffixes.put(w, probSuffixes.get(w)/suffixCount.get(w.replace("+", "")));
        }
        
        // adjust from frequency to probability 
        for (String w : probPrefixSuffix.keySet())
        {
            double d = probPrefixes.get(w);
            for (String ww : probPrefixSuffix.get(w).keySet())
            {
                probPrefixSuffix.get(w).put(ww, probPrefixSuffix.get(w).get(ww)/d);
            }
        }
        
        for (String w : probSuffixPrefix.keySet())
        {
            double d = probSuffixes.get(w);
            for (String ww : probSuffixPrefix.get(w).keySet())
            {
                probSuffixPrefix.get(w).put(ww, probSuffixPrefix.get(w).get(ww)/d);
            }
        }
        
        for (String w : probPrefixes.keySet())
        {
            probPrefixes.put(w, probPrefixes.get(w)/generalVariables.get("allWordCount"));
            // probPrefixes.put(w, probPrefixes.get(w)/allWordCount);
        }
        
        for (String w : probSuffixes.keySet())
        {
            probSuffixes.put(w, probSuffixes.get(w)/generalVariables.get("allWordCount"));
            // probSuffixes.put(w, probSuffixes.get(w)/allWordCount);
        }
        
        for (String w : hmTemplateCount.keySet())
        {
            hmTemplateCount.put(w, hmTemplateCount.get(w)/generalVariables.get("allWordCount"));
            // hmTemplateCount.put(w, hmTemplateCount.get(w)/allWordCount);
        }
        
        for (String w : seenTemplates.keySet())
            seenTemplates.put(w, seenTemplates.get(w)/generalVariables.get("allWordCount"));
            // seenTemplates.put(w, seenTemplates.get(w)/allWordCount);
        
        generalVariables.put("hasTemplate", generalVariables.get("hasTemplate")/generalVariables.get("allWordCount"));
        generalVariables.put("inGazList", generalVariables.get("inGazList")/generalVariables.get("allWordCount"));
        generalVariables.put("inMorphList", generalVariables.get("inMorphList")/generalVariables.get("allWordCount"));
        
//        hasTemplate = hasTemplate/allWordCount;
//        inGazList = inGazList/allWordCount;
//        inMorphList = inMorphList/allWordCount;
        
    }
 
    public double scorePartition(String[] parts) {
        double score = 0;
        String prefix = parts[0].trim();
        String suffix = parts[2].trim();
        String stem = parts[1].trim();

        String[] magicNumbers = "".split(" +");
        // OLD AFFIXES
        magicNumbers = "1:-0.13993759 2:-0.15551305 3:0.016871307 4:0.27713552 5:0.29271352 6:0.72550726 7:0.57233739 8:-0.44624054 9:0.015576469 10:0.047894839 11:0.10107417 12:-0.010496355 13:-0.220439 14:0.028352588 15:0.39074963 16:0.028872643 17:-0.034349553 18:0.27244216".split(" +");
        magicNumbers = "1:-0.1437429 2:-0.14587499 3:0.009059391 4:0.28035128 5:0.28248245 6:0.67095339 7:0.57969368 8:-0.45099932 9:0.023242829 10:0.045848854 11:0.10726767 12:-0.0076125083 13:-0.26756725 14:0.028012326 15:0.42734501 16:0.052140888 17:-0.027449552 18:0.35501599".split(" +");
        magicNumbers = "1:-0.13872874 2:-0.156009 3:0.00972741 4:0.27190265 5:0.28918251 6:0.77266508 7:0.55129588 8:-0.43160897 9:0.015660772 10:0.048929986 11:0.11510156 12:0.00076817657 13:-0.22142744 14:0.028178005 15:0.41690528 16:0.061022416 17:-0.0079389904 18:0.31148425".split(" +");
        magicNumbers = "1:-0.14311616 2:-0.14180851 3:0.019013764 4:0.26508892 5:0.2637786 6:0.71531349 7:0.54154384 8:-0.42738515 9:0.023315709 10:0.043116774 11:0.097248919 12:-0.0062394082 13:-0.22795568 14:0.024601221 15:0.42470586 16:0.055280965 17:-0.0048320103 18:0.32335711".split(" +");
        magicNumbers = "1:-0.15398742 2:-0.15953337 3:0.0067969058 4:0.27761135 5:0.28315729 6:0.72491896 7:0.55344361 8:-0.46385553 9:0.027008567 10:0.04043375 11:0.11108473 12:-0.013414528 13:-0.21488859 14:0.026134165 15:0.41833535 16:0.051625419 17:0.0015610278 18:0.34552893".split(" +");

//          magicNumbers = "1:-0.15776402 2:-0.17259696 3:0.016584165 4:0.27663621 5:0.29146644 6:0.86059856 7:0.52246511 8:-0.69383603 9:0.046829008 10:0.049839422 11:0.1094351 12:-0.0055135894 13:-0.1341785 14:0.025724966 15:0.3322162 16:0.058035895 17:-0.042743068 18:0.24968114".split(" +");
//          magicNumbers = "1:-0.16857447 2:-0.18586496 3:0.010852139 4:0.29406831 5:0.31136018 6:0.83903432 7:0.5711813 8:-0.6776154 9:0.044625711 10:0.052013624 11:0.1087735 12:-0.01285803 13:-0.1601363 14:0.0196927 15:0.35984066 16:0.053455725 17:-0.041518539 18:0.29257083".split(" +");
//          magicNumbers = "1:-0.19355072 2:-0.19575582 3:0.0035985725 4:0.30087075 5:0.30307707 6:0.92068785 7:0.5168584 8:-0.6908685 9:0.052750155 10:0.051686369 11:0.1107377 12:0.0039237733 13:-0.099511325 14:0.026797337 15:0.34310049 16:0.05130716 17:-0.03658665 18:0.29665565".split(" +");
//          magicNumbers = "1:-0.20369643 2:-0.21253191 3:0.02135575 4:0.30473995 5:0.31357601 6:0.77745038 7:0.54087639 8:-0.65111947 9:0.055106837 10:0.052836269 11:0.093254991 12:-0.01140264 13:-0.093823522 14:0.019123781 15:0.39480996 16:0.084141269 17:-0.003593958 18:0.29830605".split(" +");
//          magicNumbers = "1:-0.13770179 2:-0.15532771 3:0.015420194 4:0.26960981 5:0.28723794 6:0.91180861 7:0.5558874 8:-0.68309611 9:0.044741634 10:0.045165185 11:0.10539415 12:-0.00071077276 13:-0.10350377 14:0.017877294 15:0.41053876 16:0.07590387 17:-0.02430323 18:0.32511786".split(" +");
        
        ArrayList<Double> magicNo = new ArrayList<Double>();
        for (String m : magicNumbers) {
            magicNo.add(Double.parseDouble(m.substring(m.indexOf(":") + 1)));
        }
        int feat_count = 0;

        if (probPrefixes.containsKey(prefix)) {
            score += magicNo.get(feat_count++) * Math.log(probPrefixes.get(prefix));
        } else {
            score += magicNo.get(feat_count++) * -10;
        }

        if (probSuffixes.containsKey(suffix)) {
            score += magicNo.get(feat_count++) * Math.log(probSuffixes.get(suffix));
        } else {
            score += magicNo.get(feat_count++) * -10;
        }

        String trimmedTemp = suffix.replace("+", "").replace(";", "").replace(",", "");
        String altStem = "";
        if (trimmedTemp.startsWith("ت") && trimmedTemp.length() > 1) {
            altStem = stem + "ة";
        }

        double stemWordCount = -10;
        if (wordCount.containsKey(stem)) {
            stemWordCount = wordCount.get(stem);
        } else if (altStem.length() > 1 && wordCount.containsKey(altStem)) // && wordCount.get(altStem) > stemWordCount)
        {
            stemWordCount = wordCount.get(altStem);
        }

        //if (wordCount.containsKey(stem))
        score += magicNo.get(feat_count++) * stemWordCount;
                //else
        //    score += 0.19470689 * -10;

        if (probPrefixSuffix.containsKey(prefix) && probPrefixSuffix.get(prefix).containsKey(suffix)) {
            score += magicNo.get(feat_count++) * Math.log(probPrefixSuffix.get(prefix).get(suffix));
        } else {
            score += magicNo.get(feat_count++) * -20;
        }

        if (probSuffixPrefix.containsKey(suffix) && probSuffixPrefix.get(suffix).containsKey(prefix)) {
            score += magicNo.get(feat_count++) * Math.log(probSuffixPrefix.get(suffix).get(prefix));
        } else {
            score += magicNo.get(feat_count++) * -20;
        }

        if (!ft.fitTemplate(stem).equals("Y")) {
            score += magicNo.get(feat_count++) * Math.log(generalVariables.get("hasTemplate"));
            // score += magicNo.get(5) * Math.log(hasTemplate);
        } else {
            score += magicNo.get(feat_count++) * Math.log(1 - generalVariables.get("hasTemplate"));
            // score += magicNo.get(5) * Math.log(1 - hasTemplate);
        }

        if (hmListMorph.containsKey(stem) || (stem.endsWith("ي") && hmListMorph.containsKey(stem.substring(0, stem.length() - 1) + "ى"))) {
            score += magicNo.get(feat_count++) * Math.log(generalVariables.get("inMorphList"));
            // score += magicNo.get(6) * Math.log(inMorphList);
        } else {
            score += magicNo.get(feat_count++) * Math.log(1 - generalVariables.get("inMorphList"));
            // score += magicNo.get(6) * Math.log(1 - inMorphList);
        }

        if (hmListGaz.containsKey(stem) || (stem.endsWith("ي") && hmListGaz.containsKey(stem.substring(0, stem.length() - 1) + "ى"))) {
            score += magicNo.get(feat_count++) * Math.log(generalVariables.get("inGazList"));
            // score += magicNo.get(7) * Math.log(inGazList);
        } else {
            score += magicNo.get(feat_count++) * Math.log(1 - generalVariables.get("inGazList"));
            // score += magicNo.get(7) * Math.log(1 - inGazList);
        }

        if (probCondPrefixes.containsKey(prefix)) {
            score += magicNo.get(feat_count++) * Math.log(probCondPrefixes.get(prefix));
        } else {
            score += magicNo.get(feat_count++) * -20;
        }

        if (probCondSuffixes.containsKey(suffix)) {
            score += magicNo.get(feat_count++) * Math.log(probCondSuffixes.get(suffix));
        } else {
            score += magicNo.get(feat_count++) * -20;
        }

        // get probability with first suffix . for example xT + p would produce xTp 
        String stemPlusFirstSuffix = stem;
        if (suffix.indexOf("+", 1) > 0) {
            stemPlusFirstSuffix += suffix.substring(1, suffix.indexOf("+", 1));
        } else {
            stemPlusFirstSuffix += suffix;
        }
        trimmedTemp = stemPlusFirstSuffix.replace("+", "").replace(";", "").replace(",", "");
        stemWordCount = -10;
        if (wordCount.containsKey(stemPlusFirstSuffix)) {
            stemWordCount = wordCount.get(stemPlusFirstSuffix);
        } else if (stem.endsWith("ي") && wordCount.containsKey(stem.substring(0, stem.length() - 1) + "ى")) {
            stemWordCount = wordCount.get(stem.substring(0, stem.length() - 1) + "ى");
        } else if (stemPlusFirstSuffix.endsWith("ت") && wordCount.containsKey(stemPlusFirstSuffix.substring(0, stemPlusFirstSuffix.length() - 1) + "ة")) {
            stemWordCount = wordCount.get(stemPlusFirstSuffix.substring(0, stemPlusFirstSuffix.length() - 1) + "ة");
        }
        score += magicNo.get(feat_count++) * stemWordCount;
        
        // put template feature
        String template = ft.fitTemplate(stem);
        if (hmTemplateCount.containsKey(template))
            score += magicNo.get(feat_count++) * Math.log(hmTemplateCount.get(template));
        else
            score += magicNo.get(feat_count++) * -10;
        
        // difference from average length
        score += magicNo.get(feat_count++) * Math.log(Math.abs(stem.length() - generalVariables.get("averageStemLength")));
        
        trimmedTemp = suffix.replace("+", "").replace(";", "").replace(",", "");
        altStem = "";
        if (trimmedTemp.startsWith("ت") && trimmedTemp.length() > 1)
            altStem = stem + "ة";

        if (wordCount.containsKey(stem))
            stemWordCount = wordCount.get(stem);
        else if (stem.endsWith("ي") && wordCount.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
            stemWordCount = wordCount.get(stem.substring(0, stem.length() - 1) + "ى");
        else if (altStem.trim().length() > 0 && wordCount.containsKey(altStem)) // && wordCount.get(altStem) > stemWordCount)
            stemWordCount = wordCount.get(altStem);

        if (hmAraLexCom.containsKey(stem))
        {
            if (wordCount.containsKey(stem))
                score += magicNo.get(feat_count++) *  wordCount.get(stem);
            else
                score += magicNo.get(feat_count++) * -10;
        }
        else if (stem.endsWith("ي") && hmAraLexCom.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
        {
            if (wordCount.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
                score += magicNo.get(feat_count++) *  wordCount.get(stem.substring(0, stem.length() - 1) + "ى");
            else
                score += magicNo.get(feat_count++) * -10;
        }
        else if (altStem.trim().length() > 0 && hmAraLexCom.containsKey(altStem))
        {
            if (wordCount.containsKey(altStem))
                score += magicNo.get(feat_count++) * wordCount.get(altStem);
            else
                score += magicNo.get(feat_count++) * -10;
        }
        else
        {
            score += magicNo.get(feat_count++) * -20;
        }
        
        if (hmBuck.containsKey(stem))
        {
            score += magicNo.get(feat_count++);
        }
        else if (stem.endsWith("ي") && hmBuck.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
        {
            score += magicNo.get(feat_count++);
        }
        else
        {
            score += -1 * magicNo.get(feat_count++);
        }
        
        if (hmLocations.containsKey(stem))
        {
            score += magicNo.get(feat_count++);
        }
        else
        {
            score += -1 * magicNo.get(feat_count++);
        }
        
        if (hmPeople.containsKey(stem))
        {
            score += magicNo.get(feat_count++);
        }
        else
        {
            score += -1 * magicNo.get(feat_count++);
        }
        
        if (hmStop.containsKey(stem))
        {
            score += magicNo.get(feat_count++);
        }
        else if (stem.endsWith("ي") && hmStop.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
        {
            score += magicNo.get(feat_count++);
        }
        else
        {
            score += -1 * magicNo.get(feat_count++);
        }
	
//	if (wordCountDialect.containsKey(stem))
//	    score += magicNo.get(feat_count++) * wordCountDialect.get(stem);
//	else
//	    score += magicNo.get(feat_count++) * -20d;
	
        return score;
    }
    
    public ArrayList<String> segmentLine(String line) throws IOException
    {
        ArrayList<String> output = new ArrayList<String>();
        ArrayList<String> words = ArabicUtils.tokenize(ArabicUtils.removeDiacritics(line));
        for (String w : words) {
            //if (!hmSeenBefore.containsKey(w))
	    {
                //TreeMap<Double, String> solutions = nbt.mostLikelyPartition(buck2utf8(w), 1);
                TreeMap<Double, String> solutions = mostLikelyPartition((w), 1);
                String topSolution = w;
                if (solutions.size() > 0)
                    topSolution = solutions.get(solutions.firstKey());
                topSolution = topSolution.replace(";", "").replace("++", "+");
                if (topSolution.startsWith("+"))
                    topSolution = topSolution.substring(1);
                if (topSolution.endsWith("+"))
                    topSolution = topSolution.substring(0, topSolution.length() - 1);
                // hmSeenBefore.put(w, topSolution);
                output.add(topSolution.replace("++", "+"));

            }
//            else
//            {
//                String topSolution = hmSeenBefore.get(w).replace(";", "").replace("++", "+");
//                if (topSolution.startsWith("+"))
//                    topSolution = topSolution.substring(1);
//                if (topSolution.endsWith("+"))
//                    topSolution = topSolution.substring(0, topSolution.length() - 1);
//                output.add(topSolution);
//            }
        }
        return output;
    }
    
    public TreeMap<Double, String> mostLikelyPartition(String word, int numberOfSolutions) throws FileNotFoundException, IOException
    {
        word = word.trim();
        ArrayList<String> possiblePartitions = getAllPossiblePartitionsOfString(word);
        
        if (word.startsWith("لل"))
            possiblePartitions.addAll(getAllPossiblePartitionsOfString("لال" + word.substring(2)));
        else if (word.startsWith("ولل"))
            possiblePartitions.addAll(getAllPossiblePartitionsOfString("ولال" + word.substring(3)));
        else if (word.startsWith("فلل"))
            possiblePartitions.addAll(getAllPossiblePartitionsOfString("فلال" + word.substring(3)));
        // score all the different options
        TreeMap<Double, String> scores = new TreeMap<Double, String>();
        
        if (hmPreviouslySeenTokenizations.containsKey(word.replace("+", "")))
        {
            for (String p : hmPreviouslySeenTokenizations.get(word.replace("+", "")))
            {
                String pp = getProperSegmentation(p.replace(";", ""));
                String[] parts = (" " + pp + " ").split(";");
                double score = scorePartition(parts);
                while (scores.containsKey(score))
                    score -= 0.00001;
                scores.put(score, pp);
            }
        }
        else 
        {
            for (String p : possiblePartitions)
            {
                String pp = getProperSegmentation(p.replace(";", ""));
                String[] parts = (" " + pp + " ").split(";");
                if (parts.length == 3)
                {
                    double score = scorePartition(parts);
                    while (scores.containsKey(score))
                        score -= 0.00001;
                    scores.put(score, pp);
                }
            }
        }
        // keep the top 3 segmentations and throw away the rest
        int scoresSize = scores.size() - numberOfSolutions;
        TreeMap<Double, String> scoresFinal = new TreeMap<Double, String>();
        int i = 0;
        for (double d : scores.keySet())
        {
            if (i >= scoresSize)
                scoresFinal.put(d, scores.get(d));
            i++;
        }
        return scoresFinal;
    }

    public void writeOutSVMRankFeatureFile(String inputFile, String outputFile) throws FileNotFoundException, IOException
    {
        BufferedReader br = openFileForReading(inputFile);
        BufferedWriter bw = openFileForWriting(outputFile);
        String line = "";
        int qid = 0;
        while ((line = br.readLine()) != null)
        {
            
//            String[] words1 = line.trim().split("\t+");            
//            if(words1.length != 2)
//                continue;            
//	    String[] words = words1[1].split(" +");
            
            String[] words = line.split(" +");
            for (String w : words)
            {
                // if (w.replace("+", "").length() < 6)
                {
                    qid++;
                    for (String s : genSVMRankFeatures(ArabicUtils.buck2utf8(w), qid))
                    {
                        bw.write(s + "\n");
                    }
                    
                }
            }
            
        }
        bw.close();
        //System.out.println("All count: " + allCount);
    }
    
    public ArrayList<String> genSVMRankFeatures(String word, int qid)
    {
        // String output = "";
     
        String properSeg = getProperSegmentation(word);
        
        ArrayList<String> possiblePartitions = getAllPossiblePartitionsOfString(word.replace(",", "").replace("+", ""));
        // make sure they are proper
        ArrayList<String> properPossiblePartitions = new ArrayList<String>();
        TreeMap<String, Integer> stems = new TreeMap<String, Integer>();
        for (String p : possiblePartitions)
        {
            String pp = getProperSegmentation(p.replace(";", "").replace("++", "+"));
            if (!properPossiblePartitions.contains(pp))
            {
                properPossiblePartitions.add(" " + pp + " ");
                stems.put((" " + pp + " "), 0);
            }
        }
        
        // check for the preferred stem.  To be prefered the stem needs to match the maximum number of lists, have a template, etc.
        int maxAgg = -10;
        int minAgg = 10;
        for (String p : stems.keySet())
        {
            String prefix = p.split(";")[0].trim();
            String stem = p.split(";")[1].trim();
            String suffix = p.split(";")[2].trim();
            int agg = 0;

            if (wordCount.containsKey(stem) && wordCount.containsKey((prefix + stem).replace("+", "")) && wordCount.get(stem) > wordCount.get((prefix + stem).replace("+", "")) + 1)
                agg++;
            else if (wordCount.containsKey(stem) && wordCount.containsKey((prefix + stem).replace("+", "")) && wordCount.get(stem) + 1 < wordCount.get((prefix + stem).replace("+", "")))
                agg--;
            
            if (wordCount.containsKey(stem) && wordCount.containsKey((stem + suffix).replace("+", "")) && wordCount.get(stem) > wordCount.get((stem + suffix).replace("+", "")) + 1)
                agg++;
            else if (wordCount.containsKey(stem) && wordCount.containsKey((stem + suffix).replace("+", "")) && wordCount.get(stem) + 1 < wordCount.get((stem + suffix).replace("+", "")))
                agg--;
            
            stems.put(p, agg);
            if (maxAgg < agg)
                maxAgg = agg;
            if (minAgg > agg)
                minAgg = agg;
        }
        
        // score all the different options
        ArrayList<String> scores = new ArrayList<String>();
        boolean hasProper = false;
        allCount++;
        for (String pp : properPossiblePartitions)
        {
            // String pp = getProperSegmentation(p.replace(";", "").replace("++", "+"));
            String[] parts = (pp).split(";");
            String features = ""; 
            //if (stems.get(pp) == maxAgg)
                features = getSVMFeatureVector(parts, stems.get(pp));

            String output = "";
            if (features.length() > 0)
            {
                if (properSeg.equals(pp.trim())){
                    output = "2";
                    hasProper = true;
                    ProperCount++;
                }
                else
                    output = "1";
                
                output += " qid:" + String.valueOf(qid);
                output += " " + features;
                if (!scores.contains(output))
                    scores.add(output);
            }
        }
        if(!hasProper)
            System.out.println(word);
        //System.out.println(ProperCount);
        return scores;
    }

    public static String getSVMFeatureVector(String[] parts, int rank)
    {
        String score = "";
        if (parts.length == 3)
            {
                String prefix = parts[0].trim();
                String suffix = parts[2].trim();
                String stem = parts[1].trim();
                // assemble score
                
                double goodies = 0d;
                
                if (probPrefixes.containsKey(prefix))
                    score += "1:" + String.valueOf(Math.log(probPrefixes.get(prefix)));
                else
                    score += "1:-10";
                
                if (probSuffixes.containsKey(suffix))
                    score += " 2:" + String.valueOf(Math.log(probSuffixes.get(suffix)));
                else
                    score += " 2:-10";
                
                String trimmedTemp = suffix.replace("+", "").replace(";", "").replace(",", "");
                String altStem = "";
                if (trimmedTemp.startsWith("ت") && trimmedTemp.length() > 1)
                    altStem = stem + "ة";
                double stemWordCount = -10;
                if (wordCount.containsKey(stem))
                    stemWordCount = wordCount.get(stem);
                else if (altStem.trim().length() > 0 && wordCount.containsKey(altStem)) // && wordCount.get(altStem) > stemWordCount)
                    stemWordCount = wordCount.get(altStem);
                
                if (wordCount.containsKey(stem))
                    score += " 3:" + String.valueOf(stemWordCount);

                if (probPrefixSuffix.containsKey(prefix) && probPrefixSuffix.get(prefix).containsKey(suffix))
                    score += " 4:" + String.valueOf(Math.log(probPrefixSuffix.get(prefix).get(suffix)));
                else
                    score += " 4:-20";
                
                if (probSuffixPrefix.containsKey(suffix) && probSuffixPrefix.get(suffix).containsKey(prefix))
                    score += " 5:" + String.valueOf(Math.log(probSuffixPrefix.get(suffix).get(prefix)));
                else
                    score += " 5:-20";
                
                if (!ft.fitTemplate(stem).equals("Y"))
                {
                    score += " 6:" + String.valueOf(Math.log(generalVariables.get("hasTemplate")));
                    // score += " 6:" + String.valueOf(Math.log(hasTemplate));
                    goodies++;
                }
                else
                {
                    score += " 6:" + String.valueOf(Math.log(1 - generalVariables.get("hasTemplate")));
                    // score += " 6:" + String.valueOf(Math.log(1 - hasTemplate));
                }
                
                if (hmListMorph.containsKey(stem))
                {
                    score += " 7:" + String.valueOf(Math.log(generalVariables.get("inMorphList")));
                    // score += " 7:" + String.valueOf(Math.log(inMorphList));
                    goodies++;
                }
                else
                {
                    score += " 7:" + String.valueOf(Math.log(1 - generalVariables.get("inMorphList")));
                    // score += " 7:" + String.valueOf(Math.log(1 - inMorphList));
                }
                
                if (hmListGaz.containsKey(stem))
                {
                    score += " 8:" + String.valueOf(Math.log(generalVariables.get("inGazList")));
                    // score += " 8:" + String.valueOf(Math.log(inGazList));
                    goodies++;
                }
                else
                {
                    score += " 8:" + String.valueOf(Math.log(1 - generalVariables.get("inGazList")));
                    // score += " 8:" + String.valueOf(Math.log(1 - inGazList));
                }
 
                if (probCondPrefixes.containsKey(prefix))
                    score += " 9:" + String.valueOf(Math.log(probCondPrefixes.get(prefix)));
                else
                    score += " 9:-20";
                
                if (probCondSuffixes.containsKey(suffix))
                    score += " 10:" + String.valueOf(Math.log(probCondSuffixes.get(suffix)));
                else
                    score += " 10:-20";
                
                // get probability with first suffix . for example xT + p would produce xTp 
                String stemPlusFirstSuffix = stem;
                if (suffix.indexOf("+", 1) > 0)
                    stemPlusFirstSuffix += suffix.substring(1, suffix.indexOf("+", 1));
                else
                    stemPlusFirstSuffix += suffix;
                trimmedTemp = stemPlusFirstSuffix.replace("+", "").replace(";", "").replace(",", "");
                stemWordCount = -10;
                if (wordCount.containsKey(stemPlusFirstSuffix))
                    stemWordCount = wordCount.get(stemPlusFirstSuffix);
                else if (stemPlusFirstSuffix.endsWith("ت") && wordCount.containsKey(stemPlusFirstSuffix.substring(0, stemPlusFirstSuffix.length() -1) + "ة"))
                    stemWordCount = wordCount.get(stemPlusFirstSuffix.substring(0, stemPlusFirstSuffix.length() -1) + "ة");
                score += " 11:" + String.valueOf(stemWordCount);
                
                // put template feature
                String template = ft.fitTemplate(stem);
                if (hmTemplateCount.containsKey(template))
                    score += " 12:" + Math.log(hmTemplateCount.get(template));
                else
                    score += " 12:-10";
                
                // how far is the stem length different from average
                
                score += " 13:" + Math.log(Math.abs(stem.length() - generalVariables.get("averageStemLength")));
                // score += " 13:" + Math.log(Math.abs(stem.length() - averageStemLength));
                
                trimmedTemp = suffix.replace("+", "").replace(";", "").replace(",", "");
                altStem = "";
                if (trimmedTemp.startsWith("ت") && trimmedTemp.length() > 1)
                    altStem = stem + "ة";
                
                if (wordCount.containsKey(stem))
                    stemWordCount = wordCount.get(stem);
                else if (altStem.trim().length() > 0 && wordCount.containsKey(altStem)) // && wordCount.get(altStem) > stemWordCount)
                    stemWordCount = wordCount.get(altStem);
                
                if (hmAraLexCom.containsKey(stem))
                {
                    if (wordCount.containsKey(stem))
                        score += " 14:" + wordCount.get(stem);
                    else
                        score += " 14:-10";
                }
                else if (altStem.trim().length() > 0 && hmAraLexCom.containsKey(altStem))
                {
                    if (wordCount.containsKey(altStem))
                        score += " 14:" + wordCount.get(altStem);
                    else
                        score += " 14:-10";
                }
                else
                {
                    score += " 14:-20";
                }
                
                if (hmBuck.containsKey(stem))
                    score += " 15:1";
                else
                    score += " 15:-1";
                
                if (hmLocations.containsKey(stem))
                    score += " 16:1"; // + Math.log((double) hmLocations.get(stem));
                else
                    score += " 16:-1";
                
                if (hmPeople.containsKey(stem))
                    score += " 17:1"; // + Math.log((double) hmLocations.get(stem));
                else
                    score += " 17:-1";
                
                
                if (hmStop.containsKey(stem))
                    score += " 18:1";
                else
                    score += " 18:-1";
                
//		if (wordCountDialect.containsKey(stem))
//                    score += " 19:" + String.valueOf(wordCountDialect.get(stem));
//                else
//                    score += " 19:-20";
		

            }
        return score.trim();
    }
    
    public static ArrayList<String> getAllPossiblePartitionsOfString(String s)
    {
        ArrayList<String> output = new ArrayList<String>();
        if (s.length() > 20)
            output.add(s);
        else
        {
            s = s.trim();
            if (s.length() > 0)
            {
                String fullPartition = s.substring(0, 1);
                for (int i = 1; i < s.length(); i++)
                    fullPartition += "," + s.substring(i, i+1);
                String correctFullPartition = getProperSegmentation(fullPartition.replace(",", "+").replaceAll("\\++", "+"));
                String[] parts = (" " + correctFullPartition + " ").split(";");
                if (!output.contains(correctFullPartition))
                {
                    if (parts[1].length() != 1 || s.length() == 1)
                        output.add(correctFullPartition);
                }
                // output.add(fullPartition);
                if (fullPartition.contains(","))
                    output = getSubPartitions(fullPartition, output);
            }
        }
        return output;
    }
    
    private static ArrayList<String> getSubPartitions(String s, ArrayList<String> output)
    {
        // ArrayList<String> output = new ArrayList<String>();
        if (s.contains(","))
        {
            String[] parts = s.split(",");
            for (int i = 0; i < parts.length - 1; i++)
            {
                String ss = "";
                // construct string with 1 units until i
                for (int j = 0; j < i; j++)
                {
                    if (j == 0)
                        ss = parts[j];
                    else
                        ss += "," + parts[j];
                }
                // put 2 units
                if (i == 0)
                    ss = parts[i] + parts[i+1]; 
                else
                    ss += "," + parts[i] + parts[i+1];
                // put remaining 1 units until end of string
                for (int k = i + 2; k < parts.length; k++)
                {
                    if (k == 0)
                        ss = parts[k];
                    else
                        ss += "," + parts[k];
                }
                if (!output.contains(getProperSegmentation(ss.replace(",", "+").replaceAll("\\++", "+"))))
                {
                    output.add(getProperSegmentation(ss.replace(",", "+").replaceAll("\\++", "+")));
                    if (ss.contains(","))
                        output = getSubPartitions(ss, output);
                }
            }
        }
        return output;
    }
    

    
    private static String getPrefixSplit(String head)
    {
        String output = "";
        if (head.startsWith("و") || head.startsWith("ف"))
        {
            output += head.substring(0, 1) + ",";
            head = head.substring(1);
        }
        if (head.startsWith("ب") || head.startsWith("ك") || head.startsWith("ل") || head.startsWith("س"))
        {
            output += head.substring(0, 1) + ",";
            head = head.substring(1);
        }
        if (head.startsWith("ال"))
        {
            output += head.substring(0, 2) + ",";
        }
        output = output.replaceFirst(",$", "");
        return output;
    }
 
    
    public static String getProperSegmentation(String input)
    {
        if (hPrefixes.isEmpty()) {
            for (int i = 0; i < prefixes.length; i++) {
                hPrefixes.put(prefixes[i].toString(), 1);
            }
        }
        if (hSuffixes.isEmpty()) {
            for (int i = 0; i < suffixes.length; i++) {
                hSuffixes.put(suffixes[i].toString(), 1);
            }
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

        if (currentPrefix.endsWith("س+") && !stemPart.matches("^[ينأت].*"))
        {
            currentPrefix = currentPrefix.substring(0, currentPrefix.length() - 2);
            stemPart = "س" + stemPart;
        }
        output = currentPrefix + ";" + stemPart + ";" + currentSuffix;
        output = output.replaceFirst("^\\+", "");
        output = output.replaceFirst("\\+$", "");
        return output.replace("++", "+");
    }    
    
    public BufferedReader openFileForReading(String filename) throws FileNotFoundException {
        BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
        return sr;
    }

    public BufferedWriter openFileForWriting(String filename) throws FileNotFoundException {
        BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
        return sw;
    }  
    
    private static void populatePossibleAffixes () 
    {
        // populate prefixes
        // if (head.matches("(و|ف)?(ب|ك|ل)?(ال)?") || head.equals("س") || head.equals("وس") || head.equals("فس"))
        
        String[] SetPreA = {"", "و", "ف"};
        String[] SetPreB = {"", "ب", "ك", "ل"};
        String[] SetPreC = {"", "ال"};
        
        for (String a : SetPreA)
        {
            for (String b : SetPreB)
            {
                for (String c : SetPreC)
                {
                    String suf = a + b + c;
                    if (suf.trim().length() > 0)
                        hmValidPrefixes.put(suf, Boolean.TRUE);
                }
            }
        }
        hmValidPrefixes.put("", Boolean.TRUE);
        hmValidPrefixes.put("س", Boolean.TRUE);
        hmValidPrefixes.put("وس", Boolean.TRUE);
        hmValidPrefixes.put("فس", Boolean.TRUE);
        
        String[] SetA1 = {"", "ات", "ون", "ين", "ان"};
        String[] SetA2 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "نا", "ي"};
        
        for (String s : SetA1)
        {
            for (String ss : SetA2)
            {
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetB1 = {"ا", "و", "ي"};
        for (String s : SetB1)
        {
            for (String ss : SetA2)
            {
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetC1 = {"", "ن", "ت", "نا"};
        String[] SetC2 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "كي", "ي"};
        for (String s : SetC1)
        {
            for (String ss : SetC2)
            {
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetD1 = {"ون", "ين", "ات", "ان", "ا", "ي", "و", "ت", "ة", "ن", "وا", "ي", "و", "ا"};
        for (String s : SetD1)
        {
            hmValidSuffixes.put(s, Boolean.TRUE);
        }

        String[] setA = {"ا", "و", "ي", "ين", "ون"};
        String[] setB = {"ن", "نا"};
        String[] setC = {"ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن"};
        for (String a : setA)
        {
            for (String b : setB)
            {
                for (String c : setC)
                {
                    String suf = a + b + c;
                    hmValidSuffixes.put(suf, Boolean.TRUE);
                }
            }
        }
        hmValidSuffixes.put("", Boolean.TRUE);
    }
    
    private static void populatePossibleAffixesSegmented() 
    {
        // populate prefixes
        // if (head.matches("(و|ف)?(ب|ك|ل)?(ال)?") || head.equals("س") || head.equals("وس") || head.equals("فس"))
        
        String[] SetPreA = {"", "و", "ف"};
        String[] SetPreB = {"", "ب", "ك", "ل"};
        String[] SetPreC = {"", "ال"};
        
        for (String a : SetPreA)
        {
            for (String b : SetPreB)
            {
                for (String c : SetPreC)
                {
                    String separator1 = "";
                    String separator2 = "";
                    if (a.length() > 0 && b.length() > 0)
                        separator1 = ",";
                    if (b.length() > 0 && c.length() > 0)
                        separator2 = ",";
                    String suf = a + separator1 + b + separator2 + c;
                    if (suf.trim().length() > 0)
                    {
                        hmValidPrefixesSegmented.put(suf, Boolean.TRUE);
                        hmValidPrefixes.put(suf.replace(",", ""), Boolean.TRUE);
                    }
                }
            }
        }
        hmValidPrefixesSegmented.put("", Boolean.TRUE);
        hmValidPrefixesSegmented.put("س", Boolean.TRUE);
        hmValidPrefixesSegmented.put("و,س", Boolean.TRUE);
        hmValidPrefixesSegmented.put("ف,س", Boolean.TRUE);
        hmValidPrefixes.put("س", Boolean.TRUE);
        hmValidPrefixes.put("وس", Boolean.TRUE);
        hmValidPrefixes.put("فس", Boolean.TRUE);
        
//            p.matches("(ات|ون|ين)?,(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?")   -- done
//            || p.matches("[ويا],(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)?")      -- done
//            || p.matches("(ن|ت),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)")       -- done
//            || p.matches("(نا),(ه|ها|هما|هم|هن|ك|كما|كم|كن|كي|ي)")        -- done
//            || p.matches("(ون|ين|ات|ان|ا|ي|و|ت|ة|ن|وا)")               -- done
//            || p.matches("(ه|ها|هما|هم|هن|ك|كما|كم|كن|نا|ي)")             -- done
//            || p.matches("(ون|ين|ي|و|ا),(نا|ن),(ه|ها|هما|هم|هن|ك|كما|كم|كن)")
        
        String[] SetA1 = {"", "ات", "ون", "ين", "ان"};
        String[] SetA2 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "نا", "ي"};
        
        for (String s : SetA1)
        {
            for (String ss : SetA2)
            {
                String separator = "";
                if (s.trim().length() > 0 && s.trim().length() > 0)
                    separator = ",";
                hmValidSuffixesSegmented.put(s + separator + ss, Boolean.TRUE);
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetB1 = {"ا", "و", "ي"};
        for (String s : SetB1)
        {
            for (String ss : SetA2)
            {
                String separator = "";
                if (s.trim().length() > 0 && s.trim().length() > 0)
                    separator = ",";
                hmValidSuffixesSegmented.put(s + separator + ss, Boolean.TRUE);
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetC1 = {"", "ن", "ت", "نا"};
        String[] SetC2 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "كي", "ي"};
        for (String s : SetC1)
        {
            for (String ss : SetC2)
            {
                String separator = "";
                if (s.trim().length() > 0 && s.trim().length() > 0)
                    separator = ",";
                hmValidSuffixesSegmented.put(s + separator + ss, Boolean.TRUE);
                hmValidSuffixes.put(s + ss, Boolean.TRUE);
            }
        }
        
        String[] SetD1 = {"ون", "ين", "ات", "ان", "ا", "ي", "و", "ت", "ة", "ن", "وا", "ي", "و", "ا"};
        for (String s : SetD1)
        {
            hmValidSuffixesSegmented.put(s, Boolean.TRUE);
        }

        String[] setA = {"ا", "و", "ي", "ين", "ون"};
        String[] setB = {"ن", "نا"};
        String[] setC = {"ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن"};
        for (String a : setA)
        {
            for (String b : setB)
            {
                for (String c : setC)
                {
                    String separator1 = "";
                    String separator2 = "";
                    if (a.length() > 0 && b.length() > 0)
                        separator1 = ",";
                    if (b.length() > 0 && c.length() > 0)
                        separator2 = ",";
                    String suf = a + separator1 + b + separator2 + c;
                    hmValidSuffixesSegmented.put(suf, Boolean.TRUE);
                    hmValidSuffixes.put(suf.replace(",", ""), Boolean.TRUE);
                }
            }
        }
        
        String[] setA3 = {"ت"};
        String[] setB3 = {"", "ا", "ي", "ين", "ان"};
        String[] setC3 = {"", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن"};
        for (String a : setA3)
        {
            for (String b : setB3)
            {
                for (String c : setC3)
                {
                    String separator1 = "";
                    String separator2 = "";
                    if (a.length() > 0 && b.length() > 0)
                        separator1 = ",";
                    if (b.length() > 0 && c.length() > 0)
                        separator2 = ",";
                    String suf = a + separator1 + b + separator2 + c;
                    hmValidSuffixesSegmented.put(suf, Boolean.TRUE);
                    hmValidSuffixes.put(suf.replace(",", ""), Boolean.TRUE);
                }
            }
        }
        hmValidSuffixesSegmented.put("", Boolean.TRUE);
    }
    
}
