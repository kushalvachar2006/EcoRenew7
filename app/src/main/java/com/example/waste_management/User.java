package com.example.waste_management;
public class User {
    public String name;
    public String Email;
    public String Username;
    public String Password;
    public String mobilenum;
    public User(String email) {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(String name, String user_email, String user_password) {
        this.name = name;
        this.Email = user_email;
        this.Password=user_password;
    }
}

