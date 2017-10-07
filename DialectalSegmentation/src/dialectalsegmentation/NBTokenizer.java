/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package levdialsegmenter;

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

import static levdialsegmenter.ArabicUtils.prefixes;
import static levdialsegmenter.ArabicUtils.suffixes;

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

    /**
     *
     * @param BinDir
     */
    public NBTokenizer(String BinDir) throws FileNotFoundException, IOException, ClassNotFoundException {
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

            hPrefixes.put("يآآ", 1);
            hPrefixes.put("يا", 1); hPrefixes.put("شو", 1);
	    hPrefixes.put("عن", 1);
            hPrefixes.put("عم", 1);
	    hPrefixes.put("مو", 1);
	    hPrefixes.put("ال", 1);
            hPrefixes.put("لا", 1);
            hPrefixes.put("ما", 1);
            hPrefixes.put("شاء", 1);
	    hPrefixes.put("ب", 1);hPrefixes.put("ك", 1);
            hPrefixes.put("ح", 1);
            hPrefixes.put("ف", 1); 
            hPrefixes.put("ل", 1);
            hPrefixes.put("ه", 1);
            hPrefixes.put("م", 1);
            hPrefixes.put("ش", 1);//وان+ش+الله

            hPrefixes.put("ع", 1);
	    hPrefixes.put("و", 1);


            
            
//	    for (int i = 0; i < suffixes.length; i++) {
//                hSuffixes.put(suffixes[i].toString(), 1);
//            }
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
            hSuffixes.put("كن", 1);hSuffixes.put("اش", 1);
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
//            BufferedReader brSeenInTraining = openFileForReading("/home/disooqi/Dropbox/most_cited/final_splits_all_data/lev_seg/splits/lev_trainfold_01_400K_LDC");//_400K_LDC
            BufferedReader brSeenInTraining = openFileForReading(filename);
            String line = "";
//            while ((line = brSeenInTraining.readLine()) != null) {
//                String[] parts = line.trim().split("[ \t]+");
//                
////                String[] words1 = line.trim().split("\t+");            
////                if(words1.length != 2)
////                    continue;
////            
////                String[] parts = words1[1].split(" +");
//                
//                for(String w : parts){
//                //if (parts.length == 2 && Integer.parseInt(parts[0]) > 3) {
//                    String word = ArabicUtils.buck2utf8(w).replace("+", "");
//                    String segmented = ArabicUtils.buck2utf8(w);
//                    segmented = getProperSegmentation(segmented);
//                    if (!hmPreviouslySeenTokenizations.containsKey(word)) {
//                        hmPreviouslySeenTokenizations.put(word, new ArrayList<String>());
//                    }
//                    ArrayList<String> list = hmPreviouslySeenTokenizations.get(word);
//                    if (!list.contains(segmented)) {
//                        list.add(segmented);
//                    }
//                    hmPreviouslySeenTokenizations.put(word, list);
//                }
//            }
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
        
	// assemble score
        //1001-f01
        String[] magicNumbers = "1:-0.18136451 2:-0.096770152 4:0.26447585 5:0.17987235 6:0.54092056 7:0.45544985 8:-0.28599074 9:0.073613904 10:0.02684343 12:-0.022378482 13:-0.31587464 14:0.023938039 15:0.55931789 16:-0.034176923 17:0.071193345 18:0.40927854".split(" +");
//          1007
//            magicNumbers = "1:-0.11201438 2:-0.09603402 3:0.03808846 4:0.21376054 5:0.19777887 6:0.66583705 7:0.52830875 8:-0.31710994 9:0.044607047 10:0.026114356 11:0.088204347 12:0.0070737074 13:-0.17143866 14:0.028354323 15:0.49028608 16:-0.0030623029 17:0.020530591 18:0.35793689".split(" +");
//            magicNumbers = "1:-0.13851731 2:-0.12025294 3:0.030727055 4:0.24650533 5:0.22824511 6:0.64921129 7:0.62817979 8:-0.34399596 9:0.038798004 10:0.00057327532 11:0.11962733 12:0.031827051 13:-0.14712842 14:0.043629888 15:0.44180113 16:-0.038632512 17:0.047191467 18:0.43017295".split(" +");
//            magicNumbers = "1:-0.11300084 2:-0.1094825 3:0.043556053 4:0.24495248 5:0.24143033 6:0.69577295 7:0.58082712 8:-0.24655148 9:0.029380994 10:0.011749763 11:0.081584439 12:0.013045982 13:-0.19905761 14:0.035862867 15:0.50155586 16:-0.038747076 17:-0.019030258 18:0.36342409".split(" +");
//            magicNumbers = "1:-0.13189864 2:-0.12496611 3:0.049716055 4:0.22708693 5:0.22016013 6:0.64118177 7:0.63194293 8:-0.28079367 9:0.038146146 10:0.021058032 11:0.10275575 12:0.013221888 13:-0.1396105 14:0.03277206 15:0.47841868 16:0.021675743 17:0.039849207 18:0.39060026".split(" +");
//            magicNumbers = "1:-0.140284 2:-0.12968928 3:0.039128695 4:0.24837786 5:0.23778304 6:0.63422364 7:0.55069262 8:-0.34885728 9:0.036022641 10:0.007913813 11:0.089587383 12:0.0095606577 13:-0.20209898 14:0.037107207 15:0.51260394 16:-0.029763151 17:0.046322163 18:0.30389559".split(" +");
          //1008
//          magicNumbers = "1:-0.12466442 2:-0.11194593 3:0.033030514 4:0.22430831 5:0.2115885 6:0.52129644 7:0.37797445 8:-0.18836193 9:0.042571377 10:0.024694247 11:0.074533127 12:0.038050991 13:-0.28532165 14:0.035189167 15:0.55532515 16:0.0050145476 17:0.011217862 18:0.31525108".split(" +");
//          magicNumbers = "1:-0.13748971 2:-0.1173212 3:0.035091497 4:0.24097705 5:0.22081362 6:0.45756364 7:0.52455068 8:-0.25251031 9:0.03848736 10:0.014545348 11:0.089375466 12:0.05089432 13:-0.32846057 14:0.038398612 15:0.50838232 16:-0.057941683 17:0.028324066 18:0.42267945".split(" +");
//          magicNumbers = "1:-0.10036792 2:-0.079700992 3:0.035961471 4:0.23297697 5:0.21231277 6:0.56293386 7:0.41401091 8:-0.15856783 9:0.032987826 10:0.0098666884 11:0.045242812 12:0.046858091 13:-0.2657949 14:0.04423603 15:0.50477439 16:-0.006153679 17:-0.008808678 18:0.34183514".split(" +");
//          magicNumbers = "1:-0.12488493 2:-0.11221481 3:0.041993335 4:0.23661521 5:0.22394785 6:0.4590072 7:0.44013464 8:-0.21625555 9:0.031255659 10:0.0192865 11:0.055604916 12:0.03615284 13:-0.26783785 14:0.036713786 15:0.54751438 16:-0.016863722 17:0.036376238 18:0.33288422".split(" +");
//          magicNumbers = "1:-0.08769691 2:-0.078284487 3:0.039924789 4:0.19731328 5:0.18789539 6:0.53005069 7:0.3994258 8:-0.30144671 9:0.029757842 10:0.016301263 11:0.079372741 12:0.064664312 13:-0.25132194 14:0.028071295 15:0.52844751 16:-0.020134123 17:0.01171884 18:0.28684607".split(" +");

        //1010
//          magicNumbers = "1:-0.12219857 2:-0.10726891 3:0.027558057 4:0.21295048 5:0.19802122 6:0.70019805 7:0.57043326 8:-0.29961559 9:0.038474653 10:0.026790608 11:0.088220611 12:-0.024366358 13:-0.26178855 14:0.034639247 15:0.47691646 16:0.011559364 17:0.0034201529 18:0.3224667".split(" +");
//          magicNumbers = "1:-0.14492702 2:-0.1095297 3:0.043282416 4:0.23453984 5:0.19914193 6:0.70648801 7:0.65548366 8:-0.35003135 9:0.04444309 10:0.01425443 11:0.086359143 12:0.019408545 13:-0.29802835 14:0.036818534 15:0.45500994 16:-0.041353308 17:-6.666837e-05 18:0.41570491".split(" +");
//          magicNumbers = "1:-0.13475008 2:-0.11591488 3:0.036794759 4:0.22932342 5:0.21048415 6:0.73286009 7:0.63733232 8:-0.25390851 9:0.039690293 10:0.025011662 11:0.075184479 12:-0.013784353 13:-0.26781216 14:0.032011848 15:0.52290595 16:0.011150068 17:-0.005499783 18:0.40601325".split(" +");
//          magicNumbers = "1:-0.1180022 2:-0.1069712 3:0.038421992 4:0.18961303 5:0.17858709 6:0.65884495 7:0.62923342 8:-0.30457106 9:0.047124196 10:0.029670751 11:0.10703526 12:0.0068369634 13:-0.2470295 14:0.031777132 15:0.49212742 16:-0.0090701561 17:0.031876411 18:0.37905169".split(" +");
//          magicNumbers = "1:-0.12841913 2:-0.10419119 3:0.039714746 4:0.22256498 5:0.19833778 6:0.70369232 7:0.58759242 8:-0.36356315 9:0.041369218 10:0.024288183 11:0.093115129 12:-0.0018091738 13:-0.33208725 14:0.033870049 15:0.52021796 16:-0.01564071 17:0.02277481 18:0.38171768".split(" +");
          
          //1011
//          magicNumbers = "1:-0.11016525 2:-0.091760978 3:0.027401105 4:0.20772502 5:0.18932158 6:0.55001545 7:0.40873203 8:-0.16775623 9:0.042459734 10:0.019697206 11:0.067321755 12:0.033509865 13:-0.26427722 14:0.035804488 15:0.54558134 16:-0.047271956 17:7.2417279e-05 18:0.30247974".split(" +");
//          magicNumbers = "1:-0.14204806 2:-0.11895332 3:0.046907496 4:0.2464733 5:0.22338003 6:0.51773137 7:0.52392167 8:-0.2170839 9:0.04021287 10:0.010010667 11:0.095326237 12:0.067293093 13:-0.22701304 14:0.036975197 15:0.47910103 16:-0.060184408 17:0.038115568 18:0.37584111".split(" +");
//          magicNumbers = "1:-0.092223376 2:-0.09565603 3:0.03827171 4:0.2081141 5:0.21154968 6:0.59294647 7:0.41171804 8:-0.13473079 9:0.027019924 10:0.017340267 11:0.062286165 12:0.04561954 13:-0.20493193 14:0.037581813 15:0.48388961 16:-0.0059642801 17:-0.016801184 18:0.31600967".split(" +");
//          magicNumbers = "1:-0.10836497 2:-0.10123647 3:0.041575447 4:0.20169932 5:0.19456568 6:0.55710852 7:0.48036274 8:-0.20389757 9:0.039728835 10:0.020711266 11:0.095933259 12:0.05454294 13:-0.20383689 14:0.033913855 15:0.50742066 16:-0.00072981667 17:0.049230564 18:0.3610723".split(" +");
//          magicNumbers = "1:-0.12841913 2:-0.10419119 3:0.039714746 4:0.22256498 5:0.19833778 6:0.70369232 7:0.58759242 8:-0.36356315 9:0.041369218 10:0.024288183 11:0.093115129 12:-0.0018091738 13:-0.33208725 14:0.033870049 15:0.52021796 16:-0.01564071 17:0.02277481 18:0.38171768".split(" +");
            
            //All LEV data for cross evaluation
            magicNumbers = "1:-0.12566461 2:-0.11801316 3:0.0369584 4:0.21676169 5:0.20911005 6:0.6704613 7:0.61908847 8:-0.31368199 9:0.039943576 10:0.020444255 11:0.096523792 12:-0.0052463589 13:-0.32781062 14:0.035741873 15:0.49685854 16:0.015622528 17:0.040808827 18:0.38001084".split(" +");
          





        //String[] magicNumbers = "1:-0.28435925 2:-0.28457156 3:0.14189413 4:0.27611557 5:0.27632973 6:0.81240511 7:0.14667918 8:-0.57018894 9:0.023951685 10:0.02592122 11:0.14230652 12:0.041602667 13:-0.0032382524 14:0.013622358 15:0.36693767 16:-0.18616928 17:0.076464899 18:0.2726813 19:0.019954393".split(" +");
        
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
