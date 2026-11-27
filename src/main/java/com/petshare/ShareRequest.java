package com.petshare;

import java.sql.Timestamp;

public class ShareRequest {

    private int requestId;
    private int petId;

    // ========================================
    // シェア申込み時点のペット情報
    // ========================================

    private String petName;
    private String petImage;
    private String petType;
    private String petGender;
    private Integer ownerId;

    private int borrowerId;

    private Timestamp startDatetime;
    private Timestamp endDatetime;

    private double usageHours;
    private int sharePrice;

    private String status;
    private Timestamp createdAt;

    public ShareRequest() {
    }

    // ========================================
    // requestId
    // ========================================

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    // ========================================
    // petId
    // ========================================

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    // ========================================
    // petName
    // ========================================

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    // ========================================
    // petImage
    // ========================================

    public String getPetImage() {
        return petImage;
    }

    public void setPetImage(String petImage) {
        this.petImage = petImage;
    }

    // ========================================
    // petType
    // ========================================

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    // ========================================
    // petGender
    // ========================================

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    // ========================================
    // ownerId
    // ========================================

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    // ========================================
    // borrowerId
    // ========================================

    public int getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }

    // ========================================
    // startDatetime
    // ========================================

    public Timestamp getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Timestamp startDatetime) {
        this.startDatetime = startDatetime;
    }

    // ========================================
    // endDatetime
    // ========================================

    public Timestamp getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Timestamp endDatetime) {
        this.endDatetime = endDatetime;
    }

    // ========================================
    // usageHours
    // ========================================

    public double getUsageHours() {
        return usageHours;
    }

    public void setUsageHours(double usageHours) {
        this.usageHours = usageHours;
    }

    // ========================================
    // sharePrice
    // ========================================

    public int getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(int sharePrice) {
        this.sharePrice = sharePrice;
    }

    // ========================================
    // status
    // ========================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ========================================
    // createdAt
    // ========================================

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}