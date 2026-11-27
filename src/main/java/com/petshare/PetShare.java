package com.petshare;

import java.sql.Timestamp;

public class PetShare {

    // =====================================
    // 基本情報
    // =====================================

    private int shareId;
    private int petId;

    // =====================================
    // シェア時点のペット情報
    // =====================================

    private String petName;
    private String petImage;
    private String petType;
    private String petGender;
    private Integer ownerId;
    private Integer sharePrice;

    // =====================================
    // シェア期間
    // =====================================

    private Timestamp availableStart;
    private Timestamp availableEnd;

    // =====================================
    // 管理情報
    // =====================================

    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // =====================================
    // コンストラクタ
    // =====================================

    public PetShare() {
    }

    // =====================================
    // shareId
    // =====================================

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    // =====================================
    // petId
    // =====================================

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    // =====================================
    // petName
    // =====================================

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    // =====================================
    // petImage
    // =====================================

    public String getPetImage() {
        return petImage;
    }

    public void setPetImage(String petImage) {
        this.petImage = petImage;
    }

    // =====================================
    // petType
    // =====================================

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    // =====================================
    // petGender
    // =====================================

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    // =====================================
    // ownerId
    // =====================================

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    // =====================================
    // sharePrice
    // =====================================

    public Integer getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(Integer sharePrice) {
        this.sharePrice = sharePrice;
    }

    // =====================================
    // availableStart
    // =====================================

    public Timestamp getAvailableStart() {
        return availableStart;
    }

    public void setAvailableStart(Timestamp availableStart) {
        this.availableStart = availableStart;
    }

    // =====================================
    // availableEnd
    // =====================================

    public Timestamp getAvailableEnd() {
        return availableEnd;
    }

    public void setAvailableEnd(Timestamp availableEnd) {
        this.availableEnd = availableEnd;
    }

    // =====================================
    // isActive
    // =====================================

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // =====================================
    // createdAt
    // =====================================

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // =====================================
    // updatedAt
    // =====================================

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}