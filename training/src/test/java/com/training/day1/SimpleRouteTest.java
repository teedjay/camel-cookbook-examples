package com.training.day1;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleRouteTest {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";

    private CamelContext context;

    @org.junit.Before
    public void setUp() throws Exception {
        SimpleRoute simpleRoute = new SimpleRoute();
        simpleRoute.setStartUri(DIRECT_IN);
        simpleRoute.setEndUri(MOCK_OUT);
        context = new DefaultCamelContext();
        context.addRoutes(simpleRoute);
        context.start();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        context.stop();
    }

    @Test
    public void testSimpleRoute() throws Exception {

        // set some expectations
        MockEndpoint mockOut = context.getEndpoint(MOCK_OUT, MockEndpoint.class);
        mockOut.setExpectedMessageCount(1);             // one message
        mockOut.message(0).body().isEqualTo("I got: Hello");   // expect some content

        // send a message
        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody(DIRECT_IN, "Hello");

        // assert out expectations
        mockOut.assertIsSatisfied();

    }

}