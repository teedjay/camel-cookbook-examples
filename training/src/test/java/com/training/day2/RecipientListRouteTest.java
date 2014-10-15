package com.training.day2;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class RecipientListRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_SHIPPING = "mock:shipping";
    public static final String MOCK_FAST_SHIPPING = "mock:fastShipping";
    public static final String MOCK_CRM = "mock:crm";

    @Produce(uri = DIRECT_IN)
    ProducerTemplate in;

    @EndpointInject(uri = MOCK_SHIPPING)
    MockEndpoint mockShipping;

    @EndpointInject(uri = MOCK_FAST_SHIPPING)
    MockEndpoint mockFastShipping;

    @EndpointInject(uri = MOCK_CRM)
    MockEndpoint mockCrm;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        RecipientListRoute route = new RecipientListRoute();
        route.setStartUri(DIRECT_IN);
        return route;
    }

    @Test
    public void testGoldCustomer() throws Exception {
        mockShipping.setExpectedMessageCount(0);
        mockFastShipping.setExpectedMessageCount(1);
        mockCrm.setExpectedMessageCount(1);
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("value", 39.99);
        headers.put("customerType", "gold");
        in.sendBodyAndHeaders("Camel Coockbook", headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSilverCustomer() throws Exception {
        mockShipping.setExpectedMessageCount(1);
        mockFastShipping.setExpectedMessageCount(0);
        mockCrm.setExpectedMessageCount(0);
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("value", 39.99);
        headers.put("customerType", "silver");
        in.sendBodyAndHeaders("Camel Coockbook", headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testLimitedEditionBook() throws Exception {
        mockShipping.setExpectedMessageCount(0);
        mockFastShipping.setExpectedMessageCount(1);
        mockCrm.setExpectedMessageCount(0);
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("value", 139.99);
        headers.put("customerType", "silver");
        in.sendBodyAndHeaders("Signed Camel Coockbook", headers);
        assertMockEndpointsSatisfied();
    }

}