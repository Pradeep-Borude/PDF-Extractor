package com.pradeep.pdfextractor.model;

import java.util.Objects;

public class Invoice {

    /* Header */

    private String invoiceNo;
    private String invoiceDate;
    private String vehicleNo;
    private String transporter;
    private String customer;

    /* Item */

    private String partNo;

    private int quantity;

    /* Weight */

    private double totalWeight;
    private double weightPerPart;

    /* Packing */

    private int partsPerBin;
    private int totalBins;

    private double binWeight;
    private double totalBinWeight;

    private double netWeight;

    /* Price */

    private double rate;
    private double amount;

    public Invoice() {
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = trim(invoiceNo);
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = trim(invoiceDate);
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = trim(vehicleNo);
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = trim(transporter);
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = trim(customer);
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = trim(partNo);
    }

 

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public double getWeightPerPart() {
        return weightPerPart;
    }

    public void setWeightPerPart(double weightPerPart) {
        this.weightPerPart = weightPerPart;
    }

    public int getPartsPerBin() {
        return partsPerBin;
    }

    public void setPartsPerBin(int partsPerBin) {
        this.partsPerBin = partsPerBin;
    }

    public int getTotalBins() {
        return totalBins;
    }

    public void setTotalBins(int totalBins) {
        this.totalBins = totalBins;
    }

    public double getBinWeight() {
        return binWeight;
    }

    public void setBinWeight(double binWeight) {
        this.binWeight = binWeight;
    }

    public double getTotalBinWeight() {
        return totalBinWeight;
    }

    public void setTotalBinWeight(double totalBinWeight) {
        this.totalBinWeight = totalBinWeight;
    }

    public double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(double netWeight) {
        this.netWeight = netWeight;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceNo='" + invoiceNo + '\'' +
                ", invoiceDate='" + invoiceDate + '\'' +
                ", vehicleNo='" + vehicleNo + '\'' +
                ", transporter='" + transporter + '\'' +
                ", customer='" + customer + '\'' +
                ", partNo='" + partNo + '\'' +
                ", quantity=" + quantity +
                ", totalWeight=" + totalWeight +
                ", weightPerPart=" + weightPerPart +
                ", partsPerBin=" + partsPerBin +
                ", totalBins=" + totalBins +
                ", binWeight=" + binWeight +
                ", totalBinWeight=" + totalBinWeight +
                ", netWeight=" + netWeight +
                ", rate=" + rate +
                ", amount=" + amount +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceNo);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof Invoice other))
            return false;

        return Objects.equals(invoiceNo, other.invoiceNo);
    }

}