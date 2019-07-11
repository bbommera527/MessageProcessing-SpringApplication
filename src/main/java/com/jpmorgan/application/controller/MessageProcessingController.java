package com.jpmorgan.application.controller;

import com.jpmorgan.application.model.Message;
import com.jpmorgan.application.model.ApplicationTracker;
import com.jpmorgan.application.processor.MessageProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is starting point of application.
 * External system will send messages on below mentioned rest end point.
 * Later this class can be replaced by JMS listener class if required
 */

@RestController
@RequestMapping("/processor")
public class MessageProcessingController {

    private static final Logger logger = LogManager.getLogger(MessageProcessingController.class);

    @Autowired
    private MessageProcessor messageProcessor;

    @Autowired
    ApplicationTracker application;


    /**
     * This method will process all types of messages
     */
    @RequestMapping(value = "/processMessage/", method = RequestMethod.POST)//, consumes = {"application/json"})
    public ResponseEntity processMessage(@RequestBody Message message) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = null;
        if(application.isPaused())
        {
            logger.info("Application is Now Paused :");
            status = HttpStatus.LOCKED;
        }else if(messageProcessor.processMessage(message))
        {
            status = HttpStatus.ACCEPTED;
        }
        else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<String>(headers, status);
    }
}



