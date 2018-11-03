package com.pikndel.services;

import java.io.Serializable;

/**
 * Created by situn on 15/7/16.
 */
public class PaymentHistoryList implements Serializable {

   // public String walletId;
    public int amount;

    public String sign;
    public String transactionId;
    public String walletId;
    public String userId;
    public String transactionDate;
    public String remark;


}
