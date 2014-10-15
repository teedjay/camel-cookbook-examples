package com.training.day2;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class EnricherRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_SERVICE = "mock:service";
    public static final String MOCK_OUT = "mock:out";

    @Produce(uri = DIRECT_IN)
    ProducerTemplate in;

    @EndpointInject(uri = MOCK_SERVICE)
    MockEndpoint mockService;

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        EnricherRoute route = new EnricherRoute();
        route.setStartUri(DIRECT_IN);
        route.setServiceUri(MOCK_SERVICE);
        route.setEndUri(MOCK_OUT);
        return route;
    }

    @Test
    public void testEnricher() throws Exception {

        mockOut.setExpectedMessageCount(2);
        mockOut.message(0).body().isEqualTo("Ping:Pong");
        mockOut.message(1).body().isEqualTo("Pong:Ping");

        mockService.setExpectedMessageCount(2);

        mockService.whenExchangeReceived(1, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Pong");
            }
        });

        mockService.whenExchangeReceived(2, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Ping");
            }
        });

        in.sendBody("Ping");
        in.sendBody("Pong");

        assertMockEndpointsSatisfied();
    }

}