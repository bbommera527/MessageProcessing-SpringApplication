package com.jpmorgan.application.controller;

import com.jpmorgan.application.model.ApplicationTracker;
import com.jpmorgan.application.model.Message;
import com.jpmorgan.application.model.MessageType;
import com.jpmorgan.application.processor.MessageProcessor;
import com.jpmorgan.application.util.DataStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MessageProcessingControllerTest {

    @Autowired
    MessageProcessingController messageProcessingController;

    @Autowired
    ApplicationTracker application;

    @Autowired
    DataStore dataStore;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(messageProcessingController).build();
    }

    @After
    public void tearDown() {
        dataStore.getMessages().clear();
        application.resetMessageCountToZeo();
    }

    /**
     * Process single sell event message and send back ack as accepted
     * @throws Exception throw an exception if an error occurs during testing
     */
    @Test
    public void processSingleSellEventMessage_ok() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/processor/processMessage/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(singleSellEventPayloadOfOneMessage()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertEquals(" Application should not be paused ",Boolean.FALSE, application.isPaused());
        assertEquals(" Message count incremented by ",Boolean.TRUE, application.getRecordedMessageCount() > 0);
        assertEquals(" DataStore should not be empty ",1, dataStore.getMessages().size());
        assertEquals(" Message type should be SINGLE_SELL_EVENT ",MessageType.SINGLE_SELL_EVENT, dataStore.getMessages().get(0).getMessageType());
        assertEquals(" Check the price to confirm the data stored in dataStore ",BigDecimal.valueOf(1), dataStore.getMessages().get(0).getPrice());
        assertEquals(" Check the price to confirm the data stored in dataStore","Apple", dataStore.getMessages().get(0).getProductName());
    }

    /**
     * Process multi sell event message and send back ack as accepted
     * @throws Exception throw an exception if an error occurs during testing
     */
    @Test
    public void processMultiSellEventMessage_ok() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/processor/processMessage/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(multiSellEventPayloadOfOneMessage()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertEquals(Boolean.TRUE, application.getRecordedMessageCount() > 0);
        assertEquals(Boolean.FALSE, application.isPaused());
        assertEquals(1, dataStore.getMessages().size());
        assertEquals(BigDecimal.valueOf(1), dataStore.getMessages().get(0).getPrice());
        assertEquals(2, dataStore.getMessages().get(0).getNoOfQuantity());
        assertEquals(MessageType.MULTI_SELL_EVENT, dataStore.getMessages().get(0).getMessageType());
    }

    /**
     * Process Adjustment -ADD event message and send back ack as accepted
     * @throws Exception throw an exception if an error occurs during testing
     */
    @Test
    public void processAdjustmentEventMessageWithOperation_ADD() throws Exception {

        insertDataIntoDataStore();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/processor/processMessage/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(adjustmentEventPayloadForOperationAdd()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertEquals(Boolean.FALSE, application.isPaused());
        assertEquals(" message count should not increase " +
                "because application is not recording adjustment message count",
                0, application.getRecordedMessageCount());
        assertEquals(3, dataStore.getMessages().size());
        assertEquals(BigDecimal.valueOf(1), dataStore.getMessages().get(0).getPriceBeforeAdjustment());
        assertEquals(" Adjustment : ADD amount 1 to all Samsung Product",BigDecimal.valueOf(2), dataStore.getMessages().get(0).getPrice());
        assertEquals("Samsung", dataStore.getMessages().get(0).getProductName());
    }

    /**
     * Process Adjustment -Subtract event message and send back ack as accepted
     * @throws Exception throw an exception if an error occurs during testing
     */
    @Test
    public void processAdjustmentEventMessageWithOperation_SUBTRACT() throws Exception {

        insertDataIntoDataStore();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/processor/processMessage/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(adjustmentEventPayloadForOperationSubtract()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertEquals(Boolean.FALSE, application.isPaused());
        assertEquals(0, application.getRecordedMessageCount());
        assertEquals(3, dataStore.getMessages().size());
        assertEquals(BigDecimal.valueOf(1), dataStore.getMessages().get(0).getPriceBeforeAdjustment());
        assertEquals("Adjustment : SUBTRACT amount 2 to all Samsung Product",BigDecimal.valueOf(0), dataStore.getMessages().get(0).getPrice());
        assertEquals("Samsung", dataStore.getMessages().get(0).getProductName());
    }

    /**
     * Process Adjustment -Multiply event message and send back ack as accepted
     * @throws Exception throw an exception if an error occurs during testing
     */
    @Test
    public void processAdjustmentEventMessageWithOperation_MULTIPLY() throws Exception {

        insertDataIntoDataStore();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/processor/processMessage/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(adjustmentEventPayloadForOperationMultiply()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertEquals(Boolean.FALSE, application.isPaused());
        assertEquals(0, application.getRecordedMessageCount());
        assertEquals(3, dataStore.getMessages().size());
        assertEquals(BigDecimal.valueOf(1), dataStore.getMessages().get(0).getPriceBeforeAdjustment());
        assertEquals("Adjustment : MULTIPLY amount 2 to all Samsung Product",BigDecimal.valueOf(2), dataStore.getMessages().get(0).getPrice());
        assertEquals("Samsung", dataStore.getMessages().get(0).getProductName());
    }


    /**
     * Process 10 number of messages of Single and Multi message event type
     * @throws Exception throw an exception if an error occurs during testing
     */
    @Test
    public void processMixed10MessagesOfSingleAndMultiSellEventMessage_ok() throws Exception {

        for(int i=0;i<10;i++){
            if (i % 2 == 0){
            processSingleSellEventMessage();
            }else {
                processMultiSellEventMessage();
            }
        }
        assertEquals(Boolean.FALSE, application.isPaused());
        assertEquals(10, dataStore.getMessages().size());
        assertEquals(10, application.getRecordedMessageCount());
    }

    /**
     * Process 50 num of Single and Multi messages event type
     * @throws Exception throw an exception if an error occurs during testing
     */
    @Test
    public void processMixed50MessagesOfAllEventTypes_ok() throws Exception {

        for(int i=0;i<50;i++){
            if (i % 2 == 0){
                if(i==10)
                    processAdjustmentEventMessageAdd();
                processSingleSellEventMessage();
            }else {
                processMultiSellEventMessage();
            }
        }
        assertEquals(Boolean.TRUE, application.isPaused());
        assertEquals(50, dataStore.getMessages().size());
        assertEquals(50, application.getRecordedMessageCount());
    }

    @Test
    public void applicationShouldBePausedAfter50Messages() throws Exception {

        application.setRecordMessageCount(50);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/processor/processMessage/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(singleSellEventPayloadOfOneMessage()))
                .andExpect(MockMvcResultMatchers.status().isLocked());
    }

    private void processSingleSellEventMessage() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/processor/processMessage/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(singleSellEventPayloadOfOneMessage()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    private void processMultiSellEventMessage() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/processor/processMessage/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(multiSellEventPayloadOfOneMessage()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    private void processAdjustmentEventMessageAdd() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/processor/processMessage/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(adjustmentEventPayloadForOperationAdd()))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    private void insertDataIntoDataStore() {
        List<Message> messagesList = new ArrayList<>();
        Message firstSingleEventMessage= new Message();
        firstSingleEventMessage.setProductName("Samsung");
        firstSingleEventMessage.setPrice(BigDecimal.valueOf(1));
        firstSingleEventMessage.setMessageType(MessageType.SINGLE_SELL_EVENT);

        Message secondSingleEventMessage= new Message();
        secondSingleEventMessage.setProductName("Apple");
        secondSingleEventMessage.setPrice(BigDecimal.valueOf(2));
        secondSingleEventMessage.setMessageType(MessageType.SINGLE_SELL_EVENT);

        Message thirdMultiEventMessage= new Message();
        thirdMultiEventMessage.setProductName("Apple");
        thirdMultiEventMessage.setPrice(BigDecimal.valueOf(2));
        thirdMultiEventMessage.setNoOfQuantity(2);
        thirdMultiEventMessage.setMessageType(MessageType.MULTI_SELL_EVENT);

        dataStore.addMessage(firstSingleEventMessage);
        dataStore.addMessage(secondSingleEventMessage);
        dataStore.addMessage(thirdMultiEventMessage);

    }

    private String multiSellEventPayloadOfOneMessage() {
        return "{\"productName\":\"Samsung\",\"price\":\"1\",\"noOfQuantity\":2,\"messageType\":\"MULTI_SELL_EVENT\"}";
    }


    private String singleSellEventPayloadOfOneMessage(){
        return "{\"productName\":\"Apple\",\"price\":\"1\",\"messageType\":\"SINGLE_SELL_EVENT\"}";
    }

    private String adjustmentEventPayloadForOperationAdd(){
        return "{\"productName\":\"Samsung\",\"messageType\":\"ADJUSTMENT_EVENT\",\"adjustmentPrice\":1,\"operationType\":\"ADD\"}";
    }

    private String adjustmentEventPayloadForOperationMultiply(){
        return "{\"productName\":\"Samsung\",\"messageType\":\"ADJUSTMENT_EVENT\",\"adjustmentPrice\":2,\"operationType\":\"MULTIPLY\"}";
    }

    private String adjustmentEventPayloadForOperationSubtract() {
        return "{\"productName\":\"Samsung\",\"messageType\":\"ADJUSTMENT_EVENT\",\"adjustmentPrice\":1,\"operationType\":\"SUBTRACT\"}";
    }

}



