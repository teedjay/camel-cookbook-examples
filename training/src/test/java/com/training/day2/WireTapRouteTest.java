package com.training.day2;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.net.ConnectException;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class WireTapRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";
    public static final String MOCK_AUDIT = "mock:audit";
    public static final String DIRECT_SLOW_AUDIT_BACKEND = "direct:slowAuditBackend";

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
    protected RouteBuilder[] createRouteBuilders() throws Exception {

        WireTapRoute route = new WireTapRoute();
        route.setStartUri(DIRECT_IN);
        route.setEndUri(MOCK_OUT);
        route.setAuditUri(MOCK_AUDIT);

        return new RouteBuilder[] { route };

    }

    @Test
    public void testProcessor_no() throws Exception {

        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hallo: Some Body");

        mockAudit.setExpectedMessageCount(4);           // 1 + 3 redelivery attempts
        mockAudit.setMinimumExpectedMessageCount(4);    // 1 + 3 redelivery attempts
        mockAudit.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw new ConnectException("Network failure");
            }
        });

        in.sendBodyAndHeader("Some Body", "locale", "no");

        assertMockEndpointsSatisfied(); // asserts all injected mocks

    }

}