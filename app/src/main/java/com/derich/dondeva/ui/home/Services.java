package com.derich.dondeva.ui.home;

public class Services {
    String serviceName,servicePic;
    public Services() {
    }

    public Services(String serviceName, String servicePic) {
        this.serviceName = serviceName;
        this.servicePic = servicePic;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePic() {
        return servicePic;
    }

    public void setServicePic(String servicePic) {
        this.servicePic = servicePic;
    }
}
