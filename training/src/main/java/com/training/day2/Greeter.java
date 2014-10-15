package com.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * Simple bean binding example - can be tested as a simple POJO without Camel
 * But it now uses annotations to hint Camel on where to pick up values
 * @author thore
 */
public class Greeter {

    public String greet(
            String originalText, // Body is default, but could annotate with @XPath or others - to inject parts of payload
            @Header("locale") String locale) throws Exception {
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
