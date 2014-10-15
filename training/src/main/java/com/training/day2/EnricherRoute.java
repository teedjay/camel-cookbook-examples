package com.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Simple bean binding example - can be tested as a simple POJO without Camel
 * @author thore
 */
public class EnricherRoute extends RouteBuilder {

    private String startUri;
    private String serviceUri;
    private String endUri;

    public void setStartUri(String startUri) {
        this.startUri = startUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    public void setEndUri(String endUri) {
        this.endUri = endUri;
    }


    @Override
    public void configure() throws Exception {

        from(startUri).routeId("enricherRoute")
            .enrich(serviceUri, new AggregationStrategy() {
                @Override
                public Exchange aggregate(Exchange exchange0, Exchange exchange1) {
                    String body1 = exchange0.getIn().getBody(String.class); // the main route
                    String body2 = exchange1.getIn().getBody(String.class); // the enricher route
                    exchange0.getIn().setBody(body1 + ":" + body2);
                    return exchange0;
                }
            })
            .to(endUri);

    }

}
