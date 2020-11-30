package com.derich.dondeva.ui.specificservice;

public class SpecificService {
    String ssName,ssPic,ssMainName;

    public SpecificService() {
    }

    public SpecificService(String ssName, String ssPic, String ssMainName) {
        this.ssName = ssName;
        this.ssPic = ssPic;
        this.ssMainName = ssMainName;
    }

    public String getSsName() {
        return ssName;
    }

    public String getSsMainName() {
        return ssMainName;
    }

    public void setSsMainName(String ssMainName) {
        this.ssMainName = ssMainName;
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

}
