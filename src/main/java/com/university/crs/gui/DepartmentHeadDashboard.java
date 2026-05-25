package com.university.crs.gui;

import com.university.crs.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

/**
 * Department Head dashboard with role-based access control.
 * Department Heads can:
 * - View dashboard overview
 * - Manage courses in their department
 * - Manage instructors in their department
 * - View students and enrollments
 * - View reports
 * - Manage their profile
 */
public class DepartmentHeadDashboard {

    private final Stage stage;
    private final User user;
    private final StackPane contentArea = new StackPane();
    
    private VBox sidebar;
    private boolean isExpanded = true;
    private boolean isHovered = false;
    private Button activeButton = null;

    public DepartmentHeadDashboard(Stage stage, User user) {
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

        // Default page
        showPage(new OverviewPage(user).build());

        Scene scene = new Scene(root, 1400, 900);
        
        // Load global stylesheet
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Could not load styles.css: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.setTitle("Course Registration System - Department Head Dashboard");
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
        
        Label roleLabel = new Label("Department Head");
        roleLabel.setFont(FontLoader.getOutfitSemiBold(11));
        roleLabel.setTextFill(ColorScheme.BRAND_600);
        roleLabel.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-padding: 4 12; " +
            "-fx-background-radius: 12;",
            ColorScheme.BRAND_50_HEX
        ));
        roleBox.getChildren().add(roleLabel);

        // Menu section header
        Label menuHeader = new Label("MENU");
        menuHeader.setFont(FontLoader.getOutfit(12));
        menuHeader.setTextFill(ColorScheme.GRAY_400);
        menuHeader.setPadding(new Insets(0, 0, StyleConstants.SPACING_MD, 0));

        // Navigation items - Department Head specific
        VBox navBox = new VBox(StyleConstants.SPACING_SM);
        
        Button dashboardBtn = createNavButton("M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6", 
            "Dashboard", true, () -> showPage(new OverviewPage(user).build()));
        
        Button coursesBtn = createNavButton("M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253", 
            "Manage Courses", false, () -> showPage(new CoursesPage(stage, user).build()));
        
        Button instructorsBtn = createNavButton("M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z", 
            "Manage Instructors", false, () -> showPage(new InstructorsPage(stage, user).build()));
        
        Button studentsBtn = createNavButton("M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z", 
            "View Students", false, () -> showPage(new StudentsPage(stage, user).build()));
        
        Button registrationsBtn = createNavButton("M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z", 
            "View Registrations", false, () -> showPage(new EnrollmentsPage(stage, user).build()));
        
        Button reportsBtn = createNavButton("M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z", 
            "Reports", false, () -> showPage(new ReportsPage().build()));
        
        Button profileBtn = createNavButton("M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z", 
            "Profile", false, () -> showPage(new ProfilePage(user).build()));

        navBox.getChildren().addAll(
            dashboardBtn,
            coursesBtn,
            instructorsBtn,
            studentsBtn,
            registrationsBtn,
            reportsBtn,
            profileBtn
        );

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout button at bottom
        Button logoutBtn = createNavButton("M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1", 
            "Logout", false, () -> new LoginScreen(stage).show());
        logoutBtn.setStyle(StyleConstants.menuItem() + "-fx-text-fill: " + ColorScheme.ERROR_500_HEX + ";");

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
            icon.setFill(ColorScheme.GRAY_900);
            textLabel.setTextFill(ColorScheme.GRAY_900);
        } else {
            btn.setStyle(StyleConstants.menuItem());
            icon.setFill(ColorScheme.GRAY_700);
            textLabel.setTextFill(ColorScheme.GRAY_700);
        }
        
        // Hover effects
        btn.setOnMouseEntered(e -> {
            if (btn != activeButton) {
                btn.setStyle(StyleConstants.menuItemHover());
                icon.setFill(ColorScheme.GRAY_700);
                textLabel.setTextFill(ColorScheme.GRAY_700);
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (btn != activeButton) {
                btn.setStyle(StyleConstants.menuItem());
                icon.setFill(ColorScheme.GRAY_700);
                textLabel.setTextFill(ColorScheme.GRAY_700);
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
                prevIcon.setFill(ColorScheme.GRAY_700);
                prevLabel.setTextFill(ColorScheme.GRAY_700);
            }
            
            // Activate current button
            activeButton = btn;
            btn.setStyle(StyleConstants.menuItemActive());
            icon.setFill(ColorScheme.GRAY_900);
            textLabel.setTextFill(ColorScheme.GRAY_900);
            
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
