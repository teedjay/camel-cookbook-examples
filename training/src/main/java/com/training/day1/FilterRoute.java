package com.training.day1;

import org.apache.camel.builder.RouteBuilder;

public class FilterRoute extends RouteBuilder {

    public static final String CUSTOM_TRANSFORM = "customTransform";
    public static final String ID_SWEDISH_LOG = "swedishLog";

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
        from(startUri).routeId(CUSTOM_TRANSFORM)
            .filter().simple("${header[locale]} == 'se'")
                .log("Bork bork bork").id(ID_SWEDISH_LOG)     // set id for log entry
                .to("file:swedishMessage")                    //
            .end()
            .log("Received message: ${body}")
            .transform()
                .simple("I got: ${body}")
            .to(endUri);
    }

}
