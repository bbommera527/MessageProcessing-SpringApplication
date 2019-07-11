package com.jpmorgan.application.repository.impl;

import com.jpmorgan.application.model.Message;
import com.jpmorgan.application.repository.MessageRepository;
import com.jpmorgan.application.util.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MessageRepositoryImpl implements MessageRepository{

    @Autowired
    DataStore dataStore;

    /**
     * This method process the received message to datastore
     */
    @Override
    public void processMessage(Message message) {
        dataStore.addMessage(message);
    }
}



