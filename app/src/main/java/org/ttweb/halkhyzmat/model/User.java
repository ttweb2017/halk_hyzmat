package org.ttweb.halkhyzmat.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("LOGIN")
    private String login;

    @SerializedName("USER_ID")
    private String userId;

    @SerializedName("BX_USER_ID")
    private String hashedUserId;

    @SerializedName("BX_UIDH")
    private String bitrixUidh;

    @SerializedName("SESSION_ID")
    private String sessionId;

    @SerializedName("SESSID")
    private String bitrixSessid;

    @SerializedName("USER_NAME")
    private String fullName;

    @SerializedName("STATUS")
    private boolean status;

    @SerializedName("XML_ID")
    private String xmlId;

    @SerializedName("ADDRESS_FULL")
    private String fullAddress;

    @SerializedName("REGION")
    private String region;

    @SerializedName("CITY")
    private String city;

    @SerializedName("SETTLEMENT")
    private String settlement;

    @SerializedName("DISTRICT")
    private String district;

    @SerializedName("STREET")
    private String street;

    @SerializedName("HOUSE")
    private String house;

    @SerializedName("FLAT")
    private String flat;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHashedUserId() {
        return hashedUserId;
    }

    public void setHashedUserId(String hashedUserId) {
        this.hashedUserId = hashedUserId;
    }

    public String getBitrixUidh() {
        return bitrixUidh;
    }

    public void setBitrixUidh(String bitrixUidh) {
        this.bitrixUidh = bitrixUidh;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getBitrixSessid() {
        return bitrixSessid;
    }

    public void setBitrixSessid(String bitrixSessid) {
        this.bitrixSessid = bitrixSessid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getXmlId() {
        return xmlId;
    }

    public void setXmlId(String xmlId) {
        this.xmlId = xmlId;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }
}
