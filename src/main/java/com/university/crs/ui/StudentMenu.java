package com.university.crs.ui;

import com.university.crs.dao.StudentDao;
import com.university.crs.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu for student management.
 */
public class StudentMenu {

    private final StudentDao studentDao = new StudentDao();
    private final Scanner scanner;

    public StudentMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║      STUDENT MANAGEMENT      ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. View All Students        ║");
            System.out.println("║  2. Add Student              ║");
            System.out.println("║  3. Update Student           ║");
            System.out.println("║  4. Delete Student           ║");
            System.out.println("║  0. Back to Dashboard        ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("  Select: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewAll();
                case "2" -> addStudent();
                case "3" -> updateStudent();
                case "4" -> deleteStudent();
                case "0" -> back = true;
                default  -> System.out.println("  [!] Invalid option.");
            }
        }
    }

    private void viewAll() {
        try {
            List<Student> students = studentDao.getAllStudents();
            System.out.println("\n--- All Students (" + students.size() + ") ---");
            if (students.isEmpty()) {
                System.out.println("  No students found.");
            } else {
                System.out.printf("  %-5s %-25s %s%n", "ID", "Name", "Email");
                System.out.println("  " + "-".repeat(55));
                students.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.err.println("  [!] Error: " + e.getMessage());
        }
    }

    private void addStudent() {
        System.out.println("\n--- Add New Student ---");
        System.out.print("  Name       : ");
        String name = scanner.nextLine().trim();
        System.out.print("  Email      : ");
        String email = scanner.nextLine().trim();
        System.out.print("  Department : ");
        String department = scanner.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() || department.isEmpty()) {
            System.out.println("  [!] Name, email, and department cannot be empty.");
            return;
        }
        try {
            studentDao.addStudent(name, email, department);
            System.out.println("  [✓] Student added successfully.");
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("  [!] A student with that email already exists.");
            } else {
                System.err.println("  [!] Error: " + e.getMessage());
            }
        }
    }

    private void updateStudent() {
        System.out.println("\n--- Update Student ---");
        System.out.print("  Enter Student ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Student existing = studentDao.getStudentById(id);
            if (existing == null) {
                System.out.println("  [!] Student not found.");
                return;
            }
            System.out.println("  Current: " + existing);
            System.out.print("  New Name  (leave blank to keep): ");
            String name = scanner.nextLine().trim();
            System.out.print("  New Email (leave blank to keep): ");
            String email = scanner.nextLine().trim();

            name  = name.isEmpty()  ? existing.getName()  : name;
            email = email.isEmpty() ? existing.getEmail() : email;

            if (studentDao.updateStudent(id, name, email)) {
                System.out.println("  [✓] Student updated successfully.");
            } else {
                System.out.println("  [!] Update failed.");
            }
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid ID.");
        } catch (SQLException e) {
            System.err.println("  [!] Error: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        System.out.println("\n--- Delete Student ---");
        System.out.print("  Enter Student ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Student existing = studentDao.getStudentById(id);
            if (existing == null) {
                System.out.println("  [!] Student not found.");
                return;
            }
            System.out.print("  Confirm delete '" + existing.getName() + "'? (yes/no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                if (studentDao.deleteStudent(id)) {
                    System.out.println("  [✓] Student deleted.");
                } else {
                    System.out.println("  [!] Delete failed.");
                }
            } else {
                System.out.println("  Cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid ID.");
        } catch (SQLException e) {
            System.err.println("  [!] Error: " + e.getMessage());
        }
    }
}
