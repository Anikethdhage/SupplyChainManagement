package net.corda.samples.supplychain.helper;

import net.corda.core.serialization.CordaSerializable;

import java.time.LocalDateTime;

@CordaSerializable
public class PurchaseOrderData {

    private int purchaseOrderNumber;
    private LocalDateTime creationDate;
    private String productName;
    private String productCode;
    private double qunatity;
    private LocalDateTime lastUpdatedDate;
    private String lastUpdatedBy;

    public PurchaseOrderData(int purchaseOrderNumber, LocalDateTime creationDate, String productName, String productCode, double qunatity, LocalDateTime lastUpdatedDate, String lastUpdatedBy) {
        this.purchaseOrderNumber = purchaseOrderNumber;
        this.creationDate = LocalDateTime.now();
        this.productName = productName;
        this.productCode = productCode;
        this.qunatity = qunatity;
        this.lastUpdatedDate = LocalDateTime.now();
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public PurchaseOrderData() {
        this.purchaseOrderNumber = purchaseOrderNumber;
        this.creationDate = creationDate;
        this.productName = productName;
        this.productCode = productCode;
        this.qunatity = qunatity;
        this.lastUpdatedDate = lastUpdatedDate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(int purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public double getQunatity() {
        return qunatity;
    }

    public void setQunatity(double qunatity) {
        this.qunatity = qunatity;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Override
    public String toString() {
        return "PurchaseOrderData{" +
                "purchaseOrderNumber=" + purchaseOrderNumber +
                ", creationDate=" + creationDate +
                ", productName='" + productName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", qunatity=" + qunatity +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                '}';
    }
}
