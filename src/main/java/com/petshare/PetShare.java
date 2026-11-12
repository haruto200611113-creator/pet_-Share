package com.petshare;

import java.sql.Timestamp;

public class PetShare {

    private int shareId;
    private int petId;
    private Timestamp availableStart;
    private Timestamp availableEnd;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PetShare() {
    }

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public Timestamp getAvailableStart() {
        return availableStart;
    }

    public void setAvailableStart(Timestamp availableStart) {
        this.availableStart = availableStart;
    }

    public Timestamp getAvailableEnd() {
        return availableEnd;
    }

    public void setAvailableEnd(Timestamp availableEnd) {
        this.availableEnd = availableEnd;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}