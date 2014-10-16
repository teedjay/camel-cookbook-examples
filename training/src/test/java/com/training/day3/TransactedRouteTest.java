package com.training.day3;

import com.training.utils.EmbeddedActiveMQBroker;
import com.training.utils.ExceptionThrowingProcessor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.jms.connection.JmsTransactionManager;

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

    @EndpointInject(uri = MOCK_SERVICE)
    MockEndpoint mockService;

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

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(broker.getTcpConnectorUri());
        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(connectionFactory);

        ActiveMQComponent jms = new ActiveMQComponent();
        jms.setConnectionFactory(connectionFactory);
        jms.setTransactionManager(transactionManager);

        SpringTransactionPolicy policy = new SpringTransactionPolicy();
        policy.setTransactionManager(transactionManager);
        policy.setPropagationBehaviorName("PROPAGATION_REQUIRED");

        SimpleRegistry registry = new SimpleRegistry();
        registry.put("transactionManager", transactionManager);
        registry.put("jms.required", policy);

        DefaultCamelContext context = new DefaultCamelContext(registry);
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

        mockService.setExpectedMessageCount(2);
        mockService.whenExchangeReceived(2, new ExceptionThrowingProcessor());

        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Good Body");

        in.sendBody("Good Body");
        in.sendBody("Bad Body");

        assertMockEndpointsSatisfied();

    }

}