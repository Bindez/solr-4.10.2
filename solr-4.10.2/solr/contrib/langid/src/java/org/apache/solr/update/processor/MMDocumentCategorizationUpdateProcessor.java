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

import java.io.IOException;

import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.update.AddUpdateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bindez.nlp.tokenizers.burmese.Categorizer;
import com.bindez.nlp.tokenizers.burmese.Category;

//import com.bindez.nlp.spellcheck.BurmeseSpellchecker;


/**
 * Identifies the language of a set of input fields. 
 * Also supports mapping of field names based
 * on detected language. 
 * <p>
 * See <a href="http://wiki.apache.org/solr/LanguageDetection">http://wiki.apache.org/solr/LanguageDetection</a>
 * @since 3.5
 * @lucene.experimental
 */
public class MMDocumentCategorizationUpdateProcessor extends UpdateRequestProcessor implements MMDocumentCategorizationParams {

  protected final static Logger log = LoggerFactory.getLogger(MMDocumentCategorizationUpdateProcessor.class);

  protected boolean enabled;

  protected String[] inputFields = {};
  protected IndexSchema schema;
  
  public MMDocumentCategorizationUpdateProcessor(SolrQueryRequest req,
                                           SolrQueryResponse rsp, UpdateRequestProcessor next) {
    super(next);
    schema = req.getSchema();
    initParams(req.getParams());
  }
  
  private void initParams(SolrParams params) {
    if (params != null) {
      if(params.get(FIELDS_PARAM, "").length() > 0) {
        inputFields = params.get(FIELDS_PARAM, "").split(",");
      }
    }
    log.debug("MMDocumentCategorization configured");

    if (inputFields.length < 1) {
      throw new SolrException(ErrorCode.BAD_REQUEST,
              "Missing or faulty configuration of MMTextNormalizationUpdateProcessor. Input fields must be specified as a comma separated list");
    }
  }

  @Override
  public void processAdd(AddUpdateCommand cmd) throws IOException {
      process(cmd.getSolrInputDocument());
    super.processAdd(cmd);
  }
  
  /**
   * This is the main, testable process method called from processAdd()
   * @param doc the SolrInputDocument to work on
   * @return the modified SolrInputDocument
   */
  protected SolrInputDocument process(SolrInputDocument doc) {
//	  BurmeseWordTokenizer tokenizer = new BurmeseWordTokenizer();
	  Categorizer categorizer = Categorizer.getInstance();
	  Category[] categories = Category.values();
	  String category = "" ;
	  
	  // if category field and value already exist
	  if(doc != null && doc.getField("category") != null ){
		  Object v = doc.getFieldValue("category");
		  if(v !=null && v.toString().length() > 0)
			  log.info("This doc already has category value : "+v.toString());
			  return doc ;
	  }
	  
	for(String fieldName : inputFields ){
		SolrInputField inputField = doc.getField(fieldName);
		Object obj = inputField.getValue();
		if(obj != null ){
			String fieldValue = obj.toString().trim();
			if(fieldName != null && fieldName.equalsIgnoreCase("url")){
				for(int i=1; i< categories.length ; i++){
					if(fieldValue.contains(categories[i].getValue().toLowerCase())){
						category = categories[i].getValue();
						doc.setField(DOC_CATEGORY, category);
						return doc ;
					}
				}
			}else{
				log.info("Processing categoryzation...");
				category = categorizer.checkCategory(fieldValue);
				if(category != null && category.length() > 0){
					log.info("This doc is categorized as : "+category);
					doc.setField(DOC_CATEGORY,category);
				}
			}
			
			
		}
	}
	  
    return doc;
  }

  /**
   * Tells if this processor is enabled or not
   * @return true if enabled, else false
   */
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}



