/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newarabicsegmenter;

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
import java.util.Map;
import java.util.TreeMap;
import static newarabicsegmenter.ArabicUtils.prefixes;
import static newarabicsegmenter.ArabicUtils.removeDiacritics;
import static newarabicsegmenter.ArabicUtils.suffixes;
import static newarabicsegmenter.ArabicUtils.tokenize;
import static newarabicsegmenter.NewArabicSegmenter.getProperSegmentation;
import static newarabicsegmenter.NewArabicSegmenter.openFileForReading;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 *
 * @author kareemdarwish
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

//        File file = new File(BinDir + "NTBdata.generalVariables.ser");
//        if (file.exists()) {
//            loadStoredData(BinDir);
//        } else 
	{

            generalVariables.put("hasTemplate", 0d);
            generalVariables.put("inMorphList", 0d);
            generalVariables.put("inGazList", 0d);
            generalVariables.put("allWordCount", 0d);
            generalVariables.put("averageStemLength", 0d);

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

            br = openFileForReading(BinDir + "wordcountAJ.arpa");
            line = "";
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2 && parts[0].startsWith("-") && !parts[0].contains("inf")) {
                    wordCount.put(parts[1], Double.parseDouble(parts[0]));
                }
            }
	    
	    br = openFileForReading(BinDir + "DialectTweetsEG.txt.lm");
            line = "";
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) // && parts[0].startsWith("-") && !parts[0].contains("inf")) {
		{
                    wordCountDialect.put(parts[0].trim(), Double.parseDouble(parts[1])/1000000d);
                }
            }
            populatePossibleAffixes();
            populatePossibleAffixesSegmented();

            //BufferedReader brSeenInTraining = openFileForReading(BinDir + "seen-before.txt.new");
            

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
    
//    public Map populateMap(String MapName, HashMap input, DB db)
//    {
//        Map map = db.hashMap(MapName);
//        for (Object s : input.keySet())
//            map.put(s, input.get(s));
//        return map;
//    }
//    
//    public void dumpMapFile(String BinDir, String MapName, HashMap input) throws FileNotFoundException, IOException
//    {
//       BufferedWriter bw = openFileForWriting(BinDir + "NTBdata." + MapName + ".txt");
//       for (Object s : input.keySet())
//            bw.write(s + "\t" + input.get(s) + "\n");
//       bw.close();
//    }
    
    public void serializeMap(String BinDir, String MapName, HashMap input) throws FileNotFoundException, IOException {
        FileOutputStream fos
                = new FileOutputStream(BinDir + "NTBdata." + MapName + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(input);
        oos.close();
        fos.close();
    }
    
    public void storeDataItemsFile(String BinDir) throws FileNotFoundException, IOException
    {
        serializeMap(BinDir, "hmListMorph", hmListMorph);
        serializeMap(BinDir, "hmListGaz", hmListGaz);
        serializeMap(BinDir, "hmAraLexCom", hmAraLexCom);
        serializeMap(BinDir, "hmBuck", hmBuck);
        serializeMap(BinDir, "hmLocations", hmLocations);
        serializeMap(BinDir, "hmPeople", hmPeople);
        serializeMap(BinDir, "hmStop", hmStop);
        serializeMap(BinDir, "hPrefixes", hPrefixes);
        serializeMap(BinDir, "hSuffixes", hSuffixes);
        serializeMap(BinDir, "hmValidSuffixes", hmValidSuffixes);
        serializeMap(BinDir, "hmValidPrefixes", hmValidPrefixes);
        serializeMap(BinDir, "hmTemplateCount", hmTemplateCount);
        serializeMap(BinDir, "hmValidSuffixesSegmented", hmValidSuffixesSegmented);
        serializeMap(BinDir, "hmValidPrefixesSegmented", hmValidPrefixesSegmented);
        serializeMap(BinDir, "wordCount", wordCount);
        serializeMap(BinDir, "probPrefixes", probPrefixes);
        serializeMap(BinDir, "probSuffixes", probSuffixes);
        serializeMap(BinDir, "probCondPrefixes", probCondPrefixes);
        serializeMap(BinDir, "probCondSuffixes", probCondSuffixes);
        serializeMap(BinDir, "seenTemplates", seenTemplates);
        serializeMap(BinDir, "hmPreviouslySeenTokenizations", hmPreviouslySeenTokenizations);
        serializeMap(BinDir, "hmWordPossibleSplits", hmWordPossibleSplits);
        serializeMap(BinDir, "probPrefixSuffix", probPrefixSuffix);
        serializeMap(BinDir, "probSuffixPrefix", probSuffixPrefix);
        serializeMap(BinDir, "generalVariables", generalVariables);
    }
    
    public HashMap deserializeMap(String BinDir, String MapName) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(BinDir + "NTBdata." + MapName + ".ser");
         ObjectInputStream ois = new ObjectInputStream(fis);
         HashMap map = (HashMap) ois.readObject();
         ois.close();
         fis.close();
         return map;
    }
    
    public void loadStoredData(String BinDir) throws IOException, FileNotFoundException, ClassNotFoundException
    {
        hmListMorph = deserializeMap(BinDir, "hmListMorph");
        hmListGaz = deserializeMap(BinDir, "hmListGaz");
        hmAraLexCom = deserializeMap(BinDir, "hmAraLexCom");
        hmBuck = deserializeMap(BinDir, "hmBuck");
        hmLocations = deserializeMap(BinDir, "hmLocations");
        hmPeople = deserializeMap(BinDir, "hmPeople");
        hmStop = deserializeMap(BinDir, "hmStop");
        hPrefixes = deserializeMap(BinDir, "hPrefixes");
        hSuffixes = deserializeMap(BinDir, "hSuffixes");
        hmValidSuffixes = deserializeMap(BinDir, "hmValidSuffixes");
        hmValidPrefixes = deserializeMap(BinDir, "hmValidPrefixes");
        hmTemplateCount = deserializeMap(BinDir, "hmTemplateCount");
        hmValidSuffixesSegmented = deserializeMap(BinDir, "hmValidSuffixesSegmented");
        hmValidPrefixesSegmented = deserializeMap(BinDir, "hmValidPrefixesSegmented");
        wordCount = deserializeMap(BinDir, "wordCount");
        probPrefixes = deserializeMap(BinDir, "probPrefixes");
        probSuffixes = deserializeMap(BinDir, "probSuffixes");
        probCondPrefixes = deserializeMap(BinDir, "probCondPrefixes");
        probCondSuffixes = deserializeMap(BinDir, "probCondSuffixes");
        seenTemplates = deserializeMap(BinDir, "seenTemplates");
//        hmPreviouslySeenTokenizations = deserializeMap(BinDir, "hmPreviouslySeenTokenizations");
        hmWordPossibleSplits = deserializeMap(BinDir, "hmWordPossibleSplits");
        probPrefixSuffix = deserializeMap(BinDir, "probPrefixSuffix");
        probSuffixPrefix = deserializeMap(BinDir, "probSuffixPrefix");
        generalVariables = deserializeMap(BinDir, "generalVariables");
    }
    
    public void storeDataSources(String BinDir)
    {
        /*
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
        */
        
//        File fileDB = new File(BinDir + "database.bin");
//        DB db = DBMaker.fileDB(fileDB).make();
//        Map wordCountMap = populateMap("wordCount", wordCount, db);// db.hashMap("wordCount");
//        Map seenTemplatesMap = populateMap("seenTemplates", wordCount, db);
//        Map probPrefixesMap = populateMap("probPrefixes", wordCount, db);
//        Map probSuffixesMap = populateMap("probSuffixes", wordCount, db);
//        Map probCondPrefixesMap = populateMap("probCondPrefixes", wordCount, db);
//        Map probCondSuffixesMap = populateMap("probCondSuffixes", wordCount, db);
//        /*
//        for (String s : wordCount.keySet())
//            wordCountMap.put(s, wordCount.get(s));
//        */
//        Map generalMap = db.hashMap("general");
//        generalMap.put("hasTemplate", hasTemplate);
//        generalMap.put("inMorphList", inMorphList);
//        generalMap.put("inGazList", inGazList);
//        generalMap.put("allWordCount", allWordCount);
//        generalMap.put("averageStemLength", averageStemLength);
//
//        Map probSuffixPrefixMap = db.hashMap("probSuffixPrefix");
//        for (String s : probSuffixPrefix.keySet())
//        {
//            for (String ss : probSuffixPrefix.get(s).keySet())
//            {
//                probSuffixPrefixMap.put(s + "\t" + ss, probSuffixPrefix.get(s).get(ss));
//            }
//        }
//        
//        Map probPrefixSuffixMap = db.hashMap("probPrefixSuffix");
//        for (String s : probPrefixSuffix.keySet())
//        {
//            for (String ss : probPrefixSuffix.get(s).keySet())
//            {
//                probPrefixSuffixMap.put(s + "\t" + ss, probPrefixSuffix.get(s).get(ss));
//            }
//        }
//        /*
//        Map seenTemplatesMap = db.hashMap("seenTemplates");
//        for (String s : seenTemplates.keySet())
//            seenTemplatesMap.put(s, seenTemplates.get(s));
//        
//        Map probPrefixesMap = db.hashMap("probPrefixes");
//        for (String s : probPrefixes.keySet())
//            probPrefixesMap.put(s, probPrefixes.get(s));
//        
//        Map probSuffixesMap = db.hashMap("probSuffixes");
//        for (String s : probSuffixes.keySet())
//            probSuffixesMap.put(s, probSuffixes.get(s));
//        
//        Map probCondPrefixesMap = db.hashMap("probCondPrefixes");
//        for (String s : probCondPrefixes.keySet())
//            probCondPrefixesMap.put(s, probCondPrefixes.get(s));
//        
//        Map probCondSuffixesMap = db.hashMap("probCondSuffixes");
//        for (String s : probCondSuffixes.keySet())
//            probCondSuffixesMap.put(s, probCondSuffixes.get(s));
//        */
//        db.close();
        
    }
    
    public void train(String filename) throws FileNotFoundException, IOException
    {
        
        BufferedReader brSeenInTraining = openFileForReading(filename);
//        BufferedReader brSeenInTraining = openFileForReading("/home/disooqi/Dropbox/most_cited/final_splits_all_data/egy_seg/splits_msa/data_1.train");//splits_msa
        String line = "";
//            while ((line = brSeenInTraining.readLine()) != null) {
//                String[] parts = line.trim().split("[ \t]+");
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
        //01
        //String[] magicNumbers = "1:-0.10580315 2:-0.088740461 3:0.077059299 4:0.25859663 5:0.24153362 6:0.66420275 7:0.1351631 8:-0.42308339 9:0.010853587 10:0.015022006 11:0.072503455 12:0.048041068 13:-0.22454475 14:0.00022833695 15:0.60401404 16:-0.0037927693 17:0.087168999 18:0.47016838 19:0.02443004".split(" +");
        //02
        //String[] magicNumbers = "1:-0.099900782 2:-0.083594039 3:0.084298708 4:0.26312584 5:0.2468202 6:0.72485822 7:0.1615831 8:-0.43259943 9:0.0011704828 10:0.039322838 11:0.077934824 12:0.059793059 13:-0.26692867 14:0.00735508 15:0.61939043 16:0.063751653 17:0.076067209 18:0.39902639 19:0.019334314".split(" +");
        //03
        String[] magicNumbers = "1:-0.28435925 2:-0.28457156 3:0.14189413 4:0.27611557 5:0.27632973 6:0.81240511 7:0.14667918 8:-0.57018894 9:0.023951685 10:0.02592122 11:0.14230652 12:0.041602667 13:-0.0032382524 14:0.013622358 15:0.36693767 16:-0.18616928 17:0.076464899 18:0.2726813 19:0.019954393".split(" +");
        
        
        //0001
//        magicNumbers = "1:-0.21571277 2:-0.20991312 3:0.2051423 4:0.20882967 5:0.23328149 6:0.71950352 7:0.19665609 8:-0.39861929 9:0.034229096 10:-0.028946545 11:0.038647644 12:0.030969549 13:-0.12673575 14:-0.0077096294 15:0.33157241 16:-0.066908859 17:0.11543112 18:0.31469822 19:0.04353939".split(" +");
//        magicNumbers = "1:-0.25680128 2:-0.2697103 3:0.20105927 4:0.27493861 5:0.28784707 6:0.79656184 7:0.20193176 8:-0.40485862 9:0.032086719 10:-0.0024332972 11:0.031594753 12:0.042559367 13:-0.10464483 14:-0.010301752 15:0.3355248 16:-0.133424 17:0.20124871 18:0.29081494 19:0.037123736".split(" +");
//        magicNumbers = "1:-0.22270747 2:-0.25209215 3:0.20572498 4:0.24204269 5:0.27142832 6:0.85008574 7:0.23175709 8:-0.34143639 9:0.026057065 10:0.0065362393 11:0.034794629 12:0.044098001 13:-0.084338166 14:-0.0083515737 15:0.3017582 16:-0.064436205 17:0.14013317 18:0.28552675 19:0.039601076".split(" +");
//        magicNumbers = "1:-0.26748481 2:-0.28611347 3:0.22353959 4:0.25803655 5:0.27666205 6:0.74093086 7:0.21409595 8:-0.32737529 9:0.039299238 10:-0.014626612 11:0.041762002 12:0.0466072 13:-0.11297122 14:-0.0089037046 15:0.31987393 16:-0.029377533 17:0.14439294 18:0.30519551 19:0.041214898".split(" +");
//        magicNumbers = "1:-0.22077926 2:-0.21313569 3:0.21484175 4:0.20055816 5:0.24277098 6:0.73662651 7:0.17729992 8:-0.36667648 9:0.028889708 10:-0.0081646163 11:0.039849654 12:0.03895703 13:-0.13993913 14:-0.0063961116 15:0.34160244 16:-0.077830404 17:0.14267682 18:0.30316561 19:0.039496131".split(" +");
        
        //0002
//        magicNumbers = "1:-0.11153507 2:-0.11762575 3:0.1967627 4:0.22376296 5:0.22985345 6:0.72555906 7:0.19721882 8:-0.33034024 9:0.0019862717 10:-0.049236648 11:0.0067113629 12:0.066350192 13:-0.26414105 14:-0.015882028 15:0.42332354 16:-0.045852415 17:0.1105827 18:0.23304343 19:0.036284875".split(" +");
//        magicNumbers = "1:-0.15389057 2:-0.17214002 3:0.1793918 4:0.28138295 5:0.29963672 6:0.73499054 7:0.17889675 8:-0.39018324 9:-0.01054108 10:-0.038802344 11:0.0091287838 12:0.083682165 13:-0.30531034 14:-0.0061969385 15:0.3657999 16:-0.10923091 17:0.1387471 18:0.2819517 19:0.033391073".split(" +");
//        magicNumbers = "1:-0.13159706 2:-0.16232267 3:0.20766453 4:0.25465786 5:0.28538272 6:0.75238293 7:0.18117405 8:-0.30139872 9:-0.016000727 10:-0.033432443 11:0.013026919 12:0.094793335 13:-0.29271472 14:-0.007286164 15:0.39176503 16:-0.073861614 17:0.054952368 18:0.23981631 19:0.035436947".split(" +");
//        magicNumbers = "1:-0.17199887 2:-0.18831649 3:0.181915 4:0.27698153 5:0.29330096 6:0.71921676 7:0.18437384 8:-0.34423769 9:0.0041416744 10:-0.037480015 11:0.016591495 12:0.082087979 13:-0.26738316 14:-0.0088789128 15:0.34906241 16:-0.070282519 17:0.092921019 18:0.24628304 19:0.043696284".split(" +");
//        magicNumbers = "1:-0.13067925 2:-0.15915695 3:0.19139385 4:0.23053277 5:0.25900722 6:0.71257216 7:0.16574204 8:-0.29092476 9:-0.0033085749 10:-0.015633279 11:0.012214513 12:0.11343364 13:-0.29807383 14:-0.0097566852 15:0.38362926 16:0.00088238454 17:0.026235811 18:0.27471191 19:0.034878012".split(" +");

        //0003
//        magicNumbers = "1:-0.19933014 2:-0.21579584 3:0.19297229 4:0.25759465 5:0.2740607 6:1.0116674 7:0.36391011 8:-0.40111339 9:0.027088914 10:0.018584613 11:0.07303486 12:0.063213401 13:-0.048406005 14:-0.0043268995 15:0.38973281 16:-0.13313314 17:0.12159327 18:-0.0052605346 19:0.034647953".split(" +");
//        magicNumbers = "1:-0.28117937 2:-0.29516384 3:0.17991099 4:0.30887184 5:0.32285228 6:0.96266532 7:0.37305573 8:-0.42984658 9:0.041920792 10:0.033059616 11:0.094185986 12:0.070340253 13:-0.020427836 14:0.0050978083 15:0.32801148 16:-0.16753505 17:0.088383481 18:0.031503141 19:0.032591477".split(" +");
//        magicNumbers = "1:-0.21054803 2:-0.24592133 3:0.18114747 4:0.26794171 5:0.30331224 6:1.0850476 7:0.36515158 8:-0.40946582 9:0.022882689 10:0.029433874 11:0.090723179 12:0.073316224 13:-0.023256274 14:0.0031345244 15:0.34257439 16:-0.14230955 17:0.097351737 18:0.067692585 19:0.032941289".split(" +");
//        magicNumbers = "1:-0.27118227 2:-0.29039207 3:0.16389696 4:0.28652173 5:0.30573192 6:0.96684283 7:0.34254825 8:-0.44552842 9:0.044460643 10:0.036499228 11:0.11281139 12:0.062235981 13:-0.024039244 14:0.0019590463 15:0.34992468 16:-0.13504247 17:0.080915555 18:0.10489612 19:0.031799328".split(" +");
//        magicNumbers = "1:-0.25791878 2:-0.28021234 3:0.18958804 4:0.29707438 5:0.31936619 6:0.94615752 7:0.31776467 8:-0.42616463 9:0.032165706 10:0.031327121 11:0.08480031 12:0.045621615 13:-0.0432599 14:0.004657276 15:0.34716049 16:-0.064533994 17:0.038707118 18:0.078888856 19:0.033369105".split(" +");
        
        //0004
//        magicNumbers = "1:-0.079021707 2:-0.11320221 3:0.17743167 4:0.18134558 5:0.2155284 6:0.99272168 7:0.31538957 8:-0.36277112 9:-0.0038206193 10:0.021434829 11:0.070921868 12:0.054772936 13:-0.11918109 14:0.0011954845 15:0.40003204 16:-0.15438877 17:0.051610321 18:-0.010049785 19:0.03293879".split(" +");
//        magicNumbers = "1:-0.12198222 2:-0.15493856 3:0.17000893 4:0.22646391 5:0.25941685 6:0.98119086 7:0.32983592 8:-0.3892093 9:-0.0070017003 10:0.016147008 11:0.057419207 12:0.07253617 13:-0.10954463 14:0.005325499 15:0.33950618 16:-0.1512516 17:0.09453626 18:0.0079896189 19:0.025623357".split(" +");
//        magicNumbers = "1:-0.10255302 2:-0.13797282 3:0.15740195 4:0.18878487 5:0.22420186 6:0.97836453 7:0.31721431 8:-0.39141414 9:0.0018481226 10:0.022480763 11:0.080763556 12:0.072480679 13:-0.083223388 14:0.0045155315 15:0.37706074 16:-0.15314259 17:0.086717591 18:0.054815859 19:0.027132358".split(" +");
//        magicNumbers = "1:-0.090303995 2:-0.11172363 3:0.16945478 4:0.18122007 5:0.20263717 6:0.97702187 7:0.32415992 8:-0.36863047 9:0.0053536147 10:0.01146345 11:0.084960103 12:0.063711166 13:-0.098205507 14:-0.00045570161 15:0.41749483 16:-0.11321922 17:0.11614762 18:0.078584589 19:0.027699476".split(" +");
//        magicNumbers = "1:-0.12282208 2:-0.14155269 3:0.16770905 4:0.20574537 5:0.22447914 6:0.88560849 7:0.2400009 8:-0.35444084 9:0.0042926469 10:0.016266601 11:0.063197188 12:0.056455821 13:-0.17230979 14:0.0068380367 15:0.38539854 16:-0.082654208 17:0.054936621 18:0.076459892 19:0.028055865".split(" +");

          //All EGY data for cross evaluation
        magicNumbers = "1:-0.24197564 2:-0.26524594 3:0.20340557 4:0.28133601 5:0.30460429 6:0.96617687 7:0.36424249 8:-0.43708181 9:0.031821895 10:0.030390698 11:0.080044061 12:0.062318981 13:-0.039972868 14:-0.0031073818 15:0.38475451 16:-0.10728325 17:0.14480034 18:0.054951381 19:0.02687043".split(" +");

        ArrayList<Double> magicNo = new ArrayList<Double>();
        for (String m : magicNumbers) {
            magicNo.add(Double.parseDouble(m.substring(m.indexOf(":") + 1)));
        }

        if (probPrefixes.containsKey(prefix)) {
            score += magicNo.get(0) * Math.log(probPrefixes.get(prefix));
        } else {
            score += magicNo.get(0) * -10;
        }

        if (probSuffixes.containsKey(suffix)) {
            score += magicNo.get(1) * Math.log(probSuffixes.get(suffix));
        } else {
            score += magicNo.get(1) * -10;
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
        score += magicNo.get(2) * stemWordCount;
                //else
        //    score += 0.19470689 * -10;

        if (probPrefixSuffix.containsKey(prefix) && probPrefixSuffix.get(prefix).containsKey(suffix)) {
            score += magicNo.get(3) * Math.log(probPrefixSuffix.get(prefix).get(suffix));
        } else {
            score += magicNo.get(3) * -20;
        }

        if (probSuffixPrefix.containsKey(suffix) && probSuffixPrefix.get(suffix).containsKey(prefix)) {
            score += magicNo.get(4) * Math.log(probSuffixPrefix.get(suffix).get(prefix));
        } else {
            score += magicNo.get(4) * -20;
        }

        if (!ft.fitTemplate(stem).equals("Y")) {
            score += magicNo.get(5) * Math.log(generalVariables.get("hasTemplate"));
            // score += magicNo.get(5) * Math.log(hasTemplate);
        } else {
            score += magicNo.get(5) * Math.log(1 - generalVariables.get("hasTemplate"));
            // score += magicNo.get(5) * Math.log(1 - hasTemplate);
        }

        if (hmListMorph.containsKey(stem) || (stem.endsWith("ي") && hmListMorph.containsKey(stem.substring(0, stem.length() - 1) + "ى"))) {
            score += magicNo.get(6) * Math.log(generalVariables.get("inMorphList"));
            // score += magicNo.get(6) * Math.log(inMorphList);
        } else {
            score += magicNo.get(6) * Math.log(1 - generalVariables.get("inMorphList"));
            // score += magicNo.get(6) * Math.log(1 - inMorphList);
        }

        if (hmListGaz.containsKey(stem) || (stem.endsWith("ي") && hmListGaz.containsKey(stem.substring(0, stem.length() - 1) + "ى"))) {
            score += magicNo.get(7) * Math.log(generalVariables.get("inGazList"));
            // score += magicNo.get(7) * Math.log(inGazList);
        } else {
            score += magicNo.get(7) * Math.log(1 - generalVariables.get("inGazList"));
            // score += magicNo.get(7) * Math.log(1 - inGazList);
        }

        if (probCondPrefixes.containsKey(prefix)) {
            score += magicNo.get(8) * Math.log(probCondPrefixes.get(prefix));
        } else {
            score += magicNo.get(8) * -20;
        }

        if (probCondSuffixes.containsKey(suffix)) {
            score += magicNo.get(9) * Math.log(probCondSuffixes.get(suffix));
        } else {
            score += magicNo.get(9) * -20;
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
        score += magicNo.get(10) * stemWordCount;
        
        // put template feature
        String template = ft.fitTemplate(stem);
        if (hmTemplateCount.containsKey(template))
            score += magicNo.get(11) * Math.log(hmTemplateCount.get(template));
        else
            score += magicNo.get(11) * -10;
        
        // difference from average length
        score += magicNo.get(12) * Math.log(Math.abs(stem.length() - generalVariables.get("averageStemLength")));
        // score += magicNo.get(12) * Math.log(Math.abs(stem.length() - averageStemLength));
        
        
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
                score += magicNo.get(13) *  wordCount.get(stem);
            else
                score += magicNo.get(13) * -10;
        }
        else if (stem.endsWith("ي") && hmAraLexCom.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
        {
            if (wordCount.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
                score += magicNo.get(13) *  wordCount.get(stem.substring(0, stem.length() - 1) + "ى");
            else
                score += magicNo.get(13) * -10;
        }
        else if (altStem.trim().length() > 0 && hmAraLexCom.containsKey(altStem))
        {
            if (wordCount.containsKey(altStem))
                score += magicNo.get(13) * wordCount.get(altStem);
            else
                score += magicNo.get(13) * -10;
        }
        else
        {
            score += magicNo.get(13) * -20;
        }
        
        if (hmBuck.containsKey(stem))
        {
            score += magicNo.get(14);
        }
        else if (stem.endsWith("ي") && hmBuck.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
        {
            score += magicNo.get(14);
        }
        else
        {
            score += -1 * magicNo.get(14);
        }
        
        if (hmLocations.containsKey(stem))
        {
            score += magicNo.get(15);
        }
        else
        {
            score += -1 * magicNo.get(15);
        }
        
        if (hmPeople.containsKey(stem))
        {
            score += magicNo.get(16);
        }
        else
        {
            score += -1 * magicNo.get(16);
        }
        
        if (hmStop.containsKey(stem))
        {
            score += magicNo.get(17);
        }
        else if (stem.endsWith("ي") && hmStop.containsKey(stem.substring(0, stem.length() - 1) + "ى"))
        {
            score += magicNo.get(17);
        }
        else
        {
            score += -1 * magicNo.get(17);
        }
	
	if (wordCountDialect.containsKey(stem))
	    score += magicNo.get(18) * wordCountDialect.get(stem);
	else
	    score += magicNo.get(18) * -20d;
	
        return score;
    }
    
    public ArrayList<String> segmentLine(String line) throws IOException
    {
        ArrayList<String> output = new ArrayList<String>();
        ArrayList<String> words = tokenize(removeDiacritics(line));
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
//            if (hmAraLexCom.containsKey(stem))
//                agg++;
//            if (hmListGaz.containsKey(stem))
//                agg++;
//            if (!ft.fitTemplate(stem).equals("Y"))
//                agg++;
//            if (hmListGaz.containsKey(stem))
//                agg++;
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
            //else if (stems.get(pp) == minAgg)
            //    features = getSVMFeatureVector(parts, -1);
            //else
            //    features = getSVMFeatureVector(parts, 0);
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
                
                //if (wordCount.containsKey(stem))
                    score += " 3:" + String.valueOf(stemWordCount);
                //else
                //    score += 0.19470689 * -10;
                
//                if (wordCount.containsKey(stem))
//                    score += " 3:" + String.valueOf(wordCount.get(stem));
//                else
//                    score += " 3:-10";
                
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
                /*
                if (hmListMorph.containsKey(stem) && wordCount.containsKey(stem))
                    score += " 7:" + String.valueOf(stemWordCount);
                else
                    score += " 7:-10";
                
                if (hmListGaz.containsKey(stem) && wordCount.containsKey(stem))
                    score += " 8:" + String.valueOf(stemWordCount);
                else
                    score += " 8:-10";
                */
                
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
                
		if (wordCountDialect.containsKey(stem))
                    score += " 19:" + String.valueOf(wordCountDialect.get(stem));
                else
                    score += " 19:-20";
		
                /*
                if (hmPeople.containsKey(stem))
                    score += " 17:1"; // + Math.log((double) hmLocations.get(stem));
                else
                    score += " 17:-1";
                */
                // score += " 15:" + String.valueOf(rank);
                
//                if (seenTemplates.containsKey((prefix + template + suffix).replace(";", "").replace("++", "+").trim()))
//                    score += " 14:" + Math.log(seenTemplates.get((prefix + template + suffix).replace(";", "").replace("++", "+")));
//                else
//                    score += " 14:-20";
//                
//                
//                if (hmAraLexCom.containsKey(stem))
//                    goodies++;
//                
//                score += " 15:" + String.valueOf(goodies); 
                
//                score += " 13:" + Math.log((double)template.length());
                
//                if (!template.equals("Y")) // prefer segmentations generating valid templates
//                    score += " 13:1";
//                else
//                    score += " 13:0";
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
    
//    public static ArrayList<String> findAllPossibleSplits(String input, HashMap<String, Integer> list1, HashMap<String, Integer> list2)
//    {
//        if (hmWordPossibleSplits.containsKey(input))
//            return hmWordPossibleSplits.get(input);
//
//        ArrayList<String> possibleSplits = possibleSplits = new ArrayList<String>();
//        
//        if (list1.containsKey(input) || list2.containsKey(input))
//            possibleSplits.add(input);
//        
//        for (int j = 1; j < input.length(); j++)
//        {
//            String head = input.substring(0, j);
//            String trail = input.substring(j);
//
//            if (checkIfLeadingLettersCouldBePrefixes(head)) {
//                // get prefix split
//                String prefixSplits = getPrefixSplit(head);
//                // check if the rest is stem + suffixes
//                if (trail.length() >= 2) {
//                    for (int i = 0; i <= trail.length(); i++) {
//                        String tok = trail.substring(0, i);
//                        String remain = trail.substring(i);
//                        String key = "";
//                        if (tok.length() > 0)
//                        {
//                            if (remain.trim().length() == 0)
//                            {
//                                if (tok.endsWith("ة"))
//                                    key = prefixSplits + ";" + tok.substring(0, tok.length() - 1) + ";" + "ة";
//                                else
//                                    key = prefixSplits + ";" + tok + ";";
//                            }
//                            else
//                                key = prefixSplits + ";" + tok + ";" + checkIfRemainingLettersCouldBeSuffixesString(remain);
//                        }
//                        else
//                        {
//                            if (remain.trim().length() == 0)
//                                key = prefixSplits;
//                            else
//                                key = prefixSplits + ";;" + checkIfRemainingLettersCouldBeSuffixesString(remain);
//                        }
//                        if ((list1.containsKey(tok) || list2.containsKey(tok)) && 
//                                (checkIfRemainingLettersCouldBeSuffixes(remain) || remain.trim().length() == 0) &&
//                                !possibleSplits.contains(key)
//                                ) {
//                            possibleSplits.add(key);
//                        }
//                    }
//                }
//            }
//            else if (checkIfRemainingLettersCouldBeSuffixes(trail))
//            {
//                // check if rest is prefixes + stem
//                if (head.length() >= 2)
//                {
//                    for (int i = 0; i <= head.length(); i++)
//                    {
//                        if (i == 0 && (list1.containsKey(head) || list2.containsKey(head))
//                                && !possibleSplits.contains(head + ";" + checkIfRemainingLettersCouldBeSuffixesString(trail))
//                                )
//                            possibleSplits.add(";" + head + ";" + checkIfRemainingLettersCouldBeSuffixesString(trail));
//                        else
//                        {
//                            String prefix = head.substring(0, i);
//                            String tok = head.substring(i);
//                            String key = "";
//                            if (tok.length() > 0)
//                                key = getPrefixSplit(prefix) + ";" + tok + ";" + checkIfRemainingLettersCouldBeSuffixesString(trail);
//                            else
//                                key = getPrefixSplit(prefix) + ";;" + checkIfRemainingLettersCouldBeSuffixesString(trail);
//                            if ((list1.containsKey(tok) || list2.containsKey(tok)) 
//                                    && checkIfLeadingLettersCouldBePrefixes(prefix) 
//                                    && !possibleSplits.contains(key))
//                                possibleSplits.add(key);
//                        }
//                    }
//                }
//            }
//        }
//        hmWordPossibleSplits.put(input, possibleSplits);
//        return possibleSplits;
//    }
//    
//        private static boolean checkIfLeadingLettersCouldBePrefixes(String head)
//    {
//        if (head.matches("(و|ف)?(ب|ك|ل)?(ال)?") || head.equals("س") || head.equals("وس") || head.equals("فس"))
//            return true;
//        else
//            return false;
//    }
    
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
  /*  
    private static boolean checkIfRemainingLettersCouldBeSuffixes(String trail)
    {
        if (hmValidSuffixes.containsKey(trail))
            return true;
        else
            return false;
    }
    
    private static String checkIfRemainingLettersCouldBeSuffixesString(String trail)
    {
        String output = "notFound";
        if (!checkIfRemainingLettersCouldBeSuffixes(trail))
            return output;
        ArrayList<String> parts = getAllPossiblePartitionsOfString(trail);
        for (String p : parts)
        {
            if (hmValidSuffixesSegmented.containsKey(p))
                output = p;
        }
        return output;
    }
    
  
    */
    
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
