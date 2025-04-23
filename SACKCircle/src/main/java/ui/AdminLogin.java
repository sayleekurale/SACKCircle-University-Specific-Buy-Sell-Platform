package ui;

import models.Admin;
import services.AdminDAO;

import java.util.Scanner;

public class AdminLogin {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AdminDAO adminDAO = new AdminDAO();

        System.out.println("Enter username:");
        String username = scanner.nextLine();

        System.out.println("Enter password:");
        String password = scanner.nextLine(); // Ideally hash this before checking

        // For now we assume password is already hashed (or plain for demo)
        Admin admin = adminDAO.loginAdmin(username, password);

        if (admin != null) {
            System.out.println("Login successful. Welcome, " + admin.getUsername() + "!");
        } else {
            System.out.println("Invalid credentials.");
        }
    }
}
