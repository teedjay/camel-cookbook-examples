package com.training.day1;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class TempateRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";
    public static final String SEDA_BRIDGE = "seda:transform2";

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

    @Produce(uri = DIRECT_IN)
    ProducerTemplate template;

    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        TemplateRoute route1 = new TemplateRoute();
        route1.setRouteId("transform1");
        route1.setStartUri(DIRECT_IN);
        route1.setEndUri(SEDA_BRIDGE);
        route1.setStartupOrder(100);

        TemplateRoute route2 = new TemplateRoute();
        route2.setRouteId("transform2");
        route2.setStartUri(SEDA_BRIDGE);
        route2.setEndUri(MOCK_OUT);
        route2.setStartupOrder(0);

        return new RouteBuilder[] { route1, route2 };
    }

    @Override
    public boolean isUseDebugger() {
        return true;
    }

    @Override
    protected void debugBefore(Exchange exchange, Processor processor, ProcessorDefinition<?> definition, String id, String label) {
        super.debugBefore(exchange, processor, definition, id, label);
    }

    @Test
    public void testTemplateRoute() throws InterruptedException {

        String expectedResponse = "Transformed: Transformed: Hello";

        // add some expectations
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo(expectedResponse);

        // send something to start direct, will run async using seda internally and wait for the response coming back
        String response = template.requestBody(DIRECT_IN, "Hello", String.class);
        assertEquals(expectedResponse, response);

        // assert out expectations (waiting for some amount of milliseconds before throwing exception)
        assertMockEndpointsSatisfied();

    }

}