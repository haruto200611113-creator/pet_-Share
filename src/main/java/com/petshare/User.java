package com.petshare;

public class User {

// ==============================
// ユーザー情報
// ==============================

// アカウント名
private String accountName;

// メールアドレス
private String email;

// パスワード
private String password;

// 氏名
private String name;

// 生年月日
private String birthDate;

// 性別
private String gender;

// 電話番号
private String phone;

// 郵便番号
private String postalCode;

// 都道府県
private String prefecture;

// 市区町村
private String city;

// 丁目・番地
private String address;

// 建物名
private String building;


// ==============================
// コンストラクタ
// ==============================

public User() {
}


// ==============================
// アカウント名
// ==============================

public String getAccountName() {
    return accountName;
}

public void setAccountName(String accountName) {
    this.accountName = accountName;
}


// ==============================
// メールアドレス
// ==============================

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}


// ==============================
// パスワード
// ==============================

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}


// ==============================
// 氏名
// ==============================

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}


// ==============================
// 生年月日
// ==============================

public String getBirthDate() {
    return birthDate;
}

public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
}


// ==============================
// 性別
// ==============================

public String getGender() {
    return gender;
}

public void setGender(String gender) {
    this.gender = gender;
}


// ==============================
// 電話番号
// ==============================

public String getPhone() {
    return phone;
}

public void setPhone(String phone) {
    this.phone = phone;
}


// ==============================
// 郵便番号
// ==============================

public String getPostalCode() {
    return postalCode;
}

public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
}


// ==============================
// 都道府県
// ==============================

public String getPrefecture() {
    return prefecture;
}

public void setPrefecture(String prefecture) {
    this.prefecture = prefecture;
}


// ==============================
// 市区町村
// ==============================

public String getCity() {
    return city;
}

public void setCity(String city) {
    this.city = city;
}


// ==============================
// 丁目・番地
// ==============================

public String getAddress() {
    return address;
}

public void setAddress(String address) {
    this.address = address;
}


// ==============================
// 建物名
// ==============================

public String getBuilding() {
    return building;
}

public void setBuilding(String building) {
    this.building = building;
}
}
