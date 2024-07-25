package com.example.thirdAndroidApp;

import java.util.List;

public class UserResponse {
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public static class User {
        private String firstName;
        private String lastName;
        private String maidenName;
        private Hair hair;
        private Address address;
        private Company company;
        private String image;

        // Getters and Setters

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMaidenName() {
            return maidenName;
        }

        public void setMaidenName(String maidenName) {
            this.maidenName = maidenName;
        }

        public Hair getHair() {
            return hair;
        }

        public void setHair(Hair hair) {
            this.hair = hair;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Company getCompany() {
            return company;
        }

        public void setCompany(Company company) {
            this.company = company;
        }

        public String getImage(){
            return image;
        }

        public static class Hair {
            private String color;

            // Getters and Setters

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }
        }

        public static class Address {
            private String address;

            // Getters and Setters

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }

        public static class Company {
            private String department;
            private Address address;

            // Getters and Setters

            public String getDepartment() {
                return department;
            }

            public void setDepartment(String department) {
                this.department = department;
            }

            public Address getAddress() {
                return address;
            }

            public void setAddress(Address address) {
                this.address = address;
            }
        }
    }
}
