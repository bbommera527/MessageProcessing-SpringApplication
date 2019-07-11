package com.jpmorgan.application.model;

import org.springframework.stereotype.Component;

@Component
public class ApplicationTracker {

    private int messageReceived = 0;

    /**
     * This method will return true if message count reached to 20 and application will be paused.
     */
    public boolean isPaused() {
        return messageReceived == 50;
    }

    /**
     * This method responsible for incrementing message count
     */
    public synchronized void recordMessage(){
        messageReceived++;
    }

    /**
     * This method returns current recorded message count
     */
    public int getRecordedMessageCount(){
        return messageReceived;
    }

    /**
     * Set message count to Zero
     */
    public void resetMessageCountToZeo() {
        this.messageReceived = 0;
    }

    public void setRecordMessageCount(int messageReceived){
        this.messageReceived = messageReceived;
    }
}
