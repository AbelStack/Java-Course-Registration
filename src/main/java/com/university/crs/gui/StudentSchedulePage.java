package com.university.crs.gui;

import com.university.crs.dao.RegistrationDao;
import com.university.crs.dao.StudentV2Dao;
import com.university.crs.model.Registration;
import com.university.crs.model.StudentV2;
import com.university.crs.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Student Schedule Page - View approved courses (class schedule)
 */
public class StudentSchedulePage {

    private final Stage stage;
    private final User user;
    private final RegistrationDao registrationDao = new RegistrationDao();
    private final StudentV2Dao studentDao = new StudentV2Dao();
    
    private StudentV2 currentStudent;

    public StudentSchedulePage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Load current student
        try {
            currentStudent = studentDao.getStudentByStudentId(user.getUsername());
        } catch (SQLException e) {
            Label errorLabel = new Label("Failed to load student information");
            errorLabel.setFont(FontLoader.getOutfit(14));
            errorLabel.setTextFill(ColorScheme.ERROR_600);
            page.getChildren().add(errorLabel);
            return page;
        }

        // Header
        VBox header = new VBox(8);
        Label heading = new Label("My Schedule");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("Your approved courses for this semester");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        header.getChildren().addAll(heading, subtitle);

        // Schedule content
        VBox scheduleContent = buildScheduleContent();

        page.getChildren().addAll(header, scheduleContent);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildScheduleContent() {
        VBox content = new VBox(20);
        
        try {
            List<Registration> registrations = registrationDao.getRegistrationsByStudent(currentStudent.getId());
            List<Registration> approvedCourses = registrations.stream()
                .filter(r -> "APPROVED".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());
            
            if (approvedCourses.isEmpty()) {
                VBox emptyState = new VBox(15);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(60));
                emptyState.setStyle(StyleConstants.card());
                
                Label icon = new Label("📅");
                icon.setFont(FontLoader.getOutfitBold(48));
                
                Label message = new Label("No courses in your schedule yet");
                message.setFont(FontLoader.getOutfitSemiBold(16));
                message.setTextFill(ColorScheme.DARK_TEXT);
                
                Label hint = new Label("Register for courses and wait for approval to see them here");
                hint.setFont(FontLoader.getOutfit(13));
                hint.setTextFill(ColorScheme.MEDIUM_TEXT);
                
                emptyState.getChildren().addAll(icon, message, hint);
                content.getChildren().add(emptyState);
            } else {
                // Summary card
                HBox summaryCard = new HBox(40);
                summaryCard.setStyle(StyleConstants.card());
                summaryCard.setPadding(new Insets(24));
                summaryCard.setAlignment(Pos.CENTER);
                
                int totalCredits = approvedCourses.stream().mapToInt(Registration::getCredits).sum();
                
                summaryCard.getChildren().addAll(
                    createSummaryItem("📚", "Total Courses", String.valueOf(approvedCourses.size())),
                    createSummaryItem("🎯", "Total Credits", String.valueOf(totalCredits)),
                    createSummaryItem("📊", "Current GPA", String.format("%.2f", currentStudent.getGpa()))
                );
                
                content.getChildren().add(summaryCard);
                
                // Course list
                VBox coursesList = new VBox(15);
                for (Registration registration : approvedCourses) {
                    coursesList.getChildren().add(createScheduleCourseCard(registration));
                }
                content.getChildren().add(coursesList);
            }
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Failed to load schedule: " + e.getMessage());
            errorLabel.setFont(FontLoader.getOutfit(14));
            errorLabel.setTextFill(ColorScheme.ERROR_600);
            content.getChildren().add(errorLabel);
        }
        
        return content;
    }

    private VBox createSummaryItem(String icon, String label, String value) {
        VBox item = new VBox(8);
        item.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(28));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(FontLoader.getPoppinsBold(24));
        valueLabel.setTextFill(ColorScheme.PRIMARY_600);
        
        Label textLabel = new Label(label);
        textLabel.setFont(FontLoader.getOutfit(13));
        textLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        item.getChildren().addAll(iconLabel, valueLabel, textLabel);
        
        return item;
    }

    private HBox createScheduleCourseCard(Registration registration) {
        HBox card = new HBox(20);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Course code badge
        VBox codeBox = new VBox(4);
        codeBox.setAlignment(Pos.CENTER);
        codeBox.setPrefWidth(100);
        codeBox.setStyle(
            "-fx-background-color: " + ColorScheme.PRIMARY_50_HEX + "; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 12;"
        );
        
        Label codeLabel = new Label(registration.getCourseCode());
        codeLabel.setFont(FontLoader.getOutfitBold(16));
        codeLabel.setTextFill(ColorScheme.PRIMARY_700);
        
        codeBox.getChildren().add(codeLabel);
        
        // Course details
        VBox detailsBox = new VBox(8);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);
        
        Label titleLabel = new Label(registration.getCourseTitle());
        titleLabel.setFont(FontLoader.getPoppinsBold(16));
        titleLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        HBox infoRow = new HBox(20);
        infoRow.setAlignment(Pos.CENTER_LEFT);
        
        Label creditsLabel = new Label("📚 " + registration.getCredits() + " Credits");
        creditsLabel.setFont(FontLoader.getOutfit(13));
        creditsLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        Label semesterLabel = new Label("📅 " + registration.getSemesterName());
        semesterLabel.setFont(FontLoader.getOutfit(13));
        semesterLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        infoRow.getChildren().addAll(creditsLabel, semesterLabel);
        
        detailsBox.getChildren().addAll(titleLabel, infoRow);
        
        // Status badge
        VBox statusBox = new VBox(4);
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label statusBadge = new Label("✅ Enrolled");
        statusBadge.setFont(FontLoader.getOutfitSemiBold(12));
        statusBadge.setTextFill(ColorScheme.SUCCESS_700);
        statusBadge.setStyle(
            "-fx-background-color: " + ColorScheme.SUCCESS_50_HEX + "; " +
            "-fx-padding: 6 12; " +
            "-fx-background-radius: 12;"
        );
        
        statusBox.getChildren().add(statusBadge);
        
        card.getChildren().addAll(codeBox, detailsBox, statusBox);
        
        return card;
    }
}
