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
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        TemplateRoute route1 = new TemplateRoute();
        route1.setRouteId("transform1");
        route1.setStartUri(DIRECT_IN);
        route1.setEndUri("direct:transform2");
        route1.setStartupOrder(20); // needs route2, startup last (we use 20 to allow more routes later)

        TemplateRoute route2 = new TemplateRoute();
        route2.setRouteId("transform2");
        route2.setStartUri("direct:transform2");
        route2.setEndUri(MOCK_OUT);
        route2.setStartupOrder(10); // startup first (we use 10 to allow for adding more routes later)

        return new RouteBuilder[] { route1, route2 };
    }

    @Test
    public void testTemplateRoute() throws Exception {

        // add some expectations
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("I got: I got: Hello");

        // send something
        template.sendBody("Hello");

        // assert out expectations
        assertMockEndpointsSatisfied();

    }

}