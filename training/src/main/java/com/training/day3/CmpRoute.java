package com.training.day3;

import org.apache.camel.builder.RouteBuilder;

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
            .split(body())
                .bean(UpperCaseBean.class)
                .log("Processing ${exchangeId} : ${body}")
            .end()
            .log("At the end : ${body}")
            .to(endUri)
        ;

    }

    public static class UpperCaseBean {
        public String upperCase(String in) { return in.toUpperCase(); }
    }

}
