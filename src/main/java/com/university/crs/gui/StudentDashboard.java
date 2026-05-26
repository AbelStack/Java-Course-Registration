package com.university.crs.gui;

import com.university.crs.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

/**
 * Student dashboard with sidebar matching the admin dashboard design.
 * Sidebar: 290px expanded, same styling as admin.
 */
public class StudentDashboard {

    private final Stage stage;
    private final User user;
    private final StackPane contentArea = new StackPane();
    
    private VBox sidebar;
    private boolean isExpanded = true;
    private boolean isHovered = false;
    private Button activeButton = null;

    public StudentDashboard(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        // Root layout
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + ColorScheme.GRAY_50_HEX + ";");

        // Main container
        HBox mainContainer = new HBox();
        
        // Sidebar
        sidebar = buildSidebar();
        
        // Content area with padding
        VBox contentWrapper = new VBox();
        contentWrapper.setStyle("-fx-background-color: " + ColorScheme.GRAY_50_HEX + ";");
        contentWrapper.setPadding(new Insets(StyleConstants.SPACING_XL));
        HBox.setHgrow(contentWrapper, Priority.ALWAYS);
        
        contentArea.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        contentWrapper.getChildren().add(contentArea);

        mainContainer.getChildren().addAll(sidebar, contentWrapper);
        root.getChildren().add(mainContainer);

        // Default page - Student Overview
        showPage(new StudentOverviewPage(stage, user).build());

        Scene scene = new Scene(root, 1400, 900);
        
        // Load global stylesheet
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Could not load styles.css: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.setTitle("Course Registration System - Student Portal");
        stage.setMaximized(true);
        stage.show();
    }

    // ── Sidebar ──────────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(StyleConstants.SIDEBAR_WIDTH_EXPANDED);
        sidebar.setMinWidth(StyleConstants.SIDEBAR_WIDTH_EXPANDED);
        sidebar.setMaxWidth(StyleConstants.SIDEBAR_WIDTH_EXPANDED);
        sidebar.setStyle(String.format(
            "-fx-background-color: white; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 0 1 0 0;",
            ColorScheme.GRAY_200_HEX
        ));
        sidebar.setPadding(new Insets(StyleConstants.SPACING_LG, StyleConstants.SPACING_LG, StyleConstants.SPACING_LG, StyleConstants.SPACING_LG));

        // Hover behavior
        sidebar.setOnMouseEntered(e -> {
            if (!isExpanded) {
                isHovered = true;
                expandSidebar();
            }
        });
        
        sidebar.setOnMouseExited(e -> {
            if (!isExpanded && isHovered) {
                isHovered = false;
                collapseSidebar();
            }
        });

        // Logo section
        HBox logoBox = new HBox();
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(StyleConstants.SPACING_SM, 0, StyleConstants.SPACING_XL, 0));
        
        Label logo = new Label("🎓");
        logo.setFont(FontLoader.getOutfitBold(32));
        logoBox.getChildren().add(logo);

        // Role badge
        HBox roleBox = new HBox();
        roleBox.setAlignment(Pos.CENTER_LEFT);
        roleBox.setPadding(new Insets(0, 0, StyleConstants.SPACING_MD, 0));
        
        Label roleLabel = new Label("Student");
        roleLabel.setFont(FontLoader.getOutfitSemiBold(11));
        roleLabel.setTextFill(ColorScheme.PRIMARY_700);
        roleLabel.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-padding: 4 12; " +
            "-fx-background-radius: 12;",
            ColorScheme.PRIMARY_50_HEX
        ));
        roleBox.getChildren().add(roleLabel);

        // Menu section header
        Label menuHeader = new Label("MENU");
        menuHeader.setFont(FontLoader.getOutfit(12));
        menuHeader.setTextFill(Color.BLACK);
        menuHeader.setPadding(new Insets(0, 0, StyleConstants.SPACING_MD, 0));

        // Navigation items
        VBox navBox = new VBox(StyleConstants.SPACING_SM);
        
        Button dashboardBtn = createNavButton("M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6", 
            "Dashboard", true, () -> showPage(new StudentOverviewPage(stage, user).build()));
        
        Button coursesBtn = createNavButton("M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253", 
            "Available Courses", false, () -> showPage(new StudentCoursesPage(stage, user).build()));
        
        Button myRegistrationsBtn = createNavButton("M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z", 
            "My Registrations", false, () -> showPage(new StudentRegistrationsPage(stage, user).build()));
        
        Button scheduleBtn = createNavButton("M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z", 
            "My Schedule", false, () -> showPage(new StudentSchedulePage(stage, user).build()));
        
        Button profileBtn = createNavButton("M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z", 
            "Profile", false, () -> showPage(new StudentProfilePage(stage, user).build()));

        navBox.getChildren().addAll(
            dashboardBtn,
            coursesBtn,
            myRegistrationsBtn,
            scheduleBtn,
            profileBtn
        );

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout button at bottom
        Button logoutBtn = createNavButton("M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1", 
            "Logout", false, () -> {
                // Confirm logout
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Logout");
                confirm.setHeaderText("Are you sure you want to logout?");
                confirm.setContentText("You will be returned to the login screen.");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        new LoginScreen(stage).show();
                    }
                });
            });
        // Style logout button with red color
        HBox logoutContent = (HBox) logoutBtn.getGraphic();
        Label logoutLabel = (Label) logoutContent.getChildren().get(1);
        logoutLabel.setTextFill(ColorScheme.ERROR_600);

        sidebar.getChildren().addAll(logoBox, roleBox, menuHeader, navBox, spacer, logoutBtn);
        
        // Set first button as active
        activeButton = dashboardBtn;
        
        return sidebar;
    }

    private Button createNavButton(String svgPath, String text, boolean isActive, Runnable action) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(44);
        
        // Icon + Text container
        HBox content = new HBox(StyleConstants.SPACING_MD);
        content.setAlignment(Pos.CENTER_LEFT);
        
        // SVG Icon
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setScaleX(0.8);
        icon.setScaleY(0.8);
        
        StackPane iconContainer = new StackPane(icon);
        iconContainer.setMinWidth(20);
        iconContainer.setMaxWidth(20);
        
        Label textLabel = new Label(text);
        textLabel.setFont(FontLoader.getOutfitMedium(14));
        
        content.getChildren().addAll(iconContainer, textLabel);
        btn.setGraphic(content);
        
        // Apply initial style
        if (isActive) {
            btn.setStyle(StyleConstants.menuItemActive());
            icon.setFill(Color.BLACK);
            textLabel.setTextFill(Color.BLACK);
        } else {
            btn.setStyle(StyleConstants.menuItem());
            icon.setFill(Color.BLACK);
            textLabel.setTextFill(Color.BLACK);
        }
        
        // Hover effects
        btn.setOnMouseEntered(e -> {
            if (btn != activeButton) {
                btn.setStyle(StyleConstants.menuItemHover());
                icon.setFill(Color.BLACK);
                textLabel.setTextFill(Color.BLACK);
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (btn != activeButton) {
                btn.setStyle(StyleConstants.menuItem());
                icon.setFill(Color.BLACK);
                textLabel.setTextFill(Color.BLACK);
            }
        });
        
        // Click action
        btn.setOnAction(e -> {
            // Deactivate previous button
            if (activeButton != null && activeButton != btn) {
                activeButton.setStyle(StyleConstants.menuItem());
                HBox prevContent = (HBox) activeButton.getGraphic();
                StackPane prevIconContainer = (StackPane) prevContent.getChildren().get(0);
                SVGPath prevIcon = (SVGPath) prevIconContainer.getChildren().get(0);
                Label prevLabel = (Label) prevContent.getChildren().get(1);
                prevIcon.setFill(Color.BLACK);
                prevLabel.setTextFill(Color.BLACK);
            }
            
            // Activate current button
            activeButton = btn;
            btn.setStyle(StyleConstants.menuItemActive());
            icon.setFill(Color.BLACK);
            textLabel.setTextFill(Color.BLACK);
            
            action.run();
        });
        
        return btn;
    }

    private void expandSidebar() {
        sidebar.setPrefWidth(StyleConstants.SIDEBAR_WIDTH_EXPANDED);
        sidebar.setMinWidth(StyleConstants.SIDEBAR_WIDTH_EXPANDED);
        sidebar.setMaxWidth(StyleConstants.SIDEBAR_WIDTH_EXPANDED);
    }

    private void collapseSidebar() {
        sidebar.setPrefWidth(StyleConstants.SIDEBAR_WIDTH_COLLAPSED);
        sidebar.setMinWidth(StyleConstants.SIDEBAR_WIDTH_COLLAPSED);
        sidebar.setMaxWidth(StyleConstants.SIDEBAR_WIDTH_COLLAPSED);
    }

    private void showPage(javafx.scene.Node page) {
        contentArea.getChildren().setAll(page);
    }
}
