package com.training.day3;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route for testing aggregation.
 * @author thore
 */
public class AggregatorRoute extends RouteBuilder {

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

        from(startUri).routeId("aggregatorRoute")
            .log("Processing ${exchangeId} : ${body}")
            .transform(simple("${body.toUpperCase()}"))
            .aggregate(constant(true), new ConcatinatingStrategy()).completionSize(3)
            .log("At the end : ${body}")
            .to(endUri)
        ;

    }

}
