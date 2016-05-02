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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kaung Khant
 */
public class MySpellChecker {

    /**
     * @param args the command line arguments
     */
   // static BurmeseWordTokenizer wt=new BurmeseWordTokenizer();
   static SyllableTokenizer st=new BurmeseSyllableTokenizer();
    
    public static void main(String[] args) {
        String filepath="D:\\TestData.txt";
        String outFilePath="D:\\TestDataCopy.txt";
//        String dicFilePath="D:\\WordList_All_Sort.txt";
       BurmeseSpellchecker cr =BurmeseSpellchecker.getInstance();
       BurmeseWordTokenizer wt=BurmeseWordTokenizer.getInstance();
       String businessStr="ြန်မာနိုင်ငံအတွင်း ရေနံနှင့်သဘာဝဓာတ်ငွေ့ လုပ်ငန်းများတွင် ရင်းနှီးမြှုပ်နှံမှုများ ပြုလုပ်နေသည့် ထိုင်းနိုင်ငံအခြေစိုက် PTTEP သည် လက်ရှိလုပ်ငန်းများအပြင် စွမ်းအင်ထုတ်လုပ်ရေးလုပ်ငန်းများ ၊  သန့်စင်စက်ရုံများ ၊  သဘာဝဓာတ်ငွေ့ပိုက်လိုင်း လုပ်ငန်းများ ၊  ဓာတ်ဆီ ၊  ဒီဇယ် အစရှိသည့် ရေနံထွက်ပစ္စည်းလုပ်ငန်းများနှင့် လျှပ်စစ်စွမ်းအင်ထုတ် လုပ်ငန်းများတွင် ဝင်ရောက်ရင်းနှီးမြှုပ်နှံမှုလုပ်ငန်းများ လုပ်ကိုင်လိုနေကြောင်း နိုင်ငံတကာသတင်းများတွင် ဖော်ပြထားသည် ။";
       String politicStr="ဒေါ်အောင်ဆန်းစုကြည်က နေပြည်တော်တွင် စက်တင်ဘာ ၁၈ ရက်က ပြုလုပ်သည့် ပထမအကြိမ် ပြည်သူ့လွှတ်တော် ၁၁ ကြိမ် မြောက် ပုံမှန်အစည်းအဝေးအပြီးတွင် ကရင်ပြည်နယ်ရှိ အမျိုးသားဒီမိုကရေစီအဖွဲ့ချုပ် အဖွဲ့ဝင်များနှင့်တွေ့ဆုံရာ၌ အထက်ပါကဲ့သို့ ပြောကြားခဲ့ခြင်းဖြစ်သည် ။ ";
       String eductionStr="အစဉ်အလာပညာရေးသည် လူ့ပတ်ဝန်းကျင် သို့မဟုတ် လူ့ဘောင်အဖွဲ့အစည်းတွင် သင်ရိုးညွှန်းတမ်းဖြင့် လူ အထူးသဖြင့် လူငယ်များကို သတ်မှတ်သင်ကြားသည့် ကာလတွင် စတင်ခဲ့သည် ဟုဆိုနိုင်သည်။ အစဉ်အလာပညာရေးသည် တဖြည်းဖြည်း စနစ်ကျကျနှင့် ပြည့်စုံတိုးတက်လာခဲ့ရသည်။ အစဉ်အလာ ပညာရေးစနစ်သည် လူ့တန်ဖိုး သို့မဟုတ် စံတို့ကို မြင့်မားစေရန်ကြံဆောင်ပေး နေသကဲ့သို့ ပညာဗဟုသုတတိုးပွားစေရန်လည်းအလေးထားသည် ဖြစ်ရာ မဖြစ်ဖြစ်အောင်လုပ်ခြင်းကြောင့် တစ်ခါတစ်ရံ နှိပ်စက်ကလူ ပြုရသည့် စနစ် သို့မဟုတ် ပုံစံလေးတွေလည်း ဝင်လာခဲ့သည်ကို တွေ့ရသည်။";
       String sportStr="မြန်မာအသင်းမှာ ဆီမီးဖိုင်နယ်တွင် မလေးရှားအသင်းကို အနိုင်ရခဲ့သည့် ပွဲထွက်လူစာရင်းကို အပြောင်းအလဲ အနည်းငယ်ပြုလုပ်ခဲ့ပြီး ခံစစ်ကစားပုံ အမှားယွင်းများခဲ့သောကြောင့် ဗီယက်နမ်အသင်းကို သုံးဂိုးအထိပေးခဲ့ရခြင်းဖြစ်သည်";
       String entertainmentStr="အဆိုတော် လင်းလင်းဟာ သီချင်းဆိုရုံမက တေးရေးတစ်ဦးလည်း ဖြစ်ပါတယ်။ သူ့ရဲ့ တတိယမြောက် အယ်လ်ဘမ်အဖြစ် သံမဏိလိပ်ပြာ ကိုလည်း ၂၀၁၃ နှစ်လယ်ပိုင်းက ထွက်ရှိထားပါတယ်။";
       String healthStr="ကျန်းမာရေးနှင့် ညီညွတ်စွာ နေနိုင်ရန်မှာ မခက်ပေ။ မိမိ၏ နေမှုစားမှုတို့ကို စံနစ်တကျ ဂရုစိုက်၍ ပြုပြင် ပေးရုံမျှနှင့် ကျန်းမာမှုကို လွယ်ကူစွာ ရနိုင်သည်။ ";
//       System.out.println("Business->"+wt.checkCategory(businessStr));
//       System.out.println("Political->"+wt.checkCategory(politicStr));
//       System.out.println("Education->"+wt.checkCategory(eductionStr));
//       System.out.println("Sport->"+wt.checkCategory(sportStr));
//       System.out.println("Entertainment->"+wt.checkCategory(entertainmentStr));
//       System.out.println("Health->"+wt.checkCategory(healthStr));
       
        List<String> sd=wt.checkCategoryWithList(businessStr);
         for(int i=0;i<sd.size();i++)
         {
             System.out.println("Word:"+sd.get(i));
             
         }
//        System.out.println(wt.searchParticalandReplace("ကျွန်တော်ကျောင်းကို သွား တယ်"));
        //String outputStr="";        
//       String inputStr="";
//       
//       System.out.println(cr.editString("...ပထမဆုံး<hl>ရုံး</hl>ထုတ် ဇွန် လ ၂၀ ရက်‌နေ ့မနက် ၉နာရီခန် ့က တာ‌မွေမြို ့နယ် တာ‌မွေတရား<hl>ရုံး</hl>တွင် ဖမ်းဆီးခံ မဟာသနိ ္တသုခဆရာ‌တော် ၅ ပါးကို ပထမဦးဆုံး တရား<hl>ရုံး</hl>ထုတ်စစ်‌ဆေးပြီး အာမခံ‌ပေးလိုက်‌ကြောင်းသတင်းရရှိသည်။... ပြည်တွင်း..."));
//       List<Word> stList=wt.testExtractWordFromInput("စိတ်ဝင်စား");
//       for(int i=0;i<stList.size();i++)
//         {
//             System.out.println("Word:"+stList.get(i).getText());
//             System.out.println("Start:"+stList.get(i).getStart());
//             System.out.println("End:"+stList.get(i).getEnd());
//         }
//        List<Word> sd=wt.getListWordFromInput("စိတ်ဝင်စားကြမယ့် ဥပမာ - မြန်မာစစ်တုရင်လို");
//         for(int i=0;i<sd.size();i++)
//         {
//             System.out.println("Word:"+sd.get(i).getText());
//             System.out.println("Start:"+sd.get(i).getStart());
//             System.out.println("End:"+sd.get(i).getEnd());
//         }
//      inputStr=cr.readText(filepath);
//      inputStr=cr.spellCheckAutoComplete(inputStr);
//          System.out.println(inputStr);
//          List<Word> sd=wt.tokenizeWords("စိတ်ဝင်စားကြမယ့် ဥပမာ - မြန်မာစစ်တုရင်လို");
//         for(int i=0;i<sd.size();i++)
//         {
//             System.out.println("Word:"+sd.get(i).getText());
//             System.out.println("Start:"+sd.get(i).getStart());
//             System.out.println("End:"+sd.get(i).getEnd());
//         }
          //System.out.println("Change: "+wt.changeWithMM("static ဥပမာ static americanဥပမာ ဥပမာcomputer"));
        // TODO code application logic here
    }
    
    public static List<String> ReadText(String fileName)
    {
        List<String> dicList = new ArrayList<String>();
        FileInputStream fInputStream;
        BufferedReader bReader;
       Path filepath=Paths.get(fileName);
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(fileName));
             Reader rd=new InputStreamReader(is,"UTF-16");
             bReader=new BufferedReader(rd); 
            String line = null;
           
                while ((line = bReader.readLine()) != null) {
               dicList.add(line);
               
            }
            
        } catch (IOException ex) {
                //Logger.getLogger(MyCore.class.getName()).log(Level.SEVERE, null, ex);
            }   
        return dicList;
    }
        
}
