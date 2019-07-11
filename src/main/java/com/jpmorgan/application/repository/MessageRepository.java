package com.jpmorgan.application.repository;

import com.jpmorgan.application.model.Message;


public interface MessageRepository {

    void processMessage(Message message);

}
