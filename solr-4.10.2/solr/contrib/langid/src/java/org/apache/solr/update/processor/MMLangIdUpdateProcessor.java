package org.apache.solr.update.processor;



/**
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.processor.DetectedLanguage;
import org.apache.solr.update.processor.LanguageIdentifierUpdateProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bindez.nlp.detection.burmese.BurmeseLanguageDetector;
import com.bindez.nlp.detection.burmese.RegexBurmeseLanguageDetector;
import com.bindez.nlp.detection.interfaces.BurmeseLanguageConstants;
import com.bindez.nlp.unicodeconverter.ZawGyiToUni;

/**
 * Identifies the language of a set of input fields using Tika's
 * LanguageIdentifier.
 * The tika-core-x.y.jar must be on the classpath
 * <p>
 * See <a href="http://wiki.apache.org/solr/LanguageDetection">http://wiki.apache.org/solr/LanguageDetection</a>
 * @since 3.5
 */
public class MMLangIdUpdateProcessor extends LanguageIdentifierUpdateProcessor {
	protected final static Logger log = LoggerFactory.getLogger(MMLangIdUpdateProcessor.class);
	
  public MMLangIdUpdateProcessor(SolrQueryRequest req,
      SolrQueryResponse rsp, UpdateRequestProcessor next) {
    super(req, rsp, next);
  }
  
  @Override
  protected List<DetectedLanguage> detectLanguage(String content) {
    List<DetectedLanguage> languages = new ArrayList<DetectedLanguage>();
    if(content.trim().length() != 0) { 
      BurmeseLanguageDetector detector = new BurmeseLanguageDetector();
      ZawGyiToUni converter = ZawGyiToUni.getInstance();
      
      String encoding = detector.detect(content.toCharArray());
      
      if(BurmeseLanguageConstants.ZAWGYI.equals(encoding)){
    	  	log.info("Texts detected as Zawgyi. Converting to Unicode now...");
			content = converter.ZawGyiToUniConvert(content);
      }
      
      DetectedLanguage language = new DetectedLanguage(encoding,8.0);
      languages.add(language);
      
    } else {
      log.info("Text is empty and can't detect language.");
    }
    return languages;
  }

@Override
protected SolrInputDocument process(SolrInputDocument doc) {
	// TODO Auto-generated method stub
	//doc = super.process(doc);
	ZawGyiToUni converter = ZawGyiToUni.getInstance();
	Collection<Object> content = doc.getFieldValues("content");
//	Collection<Object> text = doc.getFieldValues("text");
//	Collection<Object> url = doc.getFieldValues("url");
	Collection<Object> title = doc.getFieldValues("title");
	Collection<Object> articleTitle = doc.getFieldValues("articleTitle");
	
	
	if(content != null){
	for(Object cObj : content){
		String c = (String) cObj ;
		String cEnc = new BurmeseLanguageDetector().detect(c);
		if(BurmeseLanguageConstants.ZAWGYI.equals(cEnc)){
			
			c = converter.ZawGyiToUniConvert(c);
			doc.setField("content", c);
			
			if (title != null) {
				for (Object tObj : title) {
					String t = (String) tObj;
					boolean isMM = new RegexBurmeseLanguageDetector().checkContainMyChar(t);
					if(isMM){
						doc.setField("titleLang", BurmeseLanguageConstants.MYANMAR);	
						doc.setField("title", converter.ZawGyiToUniConvert(t));
					}else{
						doc.setField("titleLang", BurmeseLanguageConstants.ENGLISH);
					}
				}
			}
			
			if (articleTitle != null) {
				for (Object atObj : articleTitle) {
					String t = (String) atObj;
					boolean isMM = new RegexBurmeseLanguageDetector().checkContainMyChar(t);
					if(isMM){
						doc.setField("articleTitleLang", BurmeseLanguageConstants.MYANMAR);	
						doc.setField("articleTitle",converter.ZawGyiToUniConvert(t));
					}else{
						doc.setField("articleTitleLang", BurmeseLanguageConstants.ENGLISH);
					}
				}
			}
			
			
		
		}
	 }
	}
	
//	for(Object o : text){
//		String t = (String) o ;
//		String encoding = new MMLanguageIdentifier().detect(t);
//		if(MMEncodingConstants.ZAWGYI.equals(encoding)){
//			t = converter.convertZGtoUnicode(t);
//			doc.setField("text", t);
//		}
//	}
	
//		if (url != null) {
//			for (Object o : url) {
//				String u = (String) o;
//				String encoding = new BurmeseLanguageDetector().detect(u);
//				if (BurmeseLanguageConstants.ZAWGYI.equals(encoding)) {
//					u = converter.ZawGyiToUniConvert(u);
//					doc.setField("url", u);
//				}
//			}
//		}
		
	return doc ;
 }
}
