package com.training.day1;

import org.apache.camel.builder.RouteBuilder;

public class SimpleRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
                .log("Received message: ${body}")
                .to("mock:out");
    }

}
