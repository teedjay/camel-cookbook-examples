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

        String subRouteId = "direct:sub" + routeId;

        from(startUri)
                .routeId(routeId)
                .startupOrder(startupOrder + 20)
            .log("Received message: ${body}")
            .to(subRouteId)
            .to(endUri);

        from(subRouteId)
                .routeId(routeId + ".sub")
                .startupOrder(startupOrder + 10)
            .transform()
                .simple("Transformed: ${body}");

    }

}
