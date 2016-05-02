package org.apache.solr.burmese.mm;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.RollingCharBuffer;
import org.apache.lucene.util.AttributeFactory;

import com.bindez.nlp.tokenizers.Word;
import com.bindez.nlp.tokenizers.burmese.BurmeseWordTokenizer;





// TODO: somehow factor out a reusable viterbi search here,
// so other decompounders/tokenizers can reuse...
public final class MMStandardWordTokenizer extends Tokenizer {
  

 private final RollingCharBuffer buffer = new RollingCharBuffer();

 private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
 private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

 /** Max word length */
 private static final int MAX_WORD_LEN = 255;

 /** buffer size: */
 private static final int IO_BUFFER_SIZE = 4096;
 
 /** word type: single=>ASCII  double=>non-ASCII word=>default */
 private final String TOKENTYPE_WORD = "word";

 private boolean EOF = false ; // to indicate there is no more token

 /**
  * I/O buffer, used to store the content of the input(one of the <br>
  * members of Tokenizer)
  */
 private  char[] tempBuffer = new char[IO_BUFFER_SIZE]; 
 private int inputDataLen = 0 ; // the number of chars read from input stream
 private String texts = "" ; // text to be tokenized
 private int totalNumWord = 0 ; //Total number of token in current text
 private int offset = 0;  // token start position in original base texts in file
 
 private List<Word> wordArray = null ;
 private int wordArrayIndex = 0 ;
 private String riskyToken = "" ;
 private BurmeseWordTokenizer wordTokenizer = BurmeseWordTokenizer.getInstance();
 
 private long wordCount = 0 ;
 
  protected MMStandardWordTokenizer(AttributeFactory factory, Reader in) {
    super(factory, in);
    input = in ;
  }

  public void close() throws IOException {
    super.close();
    buffer.reset(input);
  }

 
  public void reset() throws IOException {
    super.reset();
    resetState();
  }

 
  public void end() throws IOException {
    super.end();
    final int finalOffset = correctOffset(offset);
    this.offsetAtt.setOffset(finalOffset, finalOffset);
  }


  public boolean incrementToken() throws IOException {
    
    clearAttributes();
    
    if(EOF)
      return false ;

    if (wordArrayIndex >= totalNumWord) {
      tempBuffer = new char[IO_BUFFER_SIZE];
      inputDataLen = -1 ;
            inputDataLen = input.read(tempBuffer); // read new line
            if(riskyToken.length() < 1 && inputDataLen < 0)
              return false;
            texts = riskyToken.concat(new String(tempBuffer)) ; // concat previous last token with new texts
           
           /* System.out.println("Spellcheck start :"+ new Date().toGMTString());
            texts = spellchecker.spellCheckAutoComplete(texts);
          System.out.println("spellcheck finished :"+ new Date().toGMTString());*/
            
            // handle a very long string of white space
          while(texts.trim().length() < 1){
            inputDataLen = input.read(tempBuffer); // read new line
            texts = riskyToken.concat(new String(tempBuffer)) ; // concat previous last token with new texts
            if(inputDataLen < 0)
              return false ;
          }
          
          //lets extract words
         /* logger.debug("Extracting words :"+ new Date().toGMTString());
          
          logger.debug("Done extracting words :"+ new Date().toGMTString());
          */
          wordArray = wordTokenizer.tokenizeWords(texts.trim());
            //prepare for riskyToken
            if(wordArray != null && wordArray.size() > 1){
              riskyToken = wordArray.get(wordArray.size()-1).getText(); // keep last token in temp
              List<Word> tmp = new ArrayList<Word>();
              for(int i=0; i<wordArray.size()-1 ; i++) // just excluding last token from wordArray
                tmp.add(wordArray.get(i)) ;
              wordArray = tmp ; 
            }else if(wordArray == null || wordArray.size() < 1){
              return false ;
            }
            
      totalNumWord = wordArray == null ? 0 : wordArray.size() ;
      wordArrayIndex = 0; // default value at start
        }
    
    if(inputDataLen < 0 ){
      EOF = true ; // this round is final. No more token after this return.
    }
    
    
    Word word =  wordArray.get(wordArrayIndex++) ;
    
    wordCount++ ;
//    System.out.println(wordCount +" : "+ word.getText().toString() +" , "+word.getStart()+"-"+word.getEnd() );
    
    if (word.getText().length() > 0) {
      termAtt.copyBuffer(word.getText().toCharArray(), 0, word.getText().length()); // only for token.
      offsetAtt.setOffset(correctOffset(offset+word.getStart()), correctOffset(offset+word.getEnd()));
       // typeAtt.setType(TOKENTYPE_WORD); 
        
        if(wordArrayIndex == wordArray.size()){
        offset += word.getEnd() ;
      }
        return true;
    } else { // just workaround for empty token. Need to fix by NLP team
      termAtt.copyBuffer("-".toCharArray(), 0, 1); // only for token.
      offsetAtt.setOffset(correctOffset(offset+word.getStart()), correctOffset(offset+word.getEnd()));
      //  typeAtt.setType(TOKENTYPE_WORD); 
        
        if(wordArrayIndex == wordArray.size()){
        offset += word.getEnd() ;
      }
        
        return true;
    } 
    
//    return false ;
  }
  
  
  
  private void resetState() throws IOException {

    offset = inputDataLen = wordArrayIndex =  totalNumWord = 0;
    riskyToken = texts = "" ;
    wordArray = new ArrayList<Word>();
    EOF = false ;


  }
  
}

