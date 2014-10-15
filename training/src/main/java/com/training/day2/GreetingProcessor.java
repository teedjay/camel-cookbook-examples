package com.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * Simple processor example - relies on Camel Processor
 * @author thore
 */
public class GreetingProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        Message in = exchange.getIn();
        String originalText = in.getBody(String.class);
        String locale = in.getHeader("locale", String.class);

        if (locale == null) {
            in.setBody("Hello: " + originalText);
        } else {
            if ("no".equalsIgnoreCase(locale)) {
                in.setBody("Hallo: " + originalText);
            } else if ("se".equalsIgnoreCase(locale)) {
                in.setBody("Bork bork bork: " + originalText);
            } else {
                in.setBody("Hello: " + originalText);
            }
        }

    }

}
