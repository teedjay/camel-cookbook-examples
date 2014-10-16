package com.training.day3;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route for testing aggregation.
 * @author thore
 */
public class SplitAggregatorRoute extends RouteBuilder {

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
            .split(body())
                .log("Processing ${exchangeId} : ${body}")
                .transform(simple("${body.toUpperCase()}"))
                .to("direct:aggregate")
            .end();

        from("direct:aggregate")
            .aggregate(constant(true), new ConcatinatingStrategy())
                    .completionSize(2)
                    .completionInterval(500)
                .log("At the end : ${body}")
                .to(endUri)
            .end();

    }

}
