package com.training.day1;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class TempateRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

    @Produce(uri = DIRECT_IN)
    ProducerTemplate template;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        TemplateRoute route = new TemplateRoute();
        route.setStartUri(DIRECT_IN);
        route.setEndUri(MOCK_OUT);
        return route;
    }

    @Test
    public void testTemplateRoute() throws Exception {

        // add some expectations
        mockOut.setExpectedMessageCount(1);                    // one message
        mockOut.message(0).body().isEqualTo("I got: Hello");   // expect some content

        // send something
        template.sendBody("Hello");

        // assert out expectations
        assertMockEndpointsSatisfied();

    }

}