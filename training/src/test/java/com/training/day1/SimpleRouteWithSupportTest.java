package com.training.day1;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class SimpleRouteWithSupportTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        SimpleRoute simpleRoute = new SimpleRoute();
        simpleRoute.setStartUri(DIRECT_IN);
        simpleRoute.setEndUri(MOCK_OUT);
        return simpleRoute;
    }

    @Test
    public void testSimpleRoute() throws Exception {

        // set up some expectations
        MockEndpoint mockOut = context.getEndpoint(MOCK_OUT, MockEndpoint.class);
        mockOut.setExpectedMessageCount(1);                    // one message
        mockOut.message(0).body().isEqualTo("I got: Hello");   // expect some content

        // send something
        template.sendBody(DIRECT_IN, "Hello");

        // assert out expectations
        assertMockEndpointsSatisfied();

    }

}