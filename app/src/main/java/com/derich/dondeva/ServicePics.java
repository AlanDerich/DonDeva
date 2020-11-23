package com.derich.dondeva;

public class ServicePics {
    private String pic,serviceName,serviceCategory;

    public ServicePics() {
    }

    public ServicePics(String pic, String serviceName, String serviceCategory) {
        this.pic = pic;
        this.serviceName = serviceName;
        this.serviceCategory = serviceCategory;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }
}
