package com.pikndel.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaytmResponse {

    @SerializedName("TXNID")
    @Expose
    private String tXNID;
    @SerializedName("BANKTXNID")
    @Expose
    private String bANKTXNID;
    @SerializedName("ORDERID")
    @Expose
    private String oRDERID;
    @SerializedName("TXNAMOUNT")
    @Expose
    private String tXNAMOUNT;
    @SerializedName("STATUS")
    @Expose
    private String sTATUS;
    @SerializedName("TXNTYPE")
    @Expose
    private String tXNTYPE;
    @SerializedName("GATEWAYNAME")
    @Expose
    private String gATEWAYNAME;
    @SerializedName("RESPCODE")
    @Expose
    private String rESPCODE;
    @SerializedName("RESPMSG")
    @Expose
    private String rESPMSG;
    @SerializedName("BANKNAME")
    @Expose
    private String bANKNAME;
    @SerializedName("MID")
    @Expose
    private String mID;
    @SerializedName("PAYMENTMODE")
    @Expose
    private String pAYMENTMODE;
    @SerializedName("REFUNDAMT")
    @Expose
    private String rEFUNDAMT;
    @SerializedName("TXNDATE")
    @Expose
    private String tXNDATE;

    public String getTXNID() {
        return tXNID;
    }

    public void setTXNID(String tXNID) {
        this.tXNID = tXNID;
    }

    public String getBANKTXNID() {
        return bANKTXNID;
    }

    public void setBANKTXNID(String bANKTXNID) {
        this.bANKTXNID = bANKTXNID;
    }

    public String getORDERID() {
        return oRDERID;
    }

    public void setORDERID(String oRDERID) {
        this.oRDERID = oRDERID;
    }

    public String getTXNAMOUNT() {
        return tXNAMOUNT;
    }

    public void setTXNAMOUNT(String tXNAMOUNT) {
        this.tXNAMOUNT = tXNAMOUNT;
    }

    public String getSTATUS() {
        return sTATUS;
    }

    public void setSTATUS(String sTATUS) {
        this.sTATUS = sTATUS;
    }

    public String getTXNTYPE() {
        return tXNTYPE;
    }

    public void setTXNTYPE(String tXNTYPE) {
        this.tXNTYPE = tXNTYPE;
    }

    public String getGATEWAYNAME() {
        return gATEWAYNAME;
    }

    public void setGATEWAYNAME(String gATEWAYNAME) {
        this.gATEWAYNAME = gATEWAYNAME;
    }

    public String getRESPCODE() {
        return rESPCODE;
    }

    public void setRESPCODE(String rESPCODE) {
        this.rESPCODE = rESPCODE;
    }

    public String getRESPMSG() {
        return rESPMSG;
    }

    public void setRESPMSG(String rESPMSG) {
        this.rESPMSG = rESPMSG;
    }

    public String getBANKNAME() {
        return bANKNAME;
    }

    public void setBANKNAME(String bANKNAME) {
        this.bANKNAME = bANKNAME;
    }

    public String getMID() {
        return mID;
    }

    public void setMID(String mID) {
        this.mID = mID;
    }

    public String getPAYMENTMODE() {
        return pAYMENTMODE;
    }

    public void setPAYMENTMODE(String pAYMENTMODE) {
        this.pAYMENTMODE = pAYMENTMODE;
    }

    public String getREFUNDAMT() {
        return rEFUNDAMT;
    }

    public void setREFUNDAMT(String rEFUNDAMT) {
        this.rEFUNDAMT = rEFUNDAMT;
    }

    public String getTXNDATE() {
        return tXNDATE;
    }

    public void setTXNDATE(String tXNDATE) {
        this.tXNDATE = tXNDATE;
    }

}