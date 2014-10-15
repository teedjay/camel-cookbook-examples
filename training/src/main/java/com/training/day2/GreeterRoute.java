package com.training.day2;

import org.apache.camel.builder.RouteBuilder;

/**
 * Simple bean binding example - can be tested as a simple POJO without Camel
 * @author thore
 */
public class GreeterRoute extends RouteBuilder {

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
        from(startUri).routeId("greeterRoute")
            .choice()
                .when(simple("${header[locale]} == 'no'"))
                    .transform(simple("Hallo: ${body}"))
                .when(simple("${header[locale]} == 'se'"))
                    .transform(simple("Bork bork bork: ${body}"))
                .otherwise()
                    .transform(simple("Hello: ${body}"))
            .end()
            .setHeader("additionalGreeting", body())
            .to(endUri);
    }

}
