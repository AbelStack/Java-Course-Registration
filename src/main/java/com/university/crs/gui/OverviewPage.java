package com.university.crs.gui;

import com.university.crs.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.SQLException;

/**
 * Overview / home page showing summary stat cards and recent registrations.
 * Matches the client dashboard design.
 */
public class OverviewPage {

    private final User user;

    public OverviewPage(User user) {
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(StyleConstants.SPACING_XL);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Page header
        VBox header = new VBox(4);
        Label heading = new Label("Admin Dashboard");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("Overview of your course registration system");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        header.getChildren().addAll(heading, subtitle);

        // Fetch stats
        int totalStudents = 0, totalCourses = 0, totalEnrollments = 0, totalDepartments = 0, pendingApprovals = 0;
        try {
            // Use StudentV2Dao for enhanced students
            totalStudents = new com.university.crs.dao.StudentV2Dao().getAllStudents().size();
            
            // Use CourseV2Dao for enhanced courses
            totalCourses = new com.university.crs.dao.CourseV2Dao().getAllCourses().size();
            
            // Use RegistrationDao for enhanced enrollments
            com.university.crs.dao.RegistrationDao regDao = new com.university.crs.dao.RegistrationDao();
            totalEnrollments = regDao.getAllRegistrations().size();
            pendingApprovals = regDao.getPendingRegistrations().size();
            
            // Use DepartmentDao for departments
            totalDepartments = new com.university.crs.dao.DepartmentDao().getAllDepartments().size();
        } catch (SQLException e) {
            System.err.println("Error fetching dashboard stats: " + e.getMessage());
        }

        // KPI Cards Grid
        GridPane cardsGrid = new GridPane();
        cardsGrid.setHgap(StyleConstants.SPACING_XL);
        cardsGrid.setVgap(StyleConstants.SPACING_XL);
        
        // Make columns equal width
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            col.setHgrow(Priority.ALWAYS);
            cardsGrid.getColumnConstraints().add(col);
        }

        // First row - Main stats
        cardsGrid.add(createKPICard("Total Students", String.valueOf(totalStudents), 
            "registered students", ColorScheme.GRAY_600, false), 0, 0);
        
        cardsGrid.add(createKPICard("Total Departments", String.valueOf(totalDepartments), 
            "academic departments", ColorScheme.GRAY_600, false), 1, 0);
        
        cardsGrid.add(createKPICard("Total Courses", String.valueOf(totalCourses), 
            "available courses", ColorScheme.GRAY_600, false), 2, 0);
        
        cardsGrid.add(createKPICard("Total Enrollments", String.valueOf(totalEnrollments), 
            "course registrations", ColorScheme.GRAY_600, false), 3, 0);
        
        // Second row - Pending approvals (spans 2 columns)
        VBox pendingCard = createKPICard("Pending Approvals", String.valueOf(pendingApprovals), 
            "registrations awaiting approval", ColorScheme.ERROR_600, true);
        GridPane.setColumnSpan(pendingCard, 2);
        cardsGrid.add(pendingCard, 0, 1);

        // Recent Registrations Section
        VBox recentSection = buildRecentRegistrations();

        page.getChildren().addAll(header, cardsGrid, recentSection);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    /**
     * Create a KPI card matching the client design
     */
    private VBox createKPICard(String title, String value, String subtitle, javafx.scene.paint.Color subtitleColor, boolean showBadge) {
        VBox card = new VBox(StyleConstants.SPACING_SM);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(StyleConstants.SPACING_XL));
        card.setStyle(StyleConstants.card());
        card.setPrefHeight(120);
        
        // Header row with title and optional badge
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(headerRow, Priority.ALWAYS);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(FontLoader.getOutfitMedium(14));
        titleLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        headerRow.getChildren().add(titleLabel);
        
        // Badge for pending approvals
        if (showBadge && !value.equals("0")) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label badge = new Label(value);
            badge.setFont(FontLoader.getOutfitBold(12));
            badge.setTextFill(javafx.scene.paint.Color.WHITE);
            badge.setStyle(String.format(
                "-fx-background-color: %s; " +
                "-fx-background-radius: %.0fpx; " +
                "-fx-padding: 2 8; " +
                "-fx-min-width: 24; " +
                "-fx-min-height: 24; " +
                "-fx-alignment: center;",
                ColorScheme.ERROR_500_HEX,
                StyleConstants.RADIUS_FULL
            ));
            
            headerRow.getChildren().addAll(spacer, badge);
        }
        
        // Value (large number)
        Label valueLabel = new Label(value);
        valueLabel.setFont(FontLoader.getPoppinsBold(36));
        valueLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        // Subtitle
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(FontLoader.getOutfit(12));
        subtitleLabel.setTextFill(subtitleColor);
        
        card.getChildren().addAll(headerRow, valueLabel, subtitleLabel);
        
        // Hover effect for pending approvals card
        if (showBadge && !value.equals("0")) {
            card.setOnMouseEntered(e -> card.setStyle(
                StyleConstants.card() + 
                "-fx-border-color: " + ColorScheme.ERROR_300_HEX + "; " +
                "-fx-cursor: hand;"
            ));
            card.setOnMouseExited(e -> card.setStyle(StyleConstants.card()));
        }
        
        return card;
    }

    private VBox buildRecentRegistrations() {
        VBox section = new VBox(StyleConstants.SPACING_LG);
        
        // Section header
        HBox sectionHeader = new HBox();
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(sectionHeader, Priority.ALWAYS);
        
        Label sectionTitle = new Label("System Overview");
        sectionTitle.setFont(FontLoader.getOutfitSemiBold(18));
        sectionTitle.setTextFill(ColorScheme.DARK_TEXT);
        
        sectionHeader.getChildren().add(sectionTitle);

        // Info card
        VBox infoCard = new VBox(StyleConstants.SPACING_LG);
        infoCard.setStyle(StyleConstants.card());
        infoCard.setPadding(new Insets(StyleConstants.SPACING_XL));

        // Welcome message
        Label welcomeLabel = new Label("Welcome to the Course Registration System");
        welcomeLabel.setFont(FontLoader.getOutfitSemiBold(16));
        welcomeLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        Label descLabel = new Label("Manage courses, instructors, students, and enrollments from this dashboard.");
        descLabel.setFont(FontLoader.getOutfit(14));
        descLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        descLabel.setWrapText(true);

        // Quick actions grid
        GridPane actionsGrid = new GridPane();
        actionsGrid.setHgap(StyleConstants.SPACING_LG);
        actionsGrid.setVgap(StyleConstants.SPACING_LG);
        actionsGrid.setPadding(new Insets(StyleConstants.SPACING_LG, 0, 0, 0));
        
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            actionsGrid.getColumnConstraints().add(col);
        }

        // Quick action cards
        actionsGrid.add(createQuickActionCard("👥", "Manage Students", "Add, edit, and manage student accounts"), 0, 0);
        actionsGrid.add(createQuickActionCard("🏢", "Manage Departments", "Add departments and assign heads"), 1, 0);
        actionsGrid.add(createQuickActionCard("📚", "Manage Courses", "Add, edit, or remove courses"), 2, 0);

        infoCard.getChildren().addAll(welcomeLabel, descLabel, actionsGrid);
        section.getChildren().addAll(sectionHeader, infoCard);
        
        return section;
    }

    private VBox createQuickActionCard(String icon, String title, String description) {
        VBox card = new VBox(StyleConstants.SPACING_SM);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(StyleConstants.SPACING_LG));
        card.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx;",
            ColorScheme.GRAY_50_HEX,
            ColorScheme.GRAY_200_HEX,
            StyleConstants.RADIUS_MD,
            StyleConstants.RADIUS_MD
        ));
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfit(32));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(FontLoader.getOutfitSemiBold(14));
        titleLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        Label descLabel = new Label(description);
        descLabel.setFont(FontLoader.getOutfit(12));
        descLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        descLabel.setWrapText(true);
        
        card.getChildren().addAll(iconLabel, titleLabel, descLabel);
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(String.format(
            "-fx-background-color: white; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-cursor: hand; " +
            "-fx-effect: %s;",
            ColorScheme.BRAND_300_HEX,
            StyleConstants.RADIUS_MD,
            StyleConstants.RADIUS_MD,
            StyleConstants.SHADOW_SM
        )));
        card.setOnMouseExited(e -> card.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx;",
            ColorScheme.GRAY_50_HEX,
            ColorScheme.GRAY_200_HEX,
            StyleConstants.RADIUS_MD,
            StyleConstants.RADIUS_MD
        )));
        
        return card;
    }
}
