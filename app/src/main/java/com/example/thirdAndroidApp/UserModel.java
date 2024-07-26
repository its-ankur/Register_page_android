package com.example.thirdAndroidApp;

public class UserModel {
    private String id;
    private String firstName;
    private String maidenName;
    private String lastName;
    private String hairColor;
    private String addressAddress;
    private String companyDepartment;
    private String companyAddressAddress;
    private String imageUrl;

    // Constructor
    public UserModel(String id, String firstName, String maidenName, String lastName, String hairColor,
                     String addressAddress, String companyDepartment, String companyAddressAddress, String imageUrl) {
        this.id=id;
        this.firstName = firstName;
        this.maidenName = maidenName;
        this.lastName = lastName;
        this.hairColor = hairColor;
        this.addressAddress = addressAddress;
        this.companyDepartment = companyDepartment;
        this.companyAddressAddress = companyAddressAddress;
        this.imageUrl=imageUrl;
    }

    // Getters

    public String getId(){ return id;}

    public String getFirstName() {
        return firstName;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getHairColor() {
        return hairColor;
    }

    public String getAddressAddress() {
        return addressAddress;
    }

    public String getCompanyDepartment() {
        return companyDepartment;
    }

    public String getCompanyAddressAddress() {
        return companyAddressAddress;
    }

    public String getImageUrl(){return imageUrl;}

    // Setters
    public void setId(String id){this.id=id;}

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public void setAddressAddress(String addressAddress) {
        this.addressAddress = addressAddress;
    }

    public void setCompanyDepartment(String companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    public void setCompanyAddressAddress(String companyAddressAddress) {
        this.companyAddressAddress = companyAddressAddress;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl=imageUrl;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "firstName='" + firstName + '\'' +
                ", maidenName='" + maidenName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", hairColor='" + hairColor + '\'' +
                ", addressAddress='" + addressAddress + '\'' +
                ", companyDepartment='" + companyDepartment + '\'' +
                ", companyAddressAddress='" + companyAddressAddress + '\'' +
                '}';
    }
}
