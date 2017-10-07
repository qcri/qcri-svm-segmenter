/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maghdialsegmenter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author disooqi
 */
public class MaghSegmenter {
    private static HashMap<String, String> hmFunctionWords = new HashMap<String, String>();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, InterruptedException {
        String mode = "test";
        // TODO code application logic here
        String inputDir = "/home/disooqi/Dropbox/most_cited/POSandNERDataArz__/";
        double acc = 0.0;
        
        NBTokenizer nbt = new NBTokenizer(inputDir);
//        nbt.train("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/joint/magh_400K_LDC");
        nbt.train("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/joint/magh_1.train");
        if(mode == "test"){
            loadFunctionWords(inputDir);
            acc += scoreTestFile("/home/disooqi/Dropbox/most_cited/new_data/magh_seg/magh_1.test.trg", nbt);
//            acc += scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/joint/selectedAs_4001_5_magh", nbt);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/egy.trg", nbt);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/lev.trg", nbt);
//            scoreTestFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/glf.trg", nbt);
        }else{ //train
            nbt.writeOutSVMRankFeatureFile("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/magh.trg", "/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/magh.svm");
//            nbt.writeOutSVMRankFeatureFile("/home/disooqi/Dropbox/most_cited/new_data/magh_seg/data_5.train.svm", "/home/disooqi/Dropbox/most_cited/new_data/magh_seg/data_5.train.svm.ft");
        }
        
//        System.out.println("The average acc of 5 folds is "+ acc/5);
        //scoreTestFile("/home/disooqi/Dropbox/most_cited/egy_seg/abdelali/test_utf8.txt", nbt);
    }
    
     private static double scoreTestFile(String filename, NBTokenizer nb) throws FileNotFoundException, IOException, ClassNotFoundException
    {
	BufferedReader br = openFileForReading(filename);
	String line = "";
	double correct = 0;
	double total = 0;
        //Farasa segmenter = new Farasa();
        BufferedWriter bf =  openFileForWriting("/home/disooqi/Dropbox/most_cited/final_splits_all_data/joint/xxx");
        boolean base_line_mode = false;

	while ((line = br.readLine()) != null)
        {
            String[] words = line.trim().split(" +");
//            String[] words1 = line.trim().split("\t+");
//            
//            if(words1.length != 2){
//                //System.err.println(line);
//                continue;
//            }
            
//            String[] words = new String[1];
//            words[0] = words1[1];
            
//	    String[] words = words1[1].split(" +");
            
	    for (String w : words)
	    {
                if (w.startsWith("@") || w.startsWith("http") || w.contains("/") || w.startsWith("#")){
                    bf.write(w);
                    bf.write('\n');
                }
                
		if (!w.startsWith("@") && !w.startsWith("http") && !w.contains("/") && !w.startsWith("#")) //&& !w.contains("Q")&& !w.contains("c") && !w.contains("O")&& !w.contains("C")&& !w.contains("V")
		{
		    String s = w.replace("+", "");
                    
//		    s = ArabicUtils.buck2utf8(s);
//                    s = ArabicUtils.removeDiacritics(s);
//                    String s2 = ArabicUtils.buck2utf8(s);
                    
                    if(base_line_mode){
                        if(s.equals(w))
                            correct++;
                        total++;
                        continue;
                    }
//                    if (s.equals("Serious"))
//                            System.out.println("heeeere");
                    //System.err.println(ArabicUtils.buck2utf8(s));
                    if(w.equals("و+ع+ال+فاضي"))
                        System.out.println("vvvvvvvvvvvvvvvv");
//                    ArrayList<String> possiblePartitions = nb.getAllPossiblePartitionsOfString(s2);
//                    for(String seg : possiblePartitions){
//                        
//                    }
		    if (hmFunctionWords.containsKey(s))
		    {
			s = hmFunctionWords.get(s);//.keySet().iterator().next();
		    }
		    else
		    {
			TreeMap<Double, String> sol = nb.mostLikelyPartition(s, 1);
			if (sol.size() > 0)
			{
                            if(sol.size()>1){
                                System.out.println("vvvvvvvvvvvvvvvv");
                            }
			    for (double d : sol.keySet())
				s = sol.get(d);
			}
		    }
		    s = s.trim().replace(";", "");
                    
//                    ArrayList<String> ss = segmenter.segmentLine(s);
//                    s = ss.get(0);
		    w = w.trim();
		    if (s.startsWith("+"))
			s = s.substring(1);
		    if (s.endsWith("+"))
			s = s.substring(0, s.length() - 1);
                    if (s.startsWith("ال") && s.length() > 5)
                        if (!s.startsWith("ال+"))
                            s = s.replace("ال", "ال+");
                    
                    if (s.endsWith("ة") && !s.endsWith("+ة")){
                        s=s.substring(0, s.length() - 1)+"+ة";
                    }
//                    if (s.endsWith("و") && !s.endsWith("+و") && s.length()>3){
//                        s=s.substring(0, s.length() - 1)+"+و";
//                    }

                    bf.write(s);
                    bf.write('\n');
                    
		    if (w.startsWith("+"))
			w = w.substring(1);
		    if (w.endsWith("+"))
			w = w.substring(0, w.length() - 1);
		    if (s.replace(";", "").replace("++", "+").equals(w))
			correct++;
                    else{
			System.err.println(s.replace(";", "").replace("++", "+") + "\t" + w + "\t" + w);
                        //System.err.println(s2);
                    }
		    total++;
		}else if(w.startsWith("#") || w.startsWith("@")){
                    correct++;
                    total++;
                }
	    }
	}
        bf.close();
	System.err.println((correct/total));
        System.err.println((total-correct));
        System.err.println((total));
        return correct/total;
    }
     
    private static void loadFunctionWords(String BinDir) throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException {
        
        if (!BinDir.endsWith("/")) {
            BinDir += "/";
        }
        // load previously seen tokenizations
        BufferedReader brFunctionWords = openFileForReading(BinDir + "maghFuncWords");
        String line = "";
        while ((line = brFunctionWords.readLine()) != null)
        {
            String word = line.trim().replace("+", "");

            if (!hmFunctionWords.containsKey(word))
                hmFunctionWords.put(word, line.trim());
        }
        
      
        
        
    }
    public static BufferedReader openFileForReading(String filename) throws FileNotFoundException {
        BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
        return sr;
    }

    public static BufferedWriter openFileForWriting(String filename) throws FileNotFoundException {
        BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
        return sw;
    }
}
