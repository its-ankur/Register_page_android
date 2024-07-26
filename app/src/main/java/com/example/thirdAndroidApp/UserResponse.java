package com.example.thirdAndroidApp;

import java.util.List;

public class UserResponse {
    private List<User> users;

    // Getter for the list of users
    public List<User> getUsers() {
        return users;
    }

    public static class User {
        private String id;
        private String firstName;
        private String maidenName;
        private String lastName;
        private Hair hair;
        private Address address;
        private Company company;
        private String image;

        // Getters for all fields
        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getMaidenName() {
            return maidenName;
        }

        public String getLastName() {
            return lastName;
        }

        public Hair getHair() {
            return hair;
        }

        public Address getAddress() {
            return address;
        }

        public Company getCompany() {
            return company;
        }

        public String getImage() {
            return image;
        }

        public static class Hair {
            private String color;

            public String getColor() {
                return color;
            }
        }

        public static class Address {
            private String address;

            public String getAddress() {
                return address;
            }
        }

        public static class Company {
            private String department;
            private Address address;

            public String getDepartment() {
                return department;
            }

            public Address getAddress() {
                return address;
            }
        }
    }
}
