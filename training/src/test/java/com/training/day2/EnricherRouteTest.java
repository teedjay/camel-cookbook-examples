package com.training.day2;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
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
    public static final String MOCK_OUT = "mock:out";

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

    @Produce(uri = DIRECT_IN)
    ProducerTemplate in;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }



    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        GreeterRoute greeterRoute = new GreeterRoute();
        greeterRoute.setStartUri(DIRECT_IN);
        greeterRoute.setEndUri(MOCK_OUT);
        return greeterRoute;
    }

    @Test
    public void testProcessor_noLocale() throws Exception {
        context.start();
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hello: Some Body");
        mockOut.message(0).header("additionalGreeting").isEqualTo("Hello: Some Body");
        in.sendBody("Some Body");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testProcessor_ru() throws Exception {

        context.getRouteDefinition("russianLogging")
                .adviceWith(context, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("logRussian").before().to("mock:logRussian");
                    }
                });

        context.start();

        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Some Body");
        mockOut.message(0).header("additionalGreeting").isEqualTo("Some Body");

        MockEndpoint mockRussian = getMockEndpoint("mock:logRussian");
        mockRussian.setExpectedMessageCount(1);

        in.sendBodyAndHeader("Some Body", "locale", "ru");

        assertMockEndpointsSatisfied();

    }

    @Test
    public void testProcessor_no() throws Exception {
        context.start();
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hallo: Some Body");
        in.sendBodyAndHeader("Some Body", "locale", "no");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testProcessor_se() throws Exception {
        context.start();
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Bork bork bork: Some Body");
        in.sendBodyAndHeader("Some Body", "locale", "se");
        assertMockEndpointsSatisfied();
    }

}