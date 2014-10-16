package com.training.day3;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
* Simple aggregation using string concatination
*
* @author thore
*/
class ConcatinatingStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange; // first item to aggregate will not have an oldExchange
        }
        String s1 = oldExchange.getIn().getBody(String.class);
        String s2 = newExchange.getIn().getBody(String.class);
        oldExchange.getIn().setBody(s1 + ":" + s2);
        return oldExchange;
    }

}
