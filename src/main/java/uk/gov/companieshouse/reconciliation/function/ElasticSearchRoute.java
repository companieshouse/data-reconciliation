package uk.gov.companieshouse.reconciliation.function;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.elasticsearch.ElasticsearchScrollRequestIterator;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ElasticSearchRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:elastic")
                .setBody(constant("{\"query\": {\"match_all\": {} }, \"size\": 10000 }"))
                .enrich("{{endpoint.elasticsearch.alpha}}", (src, es) -> {
                    List<String> result = new ArrayList<>();
                    Iterator<SearchHit> it = es.getIn().getBody(ElasticsearchScrollRequestIterator.class);
                    while(it.hasNext()){
                        SearchHit hit = it.next();
                        result.add(hit.getId());
                        if(result.size() % 5000 == 0) {
                            this.log.info("Indexed {} results", result.size());
                        }
                    }
                    this.log.info("Indexed {} results", result.size());
                    src.getIn().setBody(result);
                    return src;
                })
                .to("log:esOutput");
    }
}
