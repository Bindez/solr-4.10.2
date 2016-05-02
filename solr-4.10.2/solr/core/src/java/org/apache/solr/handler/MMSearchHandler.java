package org.apache.solr.handler;


import org.apache.solr.handler.StandardRequestHandler;
import org.apache.solr.parser.ParseException;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class MMSearchHandler extends StandardRequestHandler {

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp)
			throws Exception, ParseException, InstantiationException,
			IllegalAccessException {
		super.handleRequestBody(req, rsp);
	}

}
