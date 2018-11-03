package com.pikndel.model;

import java.io.Serializable;

/**
 * Created by faisal on 8/10/16.
 */
public class TransactionDetails implements Serializable{
    public String ORDER_ID;
    public String MID;
    public String BANKNAME;
    public String BANKTXNID;
    public String CURRENCY;
    public String GATEWAYNAME;
    public String TXN_AMOUNT;
    public String IS_CHECKSUM_VALID;
    public String PAYMENTMODE;
    public String RESPCODE;
    public String RESPMSG;
    public String STATUS;
    public String TXNDATE;
    public String TXNID;
}
