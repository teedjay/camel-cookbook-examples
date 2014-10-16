package com.training.day3;

import com.training.utils.EmbeddedActiveMQBroker;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Rule;
import org.junit.Test;

/**
 * Camel has built-in test support that does setup / tear down, creates mock endpoints etc
 */
public class TransactedRouteTest extends CamelTestSupport {

    public static final String DIRECT_IN = "direct:in";
    public static final String MOCK_OUT = "mock:out";
    public static final String MOCK_SERVICE = "mock:service";

    @Rule // rule is cool : "at the start call the before() inside the class, at the end run after()"
    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker("myEmbeddedBroker");

    @Produce(uri = DIRECT_IN)
    ProducerTemplate in;

    @EndpointInject(uri = MOCK_OUT)
    MockEndpoint mockOut;

    @Override
    public boolean isUseDebugger() {
        return true;
    }

    @Override
    protected void debugBefore(Exchange exchange, Processor processor, ProcessorDefinition<?> definition, String id, String label) {
        super.debugBefore(exchange, processor, definition, id, label);
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        ActiveMQComponent jms = new ActiveMQComponent();
        jms.setBrokerURL(broker.getTcpConnectorUri());
        DefaultCamelContext context = new DefaultCamelContext();
        context.addComponent("jms", jms);
        return context;
    }

    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        TransactedRoute route = new TransactedRoute();

        route.setStartUri("jms:in");
        route.setEndUri("jms:out");
        route.setServiceUri(MOCK_SERVICE);

        RouteBuilder harness = new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from(DIRECT_IN)
                    .log("Sending ${body}")
                    .to("jms:in")
                ;

                from("jms:out")
                    .log("Received ${body}")
                    .to(MOCK_OUT)
                ;

            }
        };

        return new RouteBuilder[] { route, harness };
    }

    @Test
    public void testTransaction() throws Exception {

        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Some Body");

        in.sendBody("Some Body");

        assertMockEndpointsSatisfied();

    }

}