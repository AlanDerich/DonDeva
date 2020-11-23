package com.derich.dondeva;

public class RequestDetails {
    String date,startTime,endTime,requirementsAmount,totalAmount,serviceName,username,requestDate,image,status;

    public RequestDetails() {
    }

    public RequestDetails(String date, String startTime, String endTime, String requirementsAmount, String totalAmount, String serviceName, String username, String requestDate, String image, String status) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.requirementsAmount = requirementsAmount;
        this.totalAmount = totalAmount;
        this.serviceName = serviceName;
        this.username = username;
        this.requestDate = requestDate;
        this.image = image;
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRequirementsAmount() {
        return requirementsAmount;
    }

    public void setRequirementsAmount(String requirementsAmount) {
        this.requirementsAmount = requirementsAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
