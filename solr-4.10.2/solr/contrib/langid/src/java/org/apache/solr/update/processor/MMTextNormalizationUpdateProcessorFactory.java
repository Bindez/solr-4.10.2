package org.apache.solr.update.processor;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.util.SolrPluginUtils;
import org.apache.solr.util.plugin.SolrCoreAware;

public class MMTextNormalizationUpdateProcessorFactory extends
        UpdateRequestProcessorFactory implements SolrCoreAware, LangIdParams {

  protected SolrParams defaults;
  protected SolrParams appends;
  protected SolrParams invariants;

  public void inform(SolrCore core) {
  }

  /**
   * The UpdateRequestProcessor may be initialized in solrconfig.xml similarly
   * to a RequestHandler, with defaults, appends and invariants.
   * @param args a NamedList with the configuration parameters 
   */
  @SuppressWarnings("rawtypes")
  public void init( NamedList args )
  {
    if (args != null) {
      Object o;
      o = args.get("defaults");
      if (o != null && o instanceof NamedList) {
        defaults = SolrParams.toSolrParams((NamedList) o);
      } else {
        defaults = SolrParams.toSolrParams(args);
      }
      o = args.get("appends");
      if (o != null && o instanceof NamedList) {
        appends = SolrParams.toSolrParams((NamedList) o);
      }
      o = args.get("invariants");
      if (o != null && o instanceof NamedList) {
        invariants = SolrParams.toSolrParams((NamedList) o);
      }
    }
  }

  @Override
  public UpdateRequestProcessor getInstance(SolrQueryRequest req,
                                            SolrQueryResponse rsp, UpdateRequestProcessor next) {
    // Process defaults, appends and invariants if we got a request
    if(req != null) {
      SolrPluginUtils.setDefaults(req, defaults, appends, invariants);
    }
    return new MMTextNormalizationUpdateProcessor(req, rsp, next);
  }

}
