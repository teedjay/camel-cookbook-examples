package com.training.day3;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;

/**
 * Route template for testing new things.
 * @author thore
 */
public class IdempotentRoute extends RouteBuilder {

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

        from(startUri).routeId("idempotentRoute")
            .idempotentConsumer(body(), new MemoryIdempotentRepository())
                .log("Consuming ${body}")
                .to(endUri)
            .end()
            .log("Processed ${body}")
        ;

    }

}
