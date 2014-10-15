package com.training.day2;

import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;

/**
 * Simple recipient routing, using some header values to decide rules
 * @author thore
 */
public class RecipientListRoute extends RouteBuilder {

    private String startUri;

    public void setStartUri(String startUri) {
        this.startUri = startUri;
    }

    @Override
    public void configure() throws Exception {

        from(startUri).routeId("orderRouting")
            .recipientList(method(OrderRouter.class))
                .parallelProcessing();

    }

    public static class OrderRouter {

        public String route(
                @Header("value") Double orderValue,
                @Header("customerType") String customerType) {

            if ("gold".equals(customerType)) {
                return "mock:crm,mock:fastShipping"; // dynamic multicast and they all share the same transaction
            } else {
                if (orderValue > 100) {
                    return "mock:fastShipping";
                } else {
                    return "mock:shipping";
                }
            }

        }

    }

}
