/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bindez.nlp.spellcheck;

import com.bindez.nlp.tokenizers.Word;
import com.bindez.nlp.tokenizers.burmese.BurmeseSyllableTokenizer;
import com.bindez.nlp.tokenizers.burmese.BurmeseWordTokenizer;
import com.bindez.nlp.tokenizers.interfaces.SyllableTokenizer;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Kaung Khant
 */
public class BurmeseSpellchecker {

    private static int i = 0;
    private static List<String> lst;
    private SyllableTokenizer st = new BurmeseSyllableTokenizer();
    private BurmeseWordTokenizer wt =  BurmeseWordTokenizer.getInstance();
    private static BurmeseSpellchecker instance = null;
    private List<String> docList = new ArrayList<String>();

    private BurmeseSpellchecker() {
    }

    public static BurmeseSpellchecker getInstance() {
        if (instance == null) {
            instance = new BurmeseSpellchecker();
            instance.init();
        }
        return instance;
    }

    protected void init() {
        // initialize docList here
        docList = wt.getWordList();
    }

    public int levenshteinDistanceWithSyllable(String src, String dest) {

        int i, j, cost;
        src = st.tokenize(src);
        dest = st.tokenize(dest);

        String[] str1 = src.split("Ò");
        String[] str2 = dest.split("Ò");
        int[][] d = new int[str1.length + 1][str2.length + 1];
        for (i = 0; i <= str1.length; i++) {
            d[i][ 0] = i;
        }
        for (j = 0; j <= str2.length; j++) {
            d[0][ j] = j;
        }
        for (i = 1; i <= str1.length; i++) {
            for (j = 1; j <= str2.length; j++) {

                if (str1[i - 1].equals(str2[j - 1])) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                d[i][ j] =
                        Math.min(
                        d[i - 1][ j] + 1, // Deletion
                        Math.min(
                        d[i][ j - 1] + 1, // Insertion
                        d[i - 1][ j - 1] + cost));		// Substitution

                if ((i > 1) && (j > 1) && (str1[i - 1].equals(str2[j - 2])) && (str1[i - 2].equals(str2[j - 1]))) {
                    d[i][j] = Math.min(d[i][ j], d[i - 2][ j - 2] + cost);
                }
            }
        }

        return d[str1.length][str2.length];
    }

    public int levenshteinDistanceWithChar(String src, String dest) {     
        
        int i, j, cost;
        char[] str1 = src.toCharArray();
        char[] str2 = dest.toCharArray();
        int[][] d = new int[str1.length + 1][str2.length + 1];
        for (i = 0; i <= str1.length; i++) {
            d[i][ 0] = i;
        }
        for (j = 0; j <= str2.length; j++) {
            d[0][ j] = j;
        }
        for (i = 1; i <= str1.length; i++) {
            for (j = 1; j <= str2.length; j++) {

                if (str2[j - 1]==(str1[i - 1])) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                d[i][ j] =
                        Math.min(
                        d[i - 1][ j] + 1, // Deletion
                        Math.min(
                        d[i][ j - 1] + 1, // Insertion
                        d[i - 1][ j - 1] + cost));		// Substitution

                if ((i > 1) && (j > 1) && (str1[i - 1]==(str2[j - 2])) && (str1[i - 2]==(str2[j - 1]))) {
                    d[i][j] = Math.min(d[i][ j], d[i - 2][ j - 2] + cost);
                }
            }
        }

        return d[str1.length][str2.length];
    }    
 
    public List<String> search(String word, double fuzzyness) {
//#if !LINQ
        List<String> foundWords = new ArrayList<String>();
        List<String> similarWords = new ArrayList<String>();

        for (int i = 0; i < docList.size(); i++) {
            // Calculate the Levenshtein-distance:
            String s = docList.get(i);
            int levenshteinDistance = levenshteinDistanceWithSyllable(word, s);

            // Length of the longer string:
            int length = Math.max(word.length(), s.length());

            // Calculate the score:
            double score = 1.0 - (double) levenshteinDistance / length;

            // Match?
            if (score > fuzzyness) {
                foundWords.add(s);
            }
        }
      
        //Double Check
        for (int i = 0; i < foundWords.size(); i++) {
            // Calculate the Levenshtein-distance:
            String s = foundWords.get(i);
            int levenshteinDistance = levenshteinDistanceWithChar(word, s);

            // Length of the longer string:
            int length = Math.max(word.length(), s.length());

            // Calculate the score:
            double score = 1.0 - (double) levenshteinDistance / length;

            // Match?
            if (score > 0.90) {
                similarWords.add(s);
            }
        }

        return similarWords;
    }

    public List<String> checkDuplicated_ListwithSet(List<String> sValueTemp) {
        List<String> foundWords = new ArrayList<String>();
        Set<String> sValueSet = new HashSet<String>();
        for (String tempValueSet : sValueTemp) {
            if (!sValueSet.contains(tempValueSet)) {
                foundWords.add(tempValueSet);
            }
        }
        return foundWords;
    }

    public String normalization(String str)
    {
        str = str.replaceAll("၄င်း", "၎င်း");
            str = str.replaceAll("့်", "့်");
            str = str.replaceAll("\u200C", "");
            str = str.replaceAll("\u00A0", "");
            str=str.replaceAll("\u200B","");
            
            ///////// Case of "ာါ"/////////////
            str = str.replaceAll("([ခဂငဒဓပဝ])([ာ])", "$1ါ");
            str = str.replaceAll("([ခဂငဒဓပဝ])([ျ-ှ]+)([ါ])", "$1$2ာ");
            str = str.replaceAll("([စဇဉဖဗမရ])([ျ-ှ]*)([ါ])", "$1$2ာ");
            //////////End ////////////////
            
            /////////////////Double Entry/////////////////
            str = str.replaceAll("([်]+)", "်");
            str = str.replaceAll("([း]+)", "း");
            str = str.replaceAll("ွွှ", "ွှ");
            /////////////End///////////////////
        
        str = str.replaceAll(" ([ါ-ှ])", "$1");
        str = str.replaceAll(" ([က-အ])([္်])", "$1$2");       
        str = str.replaceAll("([က-အ])([ြ]?)င်္", "င်္$1$2");            
        str = str.replaceAll( "([ိ-ု]*)([ျြ])", "$2$1");        
         str = str.replaceAll("ဥ်","ဉ်");
         str = str.replaceAll("ဥ့်","ဉ့်");
         str=str.replaceAll("([က-အ])ွေျ", "$1ျွေ"); //ကွေျ
        // str=str.replaceAll("([်])([က-အ])([်])","$2$3");
         
            //# region two times typing
            str = str.replaceAll("\u103A\u103A", "\u103A");
             str = str.replaceAll("\u1031\u1031","\u1031");
             str=str.replaceAll("\u102F\u102F","\u102F");
             str = str.replaceAll("ီိ", "ီ");
             str = str.replaceAll("ိီ", "ီ");
             str = str.replaceAll("ဦီ", "ဦ");
             str = str.replaceAll( "ဦိ", "ဦ");            
             str = str.replaceAll("ိိ", "ိ");
            str = str.replaceAll("ွွ", "ွ");
            str = str.replaceAll("ှှ", "ှ");
             str = str.replaceAll("ဲဲ", "ဲ");
             str = str.replaceAll("ံံ", "ံ");
             str = str.replaceAll("့့", "့");
             str=str.replaceAll("ီီ", "ီ");
           // #endregion
        
       // #region Zero,Eight and seven
           str = str.replaceAll("၀([\u102B-\u103E]+)", "ဝ$1"); //zero and Wa
            str = str.replaceAll( "၀([\u1000-\u1021])([\u1039\u103A]?)", "ဝ$1$2"); //zero and Wa
           str = str.replaceAll( "([\u1000-\u103E])၀", "$1ဝ"); //zero and Wa
            str = str.replaceAll( "၈([\u102B-\u103E]+)", "ဂ$1"); //zero and Wa
           str = str.replaceAll( "၈([\u1000-\u1021])([\u1039\u103A]?)", "ဂ$1$2"); //zero and Wa
           str = str.replaceAll( "၇([\u102B-\u103E]+)", "ရ$1"); //zero and Wa
            str = str.replaceAll("၇([\u1000-\u1021])([\u1039\u103A]?)", "ရ$1$2"); //zero and Wa
          //  #endregion  
            
            // #region sa lone ya pint and za myin zwel
	str=str.replaceAll("စျ","ဈ");
            //  #endregion  
        
       
        return str;
    }
    
    public String spellCheckAutoComplete(String inputStr)
    {
        inputStr=normalization(inputStr);
          List<Word> missSpellWordIndex=wt.missSpellWordIndex(inputStr);
           List<String> fuzzyFoundList = new ArrayList<String>();          
          for(int i=0;i<missSpellWordIndex.size();i++)
          {             
          
               fuzzyFoundList=search(missSpellWordIndex.get(i).getText(),0.85);                    
               if(!fuzzyFoundList.isEmpty())
                      {
                        StringBuffer sb=new StringBuffer(inputStr);
                        inputStr=sb.replace(missSpellWordIndex.get(i).getStart(),missSpellWordIndex.get(i).getEnd(), fuzzyFoundList.get(0)).toString();
                      }
          }
        return inputStr;
    }
    
    public String readText(String fileName) {
        String instrbil = "";
        FileInputStream fInputStream;
        BufferedReader bReader;
        Path filepath = Paths.get(fileName);
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(fileName));
            Reader rd = new InputStreamReader(is, "UTF-16");
            bReader = new BufferedReader(rd);
            String line = null;

            while ((line = bReader.readLine()) != null) {
                instrbil += line;
            }

        } catch (IOException ex) {
            //Logger.getLogger(MyCore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instrbil;
    }

    public List<String> readTextList(String fileName) {
        List<String> dicList = new ArrayList<String>();
        FileInputStream fInputStream;
        BufferedReader bReader;
        Path filepath = Paths.get(fileName);
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(fileName));
            Reader rd = new InputStreamReader(is, "UTF-8");
            bReader = new BufferedReader(rd);
            String line = null;

            while ((line = bReader.readLine()) != null) {
                dicList.add(line);
            }

        } catch (IOException ex) {
            //Logger.getLogger(MyCore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dicList;
    }

    public void writeText(String fileName, String Strbil) {
        Writer out;
        try {

            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));

            out.write(Strbil);
            out.close();
        } catch (IOException ex) {
            //Logger.getLogger(MyCore.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public String editString(String inputStr)
    {
        inputStr=inputStr.substring(inputStr.indexOf("<hl>"));
        int endIndex=0;
        int hlIndex=0;
        int pokemaIndex=0;
        hlIndex=inputStr.lastIndexOf("</hl>");
        pokemaIndex=inputStr.lastIndexOf("။");
        if(hlIndex>pokemaIndex) endIndex=hlIndex;
        else endIndex=pokemaIndex;
        inputStr=inputStr.substring(0,endIndex+1);
        return inputStr;    
    }
}
