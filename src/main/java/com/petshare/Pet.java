package com.petshare;

import java.sql.Date;
import java.sql.Timestamp;

public class Pet {

    // ==============================
    // 基本情報
    // ==============================

    private int petId;
    private int accountId;

    private String petName;
    private String petImage;
    private String petType;
    private String breed;
    private String gender;
    private Date birthDate;
    private Double weight;
    // ==============================
    // 性格・特徴
    // ==============================

    private String personality;
    private String features;
    private String likes;
    private String dislikes;

    // ==============================
    // 相性
    // ==============================

    private String otherAnimals;
    private String childrenCompatibility;

    // ==============================
    // 飼育情報
    // ==============================

    private String foodInfo;
    private String walkInfo;
    private String toiletInfo;
    private String aloneTime;
    private String careInfo;

    // ==============================
    // 健康・安全情報
    // ==============================

    private String vaccination;
    private String neutering;
    private String medicalHistory;
    private String allergies;
    private String medication;
    private String precautions;

    // ==============================
    // シェア情報
    // ==============================

    private boolean isShared;

    private Integer sharePrice;
    private Date shareStartDate;
    private Date shareEndDate;
    private String shareConditions;
    // ==============================
    // 管理情報
    // ==============================

    private Timestamp createdAt;
    private Timestamp updatedAt;

    // ==============================
    // コンストラクタ
    // ==============================

    public Pet() {
    }

    // ==============================
    // Getter / Setter
    // ==============================

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetImage() {
        return petImage;
    }

    public void setPetImage(String petImage) {
        this.petImage = petImage;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
    }

    public String getOtherAnimals() {
        return otherAnimals;
    }

    public void setOtherAnimals(String otherAnimals) {
        this.otherAnimals = otherAnimals;
    }

    public String getChildrenCompatibility() {
        return childrenCompatibility;
    }

    public void setChildrenCompatibility(String childrenCompatibility) {
        this.childrenCompatibility = childrenCompatibility;
    }

    public String getFoodInfo() {
        return foodInfo;
    }

    public void setFoodInfo(String foodInfo) {
        this.foodInfo = foodInfo;
    }

    public String getWalkInfo() {
        return walkInfo;
    }

    public void setWalkInfo(String walkInfo) {
        this.walkInfo = walkInfo;
    }

    public String getToiletInfo() {
        return toiletInfo;
    }

    public void setToiletInfo(String toiletInfo) {
        this.toiletInfo = toiletInfo;
    }

    public String getAloneTime() {
        return aloneTime;
    }

    public void setAloneTime(String aloneTime) {
        this.aloneTime = aloneTime;
    }

    public String getCareInfo() {
        return careInfo;
    }

    public void setCareInfo(String careInfo) {
        this.careInfo = careInfo;
    }

    public String getVaccination() {
        return vaccination;
    }

    public void setVaccination(String vaccination) {
        this.vaccination = vaccination;
    }

    public String getNeutering() {
        return neutering;
    }

    public void setNeutering(String neutering) {
        this.neutering = neutering;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setIsShared(boolean isShared) {
        this.isShared = isShared;
    }

    public Integer getSharePrice() {
        return sharePrice;
    }

    public void setSharePrice(Integer sharePrice) {
        this.sharePrice = sharePrice;
    }

    public Date getShareStartDate() {
        return shareStartDate;
    }

    public void setShareStartDate(Date shareStartDate) {
        this.shareStartDate = shareStartDate;
    }

    public Date getShareEndDate() {
        return shareEndDate;
    }

    public void setShareEndDate(Date shareEndDate) {
        this.shareEndDate = shareEndDate;
    }

    public String getShareConditions() {
        return shareConditions;
    }

    public void setShareConditions(String shareConditions) {
        this.shareConditions = shareConditions;
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