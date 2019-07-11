package com.jpmorgan.application.processor;

import com.jpmorgan.application.model.Message;


public interface MessageProcessor {

    /**
     * This method will start processing the received message
     */
    boolean processMessage(Message message);
}
