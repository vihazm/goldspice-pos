/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

public class user {
    private int userId;
    private String name;
    private String username;
    private String password;
    private String role;
    private String joinDate;

    // Make sure your constructor has this exact order!
    public user(int userId, String name, String username, String password, String role, String joinDate) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.joinDate = joinDate;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public int getUserId() { return userId; }
}

