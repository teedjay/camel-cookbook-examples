package com.training.day1;

import org.apache.camel.builder.RouteBuilder;

public class SimpleRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
            .routeId("customTransform")
            .log("Received message: ${body}")
            .transform()
                .simple("I got: ${body}")       // you have xpath and loads others
            .to("mock:out");
    }

}
