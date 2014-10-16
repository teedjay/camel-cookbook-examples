package com.training.day3;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route template for testing new things.
 * @author thore
 */
public class TransactedRoute extends RouteBuilder {

    private String startUri;
    private String endUri;
    private String serviceUri;

    public void setStartUri(String startUri) {
        this.startUri = startUri;
    }

    public void setEndUri(String endUri) {
        this.endUri = endUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    @Override
    public void configure() throws Exception {

        from(startUri).routeId("splitterRoute")
            .to(endUri);

    }

}
