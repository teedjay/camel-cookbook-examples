package com.training.day3;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;

/**
 * Route template for testing new things.
 * @author thore
 */
public class IdempotentRoute extends RouteBuilder {

    private String startUri;
    private String queryUri;
    private String endUri;

    public void setStartUri(String startUri) {
        this.startUri = startUri;
    }

    public void setQueryUri(String queryUri) {
        this.queryUri = queryUri;
    }

    public void setEndUri(String endUri) {
        this.endUri = endUri;
    }

    @Override
    public void configure() throws Exception {

        from(startUri).routeId("idempotentRoute")
            .idempotentConsumer(body(), new MemoryIdempotentRepository())
                    .skipDuplicate(false)
                .choice()
                    .when(simple("${property[" + Exchange.DUPLICATE_MESSAGE + "]} == true"))
                        .log("Duplicate ${body}")
                        .to(queryUri)
                    .otherwise()
                        .log("Consuming ${body}")
                        .to(endUri)
                .endChoice()
            .end()
        ;

    }

}
