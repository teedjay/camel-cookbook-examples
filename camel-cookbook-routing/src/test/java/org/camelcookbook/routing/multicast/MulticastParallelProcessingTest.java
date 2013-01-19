package org.camelcookbook.routing.multicast;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MulticastParallelProcessingTest extends CamelTestSupport {

    public static final String MESSAGE_BODY = "Message to be multicast";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new MulticastParallelProcessingRouteBuilder();
    }

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:first")
    private MockEndpoint mockFirst;

    @EndpointInject(uri = "mock:second")
    private MockEndpoint mockSecond;

    @EndpointInject(uri = "mock:afterMulticast")
    private MockEndpoint afterMulticast;

    @Test
    public void testAllMessagesParticipateInDifferentTransactions() throws InterruptedException {
        afterMulticast.setExpectedMessageCount(1);
        mockFirst.setExpectedMessageCount(1);
        mockSecond.setExpectedMessageCount(1);

        template.sendBody(MESSAGE_BODY);

        assertMockEndpointsSatisfied();

        // check that all of the messages participated in different transactions
        assertNotEquals(getExchange(afterMulticast).getUnitOfWork(), getExchange(mockFirst).getUnitOfWork());
        assertNotEquals(getExchange(afterMulticast).getUnitOfWork(), getExchange(mockSecond).getUnitOfWork());
    }


    @Test
    public void testAllEndpointsReachedByDifferentThreads() throws InterruptedException {
        afterMulticast.setExpectedMessageCount(1);
        mockFirst.setExpectedMessageCount(1);
        mockFirst.message(0).exchangePattern().equals(ExchangePattern.InOnly);
        mockSecond.setExpectedMessageCount(1);
        mockSecond.message(0).exchangePattern().equals(ExchangePattern.InOnly);

        String response = (String) template.requestBody(MESSAGE_BODY);
        assertEquals("response", response);

        assertMockEndpointsSatisfied();

        // check that all of the mock endpoints were reached by the different threads
        String mainThreadName = getExchange(afterMulticast).getIn().getHeader("threadName", String.class);
        String firstThreadName = getExchange(mockFirst).getIn().getHeader("threadName", String.class);
        String secondThreadName = getExchange(mockSecond).getIn().getHeader("threadName", String.class);
        assertNotEquals(firstThreadName, mainThreadName);
        assertNotEquals(firstThreadName, secondThreadName);
        assertNotEquals(mainThreadName, secondThreadName);
    }

    private Exchange getExchange(MockEndpoint mock) {
        return mock.getExchanges().get(0);
    }

}