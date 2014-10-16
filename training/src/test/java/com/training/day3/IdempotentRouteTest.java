package com.training.day3;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class IdempotentRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_QUERY = "mock:query";
    public static final String MOCK_OUT = "mock:out";

    @Produce(uri = DIRECT_IN)
    ProducerTemplate in;

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

    @EndpointInject(uri = MOCK_QUERY)
    MockEndpoint mockQuery;

    @Override
    public boolean isUseDebugger() {
        return true;
    }

    @Override
    protected void debugBefore(Exchange exchange, Processor processor, ProcessorDefinition<?> definition, String id, String label) {
        super.debugBefore(exchange, processor, definition, id, label);
    }

    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        IdempotentRoute route = new IdempotentRoute();
        route.setStartUri(DIRECT_IN);
        route.setQueryUri(MOCK_QUERY);
        route.setEndUri(MOCK_OUT);
        return new RouteBuilder[] { route };
    }

    @Test
    public void testRoute() throws Exception {

        mockOut.setExpectedMessageCount(3);
        mockOut.expectedBodiesReceived("cheese", "chocolate", "wine");

        mockQuery.setExpectedMessageCount(1);
        mockQuery.expectedBodiesReceived("cheese");

        in.sendBody("cheese");
        in.sendBody("chocolate");
        in.sendBody("wine");
        in.sendBody("cheese");

        assertMockEndpointsSatisfied(); // asserts all injected mocks
    }

}