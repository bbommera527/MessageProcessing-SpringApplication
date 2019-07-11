package com.jpmorgan.application.util;

import com.jpmorgan.application.model.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is in-memory data store to store messages later this can be replaced by any persistent Database
 */

@Component
public class DataStore {

    private List<Message> messages = Collections.synchronizedList(new ArrayList<>());

    /**
     * This method return list of messages from store
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * This method to store all the incoming messages into memory
     */
    public void addMessage(Message messages) {
        this.messages.add(messages);
    }
}
