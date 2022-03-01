package com.homegarage.garageowner.model;

public class InfoUserGarageModel {

    private String name;
    private String phone;
    private String email;
    private String password;
    private String governoate;
    private String city;
    private String restOfAddressEN;
    private String restOfAddressAr;
    private String location;

    public String getGovernoate() {
        return governoate;
    }

    public void setGovernoate(String governoate) {
        this.governoate = governoate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public InfoUserGarageModel() {
    }

    public InfoUserGarageModel(String name, String phone, String email, String password, String governoate, String city, String restOfAddressEN, String restOfAddressAr, String location) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.governoate = governoate;
        this.city = city;
        this.restOfAddressEN = restOfAddressEN;
        this.restOfAddressAr = restOfAddressAr;
        this.location = location;
    }

    public String getRestOfAddressEN() {
        return restOfAddressEN;
    }

    public void setRestOfAddressEN(String restOfAddressEN) {
        this.restOfAddressEN = restOfAddressEN;
    }

    public String getRestOfAddressAr() {
        return restOfAddressAr;
    }

    public void setRestOfAddressAr(String restOfAddressAr) {
        this.restOfAddressAr = restOfAddressAr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
