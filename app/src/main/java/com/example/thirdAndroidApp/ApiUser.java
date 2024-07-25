package com.example.thirdAndroidApp;

public class ApiUser {
    private String firstName;
    private String maidenName;
    private String lastName;
    private Hair hair;
    private Address address;
    private Company company;

    // Getters and Setters

    public static class Hair {
        private String color;

        // Getter and Setter
    }

    public static class Address {
        private String address;

        // Getter and Setter
    }

    public static class Company {
        private String department;
        private Address address;

        // Getters and Setters
    }
}
