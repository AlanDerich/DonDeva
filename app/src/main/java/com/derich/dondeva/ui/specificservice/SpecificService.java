package com.derich.dondeva.ui.specificservice;

public class SpecificService {
    String ssName,ssPic,ssRequirements,ssRequirementsPrice,ssServiceHours,ssServiceMinutes,ssServiceFee;

    public SpecificService() {
    }

    public SpecificService(String ssName, String ssPic, String ssRequirements, String ssRequirementsPrice, String ssServiceHours, String ssServiceMinutes, String ssServiceFee) {
        this.ssName = ssName;
        this.ssPic = ssPic;
        this.ssRequirements = ssRequirements;
        this.ssRequirementsPrice = ssRequirementsPrice;
        this.ssServiceHours = ssServiceHours;
        this.ssServiceMinutes = ssServiceMinutes;
        this.ssServiceFee = ssServiceFee;
    }

    public String getSsName() {
        return ssName;
    }

    public void setSsName(String ssName) {
        this.ssName = ssName;
    }

    public String getSsPic() {
        return ssPic;
    }

    public void setSsPic(String ssPic) {
        this.ssPic = ssPic;
    }

    public String getSsRequirements() {
        return ssRequirements;
    }

    public void setSsRequirements(String ssRequirements) {
        this.ssRequirements = ssRequirements;
    }

    public String getSsRequirementsPrice() {
        return ssRequirementsPrice;
    }

    public void setSsRequirementsPrice(String ssRequirementsPrice) {
        this.ssRequirementsPrice = ssRequirementsPrice;
    }

    public String getSsServiceHours() {
        return ssServiceHours;
    }

    public void setSsServiceHours(String ssServiceHours) {
        this.ssServiceHours = ssServiceHours;
    }

    public String getSsServiceMinutes() {
        return ssServiceMinutes;
    }

    public void setSsServiceMinutes(String ssServiceMinutes) {
        this.ssServiceMinutes = ssServiceMinutes;
    }

    public String getSsServiceFee() {
        return ssServiceFee;
    }

    public void setSsServiceFee(String ssServiceFee) {
        this.ssServiceFee = ssServiceFee;
    }
}
