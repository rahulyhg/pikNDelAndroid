package com.pikndel.model;

import java.io.Serializable;

/**
 * Created by ankus on 14/12/15.
 */
public class NavigationDrawerModel implements Serializable {
    String drawerText;
    int drawerImage;

    public int getDrawerImage() {
        return drawerImage;
    }

    public void setDrawerImage(int drawerImage) {
        this.drawerImage = drawerImage;
    }

    public String getDrawerText() {
        return drawerText;
    }

    public void setDrawerText(String drawerText) {
        this.drawerText = drawerText;
    }


}
