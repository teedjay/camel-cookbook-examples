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
public class SplitAggregatorRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";

    @Produce(uri = DIRECT_IN)
    ProducerTemplate in;

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

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
        SplitAggregatorRoute route = new SplitAggregatorRoute();
        route.setStartUri(DIRECT_IN);
        route.setEndUri(MOCK_OUT);
        return new RouteBuilder[] { route };
    }

    @Test
    public void testCMP() throws Exception {

        String[] goodThing = { "cake", "puppies", "sleeping babies"};

        mockOut.setExpectedMessageCount(2);
        mockOut.message(0).body().isEqualTo("CAKE:PUPPIES");    // this should be aggregated by the main thread for splitted message #2
        mockOut.message(1).body().isEqualTo("SLEEPING BABIES"); // this should be aggreagted ty the timeout checker thread

        in.sendBody(goodThing);

        assertMockEndpointsSatisfied();

    }

}