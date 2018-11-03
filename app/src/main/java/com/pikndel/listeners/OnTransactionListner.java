package com.pikndel.listeners;

import com.pikndel.model.TransactionDetails;

/**
 * Created by ashimkanshal on 25/8/16.
 */
public interface OnTransactionListner {

    public void onTransactionSuccess(TransactionDetails transactionDetails);

    public void onTransactionfailure(String failureString);
}
