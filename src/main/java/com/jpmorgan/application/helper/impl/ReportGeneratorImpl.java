package com.jpmorgan.application.helper.impl;

import com.jpmorgan.application.helper.ReportGenerator;
import com.jpmorgan.application.model.Message;
import com.jpmorgan.application.util.DataStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * This class is responsible for generating reports and printing on console.
 * Later output to console can be replaced with file
 *
 */

@Component
public class ReportGeneratorImpl implements ReportGenerator {

    @Autowired
    private DataStore dataStore;

    private static String REPORT_SEPRATOR_LINE = "--------------------------------------------------";
    private static String TOTAL_SALE_REPORT_LABEL = "----------TOTAL SALE AMOUNT--------------{}";

    private static final Logger logger = LogManager.getLogger(ReportGeneratorImpl.class);

    /**
     * This method to print single and multi sale messages report
     */
    @Override
    public void printSingleAndMultiSaleReport() {
        logger.info("----------------Sales Report Start----------------");
        BigDecimal totalSale = BigDecimal.ZERO;
        for (Message message : dataStore.getMessages()) {
            if (!message.isProcessed()) {
                logger.info(printSingleAndMultiSaleData(message));
                totalSale = totalSale.add(message.getPrice().multiply(BigDecimal.valueOf(message.getNoOfQuantity())));
                message.setProcessed(Boolean.TRUE);
            }
        }
        logger.info(REPORT_SEPRATOR_LINE);
        logger.info(TOTAL_SALE_REPORT_LABEL, totalSale);
        logger.info(REPORT_SEPRATOR_LINE);
    }

    /**
     * This method to print adjustment report
     */
    @Override
    public void printAdjustmentsReport() {
        logger.info("==========Adjustment Report Start==============");
        BigDecimal totalSale = BigDecimal.ZERO;
        for (Message message : dataStore.getMessages()) {

                logger.info(printAdjustmentData(message));
                totalSale = totalSale.add(message.getPrice().multiply(BigDecimal.valueOf(message.getNoOfQuantity())));

        }
        logger.info(REPORT_SEPRATOR_LINE);
        logger.info(TOTAL_SALE_REPORT_LABEL, totalSale);
        logger.info(REPORT_SEPRATOR_LINE);
    }

    private String printSingleAndMultiSaleData(Message message) {
        return "{" +
                "productName='" + message.getProductName() + '\'' +
                ", price=" + message.getPrice() +
                ", noOfQuantity='" + message.getNoOfQuantity() + '\'' +
                '}';
    }

    private String printAdjustmentData(Message message) {
        return "{" +
                "productName='" + message.getProductName() + '\'' +
                ", price=" + message.getPrice() +
                ", noOfQuantity='" + message.getNoOfQuantity() + '\'' +
                ", priceBeforeAdjustment=" + message.getPriceBeforeAdjustment() +
                ", messageType=" + message.getMessageType() +
                ", adjustmentPrice=" + message.getAdjustmentPrice() +
                '}';
    }

}
