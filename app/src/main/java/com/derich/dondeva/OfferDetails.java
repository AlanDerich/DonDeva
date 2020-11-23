package com.derich.dondeva;

public class OfferDetails {
    private String pic,offerName,details;
    public OfferDetails() {
    }

    public OfferDetails(String pic, String offerName, String details) {
        this.pic = pic;
        this.offerName = offerName;
        this.details = details;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
