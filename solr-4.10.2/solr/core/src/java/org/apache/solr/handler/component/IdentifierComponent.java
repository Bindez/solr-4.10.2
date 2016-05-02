package org.apache.solr.handler.component;

import java.io.IOException;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bindez.nlp.detection.burmese.RegexBurmeseLanguageDetector;
import com.bindez.nlp.detection.interfaces.BurmeseLanguageConstants;
import com.bindez.nlp.unicodeconverter.ZawGyiToUni;


public class IdentifierComponent extends SearchComponent {
	
	Logger logger = LoggerFactory.getLogger(IdentifierComponent.class);
	
	private ZawGyiToUni converter = ZawGyiToUni.getInstance();

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
		SolrQueryRequest req = rb.req;
		SolrParams params = req.getParams();
		
		if (rb.getQueryString() == null) {
		      rb.setQueryString( params.get( CommonParams.Q ) );
		}
		
		String qStr = rb.getQueryString();
//		String encoding = new MMLanguageIdentifier().detect(qStr);
		String encoding = new RegexBurmeseLanguageDetector().detect(qStr);
		
		if(BurmeseLanguageConstants.ZAWGYI.equals(encoding)){
			logger.debug("Detected Zawgyi texts. Will be converted to Unicode");
			qStr = converter.ZawGyiToUniConvert(qStr);
		}else if(BurmeseLanguageConstants.MM3.equals(encoding))
			logger.debug("Detected Unicode texts. No conversion required.");
		else
			logger.debug("Cannot detect input, the texts will not be converted");
		
		rb.setQueryString(qStr);
	}

	@Override
	public void process(ResponseBuilder rb) throws IOException {
		
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

}
