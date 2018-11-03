package com.pikndel.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class Terms implements Serializable{
    public String value;
    public String offset;
    public String long_name;
    public String short_name;
    public List<String> types;

    //public List<TermsPastalCode> types;
    public TermsPastalCode address_components;

}
