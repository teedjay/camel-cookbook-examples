package com.training.day1;

import org.apache.camel.builder.RouteBuilder;

public class TemplateRoute extends RouteBuilder {

    private String routeId = "templateRoute";   // nice defaults
    private String startUri;
    private String endUri;
    private int startupOrder = 0 ;

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public void setStartUri(String startUri) {
        this.startUri = startUri;
    }

    public void setEndUri(String endUri) {
        this.endUri = endUri;
    }

    public void setStartupOrder(int startupOrder) {
        this.startupOrder = startupOrder;
    }

    @Override
    public void configure() throws Exception {
        from(startUri)
                .routeId(routeId)
                .startupOrder(startupOrder)
            .log("Received message: ${body}")
            .transform()
                .simple("I got: ${body}")       // you have xpath and loads others
            .to(endUri);
    }

}
