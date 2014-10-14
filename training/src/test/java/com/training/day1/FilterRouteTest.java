package com.training.day1;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Testing some simple filtering
 */
public class FilterRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";
    public static final String MOCK_SWEDISH_LOG = "mock:swedishLog";

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

    @Produce(uri = DIRECT_IN)
    ProducerTemplate template;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        FilterRoute route = new FilterRoute();
        route.setStartUri(DIRECT_IN);
        route.setEndUri(MOCK_OUT);
        return route;
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    public boolean isUseDebugger() {
        return true;
    }

    @Override
    public String isMockEndpointsAndSkip() {
        return "file:*"; // skip all file: endpoints :D
    }

    @Override
    protected void debugBefore(Exchange exchange, Processor processor, ProcessorDefinition<?> definition, String id, String label) {
        super.debugBefore(exchange, processor, definition, id, label);
    }

    @Test
    public void testWithNoLocale() throws Exception {
        context.start();
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("I got: Hello");
        template.sendBody("Hello");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testWithSwedishLocale() throws Exception {

        // redefine the route configuration
        context.getRouteDefinition(FilterRoute.CUSTOM_TRANSFORM)
                .adviceWith(context, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById(FilterRoute.ID_SWEDISH_LOG)
                                .after().to(MOCK_SWEDISH_LOG);
                    }
                });

        context.start();

        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("I got: Hello");

        MockEndpoint mockSwedishLogs = getMockEndpoint(MOCK_SWEDISH_LOG);
        mockSwedishLogs.setExpectedMessageCount(1);

        MockEndpoint mockFileEndpoint = getMockEndpoint("mock:file:swedishMessage");
        mockFileEndpoint.setExpectedMessageCount(1);

        template.sendBodyAndHeader("Hello", "locale", "se");

        assertMockEndpointsSatisfied();

    }


}