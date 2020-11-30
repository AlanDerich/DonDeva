package com.derich.dondeva;

public class Favorites {
    String servName,username,mainService;

    public Favorites() {
    }

    public Favorites(String servName, String username, String mainService) {
        this.servName = servName;
        this.username = username;
        this.mainService = mainService;
    }

    public String getMainService() {
        return mainService;
    }

    public void setMainService(String mainService) {
        this.mainService = mainService;
    }

    public String getServName() {
        return servName;
    }

    public void setServName(String servName) {
        this.servName = servName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
