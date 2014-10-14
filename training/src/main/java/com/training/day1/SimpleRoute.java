package com.training.day1;

import org.apache.camel.builder.RouteBuilder;

public class SimpleRoute extends RouteBuilder {

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
        from(startUri)
            .routeId("customTransform")
            .log("Received message: ${body}")
            .transform()
                .simple("I got: ${body}")       // you have xpath and loads others
            .to(endUri);
    }

}
