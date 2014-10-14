package com.training.day1;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Testing some simple filtering
 */
public class FilterRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";

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

    @Test
    public void testWithNoLocale() throws Exception {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("I got: Hello");
        template.sendBody("Hello");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testWithSwedishLocale() throws Exception {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("I got: Hello");
        template.sendBodyAndHeader("Hello", "locale", "se");
        assertMockEndpointsSatisfied();
    }


}