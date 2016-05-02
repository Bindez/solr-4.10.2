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

import com.bindez.nlp.spellcheck.BurmeseSpellchecker;



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
public class MMTextNormalizationUpdateProcessor extends UpdateRequestProcessor implements MMTextNormalizationParams {

  protected final static Logger log = LoggerFactory.getLogger(MMTextNormalizationUpdateProcessor.class);

  protected BurmeseSpellchecker spellchecker = BurmeseSpellchecker.getInstance();
  protected boolean enabled;

  protected String[] inputFields = {};
  protected IndexSchema schema;
  
  public MMTextNormalizationUpdateProcessor(SolrQueryRequest req,
                                           SolrQueryResponse rsp, UpdateRequestProcessor next) {
    super(next);
    schema = req.getSchema();
//    spellchecker = BurmeseSpellchecker.getInstance();
    initParams(req.getParams());
  }
  
  private void initParams(SolrParams params) {
    if (params != null) {
      if(params.get(FIELDS_PARAM, "").length() > 0) {
        inputFields = params.get(FIELDS_PARAM, "").split(",");
      }
    }
    log.debug("MMTextNormalization configured");

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
	
	for(String fieldName : inputFields ){
		SolrInputField inputField = doc.getField(fieldName);
		
		if(inputField != null && inputField.getValue() != null ){
			String fieldValue = inputField.getValue().toString().trim();
			if(fieldValue.length() > 0){
				fieldValue = spellchecker.normalization(fieldValue);
				doc.setField(fieldName, fieldValue);
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



