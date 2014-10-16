package com.training.day3;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Route for testing splitting.
 * @author thore
 */
public class CmpRoute extends RouteBuilder {

    private String startUri;
    private String endUri;

    public void setStartUri(String startUri) {
        this.startUri = startUri;
    }

    public void setEndUri(String endUri) {
        this.endUri = endUri;
    }

    @Override
    public void configure() throws Exception {

        from(startUri).routeId("splitterRoute")
            .split(body()).aggregationStrategy(new ConcatinatingStrategy())
                .transform(simple("${body.toUpperCase()}"))
                .log("Processing ${exchangeId} : ${body}")
            .end()
            .log("At the end : ${body}")
            .to(endUri)
        ;

    }

    private class ConcatinatingStrategy implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange; // first item to aggregate will not have an oldExchange
            }
            String s1 = oldExchange.getIn().getBody(String.class);
            String s2 = newExchange.getIn().getBody(String.class);
            oldExchange.getIn().setBody(s1 + ":" + s2);
            return oldExchange;
        }
    }

}
