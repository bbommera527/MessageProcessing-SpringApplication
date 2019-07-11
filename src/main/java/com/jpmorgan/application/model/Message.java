package com.jpmorgan.application.model;

import java.math.BigDecimal;


public class Message {

    private String productName;
    private BigDecimal price;
    private BigDecimal priceBeforeAdjustment;
    private boolean isProcessed;
    private MessageType messageType;
    private int noOfQuantity=1;
    private BigDecimal adjustmentPrice;
    private AdjustmentOperation operationType;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPriceBeforeAdjustment() {
        return priceBeforeAdjustment;
    }

    public void setPriceBeforeAdjustment(BigDecimal priceBeforeAdjustment) {
        this.priceBeforeAdjustment = priceBeforeAdjustment;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public int getNoOfQuantity() {
        return noOfQuantity;
    }

    public void setNoOfQuantity(int noOfQuantity) {
        this.noOfQuantity = noOfQuantity;
    }

    public BigDecimal getAdjustmentPrice() {
        return adjustmentPrice;
    }

    public void setAdjustmentPrice(BigDecimal adjustmentPrice) {
        this.adjustmentPrice = adjustmentPrice;
    }

    public AdjustmentOperation getOperationType() {
        return operationType;
    }

    public void setOperationType(AdjustmentOperation operationType) {
        this.operationType = operationType;
    }

    @Override
    public String toString() {
        return "Message{" +
                "productName='" + productName + '\'' +
                ", price=" + price +
                ", noOfQuantity='" + noOfQuantity + '\'' +
                ", priceBeforeAdjustment=" + priceBeforeAdjustment +
                ", messageType=" + messageType +
                ", adjustmentPrice=" + adjustmentPrice +
                ", operationType=" + operationType +
                '}';
    }

}
