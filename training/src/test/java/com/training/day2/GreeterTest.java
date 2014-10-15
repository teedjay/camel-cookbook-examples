package com.training.day2;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class GreeterTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

    @Produce(uri = DIRECT_IN)
    ProducerTemplate in;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(DIRECT_IN)
                    .setHeader("additionalGreeting", method(new Greeter(), "greet(${body}, ${header[locale]})"))
                    .transform(method(new Greeter(), "greet(${body}, ${header[locale]})"))
                    .to(MOCK_OUT);
            }
        };
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
    public void testProcessor_noLocale() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hello: Good people");
        mockOut.message(0).header("additionalGreeting").isEqualTo("Hello: Good people");
        in.sendBody("Good people");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testProcessor_no() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hallo: Good people");
        in.sendBodyAndHeader("Good people", "locale", "no");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testProcessor_se() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Bork bork bork: Good people");
        in.sendBodyAndHeader("Good people", "locale", "se");
        assertMockEndpointsSatisfied();
    }

}