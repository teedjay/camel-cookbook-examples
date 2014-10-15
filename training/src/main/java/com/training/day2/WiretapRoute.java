package com.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.net.ConnectException;

/**
 * Adding wire tapping - for auditing in the background and other uses.
 * @author thore
 */
public class WireTapRoute extends RouteBuilder {

    private String startUri;
    private String endUri;
    private String auditUri;

    public void setStartUri(String startUri) {
        this.startUri = startUri;
    }

    public void setEndUri(String endUri) {
        this.endUri = endUri;
    }

    public void setAuditUri(String auditUri) {
        this.auditUri = auditUri;
    }

    @Override
    public void configure() throws Exception {

        // add any post processing here
        //onCompletion()
            //.onCompleteOnly()
            //    .log("Message completed successfully");
            //.onFailureOnly()
            //    .log("Message failed");

        // introducing a global exception handler
        onException(ConnectException.class)
                .onWhen(simple("${exception.message} contains 'Network'"))
                .maximumRedeliveries(3)
                .redeliveryDelay(1000)
            .log("Problem connection to backend: ${exception.message}")
            .log("Attempted ${header[" + Exchange.REDELIVERY_COUNTER + "]} redeliveries")
            //.handled(true)      // finish processing
            //.continued(true)    // go to next processing step
        .end();

        from(startUri).routeId("greeterRoute").startupOrder(30)
            .choice()
                .when(simple("${header[locale]} == 'no'"))
                    .wireTap("direct:audit")
                    .transform(simple("Hallo: ${body}"))
                .endChoice()
                .when(simple("${header[locale]} == 'se'"))
                    .transform(simple("Bork bork bork: ${body}"))
                .when(simple("${header[locale]} == 'ru'"))
                    .to("direct:russian")
                .otherwise()
                    .transform(simple("Hello: ${body}"))
            .end()
            .setHeader("additionalGreeting", body())
            .to(endUri);

        from("direct:russian").routeId("russianLogging").startupOrder(20)
            .log("I have no idea on how to speak russian").id("logRussian");

        from("direct:audit").routeId("audit").startupOrder(10)
            .to(auditUri)
            .log("Audit system responded : ${body}");

    }

}
