package com.pikndel.model;

import java.io.Serializable;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class UserPlanList implements Serializable{
    public String planDesc;
    public String planName;
    public String planId;

    public int isAppPlan;
    public int isActive;
    public float amount;

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    private boolean isExpand;

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    private boolean isActivated;
}