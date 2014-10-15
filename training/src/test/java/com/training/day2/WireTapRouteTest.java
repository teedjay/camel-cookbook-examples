package com.training.day2;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class WireTapRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";
    public static final String MOCK_AUDIT = "mock:audit";

    @Produce(uri = DIRECT_IN)
    ProducerTemplate in;

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

    @EndpointInject(uri = MOCK_AUDIT)
    MockEndpoint mockAudit;

    @Override
    public boolean isUseDebugger() {
        return true;
    }

    @Override
    protected void debugBefore(Exchange exchange, Processor processor, ProcessorDefinition<?> definition, String id, String label) {
        super.debugBefore(exchange, processor, definition, id, label);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        WireTapRoute route = new WireTapRoute();
        route.setStartUri(DIRECT_IN);
        route.setEndUri(MOCK_OUT);
        route.setAuditUri(MOCK_AUDIT);
        return route;
    }

    @Test
    public void testProcessor_no() throws Exception {

        context.start();
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hallo: Some Body");

        mockAudit.setExpectedMessageCount(1);

        in.sendBodyAndHeader("Some Body", "locale", "no");
        assertMockEndpointsSatisfied();
    }

}