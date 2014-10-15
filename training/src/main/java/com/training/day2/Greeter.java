package com.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * Simple bean binding example - no dependencies - can be tested as a simple POJO without Camel at all
 * @author thore
 */
public class Greeter {

    public String greet(String originalText, String locale) throws Exception {
        if (locale == null) {
            return "Hello: " + originalText;
        } else {
            if ("no".equalsIgnoreCase(locale)) {
                return "Hallo: " + originalText;
            } else if ("se".equalsIgnoreCase(locale)) {
                return "Bork bork bork: " + originalText;
            } else {
                return "Hello: " + originalText;
            }
        }
    }

}
