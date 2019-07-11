package com.jpmorgan.application.processor.impl;

import com.jpmorgan.application.helper.ReportGenerator;
import com.jpmorgan.application.model.ApplicationTracker;
import com.jpmorgan.application.model.Message;
import com.jpmorgan.application.model.MessageType;
import com.jpmorgan.application.processor.MessageProcessor;
import com.jpmorgan.application.repository.impl.MessageRepositoryImpl;
import com.jpmorgan.application.util.DataStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MessageProcessorImpl implements MessageProcessor {

    static final Logger logger = LogManager.getLogger(MessageProcessorImpl.class);

    @Autowired
    private MessageRepositoryImpl messageRepositoryImpl;

    @Autowired
    private ReportGenerator reportGenerator;

    @Autowired
    DataStore dataStore;

    @Autowired
    ApplicationTracker applicationTracker;

    /**
     * This method will start processing the received message and does following things
     *
     * 1 validate message
     * 2 record message
     * 3 if message type is adjustment it will update data store for existing stock
     * 4 if message type is single or multi sale type then process and store in data store
     * 5 call the report generator method which will print data on console after 10th message
     */
    @Override
    public boolean processMessage(Message message) {
        logger.info("Received message type {} for processing : {}", message.getMessageType());

        //first validate the received message if is valid then only process otherwise return false
        if (validateMessage(message)){

            //check if message type is Adjustment then update store and return
            if (MessageType.ADJUSTMENT_EVENT.equals(message.getMessageType())) {
                return updateProductPricesInDataStore(message);
            }

            //record the message
            applicationTracker.recordMessage();

            //proceed to store the message into data store
            messageRepositoryImpl.processMessage(message);

            //send for report printing method
            generateMessageReport();

            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

    /**
     * This method is responsible for validating incoming messages as per message type
     * NOTE : Validation needs to be implemented based on MessageType
     */
    private boolean validateMessage(Message message) {
        return Boolean.TRUE;
    }

    /**
     * Retrieve the messages from store and apply filter on product name
     */
    private boolean updateProductPricesInDataStore(Message currentMessage){
       dataStore.getMessages()
               .stream()
               .filter(message -> message.getProductName().equals(currentMessage.getProductName()))
               .forEach(message -> updatePriceForExistingProductsIntoStore(message, currentMessage));
       return Boolean.TRUE;
    }

    /**
     * Update price in store for filtered products in above method
     */
    private void updatePriceForExistingProductsIntoStore(Message messageFromStore, Message currentMessage){

        messageFromStore.setPriceBeforeAdjustment(messageFromStore.getPrice());
        messageFromStore.setAdjustmentPrice(currentMessage.getAdjustmentPrice());

        switch (currentMessage.getOperationType()) {
            case ADD:
                messageFromStore.setPrice(messageFromStore.getPrice().add(currentMessage.getAdjustmentPrice())
                        .multiply(BigDecimal.valueOf(messageFromStore.getNoOfQuantity())));
                break;
            case SUBTRACT:
                messageFromStore.setPrice(messageFromStore.getPrice().subtract(currentMessage.getAdjustmentPrice())
                        .multiply(BigDecimal.valueOf(messageFromStore.getNoOfQuantity())));
                break;
            case MULTIPLY:
                messageFromStore.setPrice(messageFromStore.getPrice().multiply(currentMessage.getAdjustmentPrice())
                        .multiply(BigDecimal.valueOf(messageFromStore.getNoOfQuantity())));
                break;
            default:
                break;
        }
    }

    /**
     * This method responsible checking if message count is 10
     * then print sale report and if message count
     * is 20 then print report with adjustments made into data store
     */
    private void generateMessageReport() {
        if (applicationTracker.getRecordedMessageCount() % 10 == 0) {
            reportGenerator.printSingleAndMultiSaleReport();
        }
        if (applicationTracker.getRecordedMessageCount() == 50) {
            reportGenerator.printAdjustmentsReport();
            applicationNowPaused();
        }
    }

    /**
     * This method displays application is paused Now on console
     */
    private void applicationNowPaused() {
        logger.info("============================================================");
        logger.info("==  50 messages processed, the application is now paused  ==");
        logger.info("============================================================");
    }
}
