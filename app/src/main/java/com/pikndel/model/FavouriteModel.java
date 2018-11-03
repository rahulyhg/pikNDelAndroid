package com.pikndel.model;

/**
 * Created by priya.singh on 2/5/16.
 */
public class FavouriteModel {
    public String getTvCompanyName() {
        return tvCompanyName;
    }

    public void setTvCompanyName(String tvCompanyName) {
        this.tvCompanyName = tvCompanyName;
    }

    public int getIvCompanyRating() {
        return ivCompanyRating;
    }

    public void setIvCompanyRating(int ivCompanyRating) {
        this.ivCompanyRating = ivCompanyRating;
    }

    public String getTvCompanyCity() {
        return tvCompanyCity;
    }

    public void setTvCompanyCity(String tvCompanyCity) {
        this.tvCompanyCity = tvCompanyCity;
    }

    public String getTvCompanyAddress() {
        return tvCompanyAddress;
    }

    public void setTvCompanyAddress(String tvCompanyAddress) {
        this.tvCompanyAddress = tvCompanyAddress;
    }

    public String tvCompanyName, tvCompanyAddress, tvCompanyCity;
    public int ivCompanyRating;


    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
