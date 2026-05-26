package com.university.crs.gui;

import com.university.crs.dao.RegistrationDao;
import com.university.crs.dao.StudentV2Dao;
import com.university.crs.model.StudentV2;
import com.university.crs.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Student dashboard overview page - shows student stats and quick info
 */
public class StudentOverviewPage {

    private final Stage stage;
    private final User user;
    private final StudentV2Dao studentDao = new StudentV2Dao();
    private final RegistrationDao registrationDao = new RegistrationDao();

    public StudentOverviewPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Header
        VBox header = new VBox(8);
        Label greeting = new Label("Welcome back, " + user.getFullName() + "!");
        greeting.setFont(FontLoader.getPoppinsBold(28));
        greeting.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("Here's your academic overview");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        header.getChildren().addAll(greeting, subtitle);

        // Stats cards
        HBox statsRow = new HBox(20);
        
        try {
            StudentV2 student = studentDao.getStudentByStudentId(user.getUsername());
            if (student != null) {
                int pendingCount = registrationDao.getPendingRegistrationsCount(student.getId());
                int approvedCount = registrationDao.getApprovedRegistrationsCount(student.getId());
                int totalCredits = registrationDao.getTotalCreditsForStudent(student.getId());
                
                statsRow.getChildren().addAll(
                    createStatCard("📚", "Enrolled Courses", String.valueOf(approvedCount), ColorScheme.PRIMARY_500),
                    createStatCard("⏳", "Pending Requests", String.valueOf(pendingCount), ColorScheme.WARNING_500),
                    createStatCard("🎯", "Total Credits", String.valueOf(totalCredits), ColorScheme.SUCCESS_500),
                    createStatCard("📊", "Current GPA", String.format("%.2f", student.getGpa()), ColorScheme.INFO_500)
                );
            }
        } catch (SQLException e) {
            System.err.println("Error loading student stats: " + e.getMessage());
        }

        // Quick actions
        VBox quickActions = buildQuickActionsCard();

        // Recent activity
        VBox recentActivity = buildRecentActivityCard();

        page.getChildren().addAll(header, statsRow, quickActions, recentActivity);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox createStatCard(String icon, String label, String value, Color accentColor) {
        VBox card = new VBox(12);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(24));
        card.setPrefWidth(250);
        card.setMinHeight(120);
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(32));
        
        // Value
        Label valueLabel = new Label(value);
        valueLabel.setFont(FontLoader.getPoppinsBold(28));
        valueLabel.setTextFill(accentColor);
        
        // Label
        Label textLabel = new Label(label);
        textLabel.setFont(FontLoader.getOutfit(14));
        textLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        card.getChildren().addAll(iconLabel, valueLabel, textLabel);
        
        return card;
    }

    private VBox buildQuickActionsCard() {
        VBox card = new VBox(20);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(30));
        
        Label title = new Label("Quick Actions");
        title.setFont(FontLoader.getOutfitSemiBold(18));
        title.setTextFill(ColorScheme.DARK_TEXT);
        
        HBox actionsRow = new HBox(15);
        
        // Browse Courses button
        VBox browseAction = createActionButton("📚", "Browse Courses", "Explore available courses");
        
        // View Registrations button
        VBox registrationsAction = createActionButton("📋", "My Registrations", "Check registration status");
        
        // View Schedule button
        VBox scheduleAction = createActionButton("📅", "My Schedule", "View your class schedule");
        
        actionsRow.getChildren().addAll(browseAction, registrationsAction, scheduleAction);
        
        card.getChildren().addAll(title, actionsRow);
        
        return card;
    }

    private VBox createActionButton(String icon, String title, String description) {
        VBox action = new VBox(8);
        action.setStyle(
            "-fx-background-color: " + ColorScheme.GRAY_50_HEX + "; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 20; " +
            "-fx-cursor: hand;"
        );
        action.setPrefWidth(200);
        action.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(32));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(FontLoader.getOutfitSemiBold(14));
        titleLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        Label descLabel = new Label(description);
        descLabel.setFont(FontLoader.getOutfit(12));
        descLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setMaxWidth(180);
        
        action.getChildren().addAll(iconLabel, titleLabel, descLabel);
        
        // Hover effect
        action.setOnMouseEntered(e -> action.setStyle(
            "-fx-background-color: " + ColorScheme.PRIMARY_50_HEX + "; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 20; " +
            "-fx-cursor: hand;"
        ));
        action.setOnMouseExited(e -> action.setStyle(
            "-fx-background-color: " + ColorScheme.GRAY_50_HEX + "; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 20; " +
            "-fx-cursor: hand;"
        ));
        
        return action;
    }

    private VBox buildRecentActivityCard() {
        VBox card = new VBox(20);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(30));
        
        Label title = new Label("Recent Activity");
        title.setFont(FontLoader.getOutfitSemiBold(18));
        title.setTextFill(ColorScheme.DARK_TEXT);
        
        VBox activityList = new VBox(15);
        
        // TODO: Load actual recent activity from database
        activityList.getChildren().addAll(
            createActivityItem("✅", "Registration approved for CS101", "2 hours ago", ColorScheme.SUCCESS_500),
            createActivityItem("⏳", "Waiting for approval - MATH201", "1 day ago", ColorScheme.WARNING_500),
            createActivityItem("📚", "Registered for 3 new courses", "2 days ago", ColorScheme.PRIMARY_500)
        );
        
        card.getChildren().addAll(title, activityList);
        
        return card;
    }

    private HBox createActivityItem(String icon, String text, String time, Color iconColor) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle(
            "-fx-background-color: " + ColorScheme.GRAY_50_HEX + "; " +
            "-fx-background-radius: 8;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(20));
        iconLabel.setTextFill(iconColor);
        
        VBox textBox = new VBox(4);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        
        Label textLabel = new Label(text);
        textLabel.setFont(FontLoader.getOutfitMedium(14));
        textLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        Label timeLabel = new Label(time);
        timeLabel.setFont(FontLoader.getOutfit(12));
        timeLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        textBox.getChildren().addAll(textLabel, timeLabel);
        
        item.getChildren().addAll(iconLabel, textBox);
        
        return item;
    }
}
