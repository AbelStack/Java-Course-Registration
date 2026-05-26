package com.university.crs.gui;

import com.university.crs.dao.UserDao;
import com.university.crs.model.User;
import com.university.crs.util.ValidationUtil;
import com.university.crs.util.ValidationUtil.ValidationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Login screen with split-panel design matching the client project.
 * Left: Login form | Right: Branding
 */
public class LoginScreen {

    private final Stage stage;
    private final UserDao userDao = new UserDao();

    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Root container - centered login form
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: white;");
        root.setAlignment(Pos.CENTER);

        // Centered login form
        VBox loginPanel = buildLoginForm();
        loginPanel.setMaxWidth(500);
        
        root.getChildren().add(loginPanel);

        Scene scene = new Scene(root, 1400, 800);
        
        // Load global stylesheet
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Could not load styles.css: " + e.getMessage());
        }
        
        stage.setTitle("Course Registration System - Sign In");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private VBox buildLoginForm() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: white;");
        panel.setPadding(new Insets(60, 80, 60, 80));

        // Form container
        VBox formContainer = new VBox(StyleConstants.SPACING_XL);
        formContainer.setMaxWidth(400);
        formContainer.setAlignment(Pos.TOP_LEFT);

        // Header
        VBox header = new VBox(8);
        Label title = new Label("Sign In");
        title.setFont(FontLoader.getOutfitBold(36));
        title.setTextFill(ColorScheme.GRAY_900);
        
        Label subtitle = new Label("Enter your username, password, and role to sign in!");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.GRAY_500);
        
        header.getChildren().addAll(title, subtitle);

        // Error message container
        VBox errorContainer = new VBox();
        errorContainer.setVisible(false);
        errorContainer.setManaged(false);
        errorContainer.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 12 16;",
            ColorScheme.ERROR_50_HEX,
            ColorScheme.ERROR_200_HEX,
            StyleConstants.RADIUS_MD,
            StyleConstants.RADIUS_MD
        ));
        
        HBox errorContent = new HBox(8);
        errorContent.setAlignment(Pos.CENTER_LEFT);
        
        Label errorIcon = new Label("✕");
        errorIcon.setFont(FontLoader.getOutfitBold(16));
        errorIcon.setTextFill(ColorScheme.ERROR_500);
        
        Label errorLabel = new Label();
        errorLabel.setFont(FontLoader.getOutfit(14));
        errorLabel.setTextFill(ColorScheme.ERROR_700);
        errorLabel.setWrapText(true);
        
        errorContent.getChildren().addAll(errorIcon, errorLabel);
        errorContainer.getChildren().add(errorContent);

        // Username field
        VBox usernameGroup = new VBox(6);
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle(StyleConstants.label());
        
        Label requiredStar = new Label("*");
        requiredStar.setTextFill(ColorScheme.ERROR_500);
        
        HBox usernameLabelBox = new HBox(2);
        usernameLabelBox.getChildren().addAll(usernameLabel, requiredStar);
        
        TextField usernameField = new TextField();
        usernameField.setPrefHeight(StyleConstants.INPUT_HEIGHT);
        usernameField.setStyle(StyleConstants.input());
        
        // Focus listener
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle(StyleConstants.inputFocus());
            } else {
                usernameField.setStyle(StyleConstants.input());
            }
        });
        
        usernameGroup.getChildren().addAll(usernameLabelBox, usernameField);

        // Password field
        VBox passwordGroup = new VBox(6);
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(StyleConstants.label());
        
        Label passwordStar = new Label("*");
        passwordStar.setTextFill(ColorScheme.ERROR_500);
        
        HBox passwordLabelBox = new HBox(2);
        passwordLabelBox.getChildren().addAll(passwordLabel, passwordStar);
        
        StackPane passwordContainer = new StackPane();
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(StyleConstants.INPUT_HEIGHT);
        passwordField.setStyle(StyleConstants.input());
        
        TextField passwordVisible = new TextField();
        passwordVisible.setPrefHeight(StyleConstants.INPUT_HEIGHT);
        passwordVisible.setStyle(StyleConstants.input());
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);
        
        // Bind text fields
        passwordField.textProperty().bindBidirectional(passwordVisible.textProperty());
        
        // Toggle button
        Button toggleButton = new Button("👁");
        toggleButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-text-fill: " + ColorScheme.GRAY_500_HEX + "; " +
            "-fx-font-size: 16px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 0;"
        );
        StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(toggleButton, new Insets(0, 14, 0, 0));
        
        toggleButton.setOnAction(e -> {
            boolean isVisible = passwordVisible.isVisible();
            passwordVisible.setVisible(!isVisible);
            passwordVisible.setManaged(!isVisible);
            passwordField.setVisible(isVisible);
            passwordField.setManaged(isVisible);
            toggleButton.setText(isVisible ? "👁" : "🙈");
        });
        
        // Focus listeners
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle(StyleConstants.inputFocus());
                passwordVisible.setStyle(StyleConstants.inputFocus());
            } else {
                passwordField.setStyle(StyleConstants.input());
                passwordVisible.setStyle(StyleConstants.input());
            }
        });
        
        passwordVisible.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle(StyleConstants.inputFocus());
                passwordVisible.setStyle(StyleConstants.inputFocus());
            } else {
                passwordField.setStyle(StyleConstants.input());
                passwordVisible.setStyle(StyleConstants.input());
            }
        });
        
        passwordContainer.getChildren().addAll(passwordField, passwordVisible, toggleButton);
        passwordGroup.getChildren().addAll(passwordLabelBox, passwordContainer);

        // Role dropdown
        VBox roleGroup = new VBox(6);
        Label roleLabel = new Label("Role");
        roleLabel.setStyle(StyleConstants.label());
        
        Label roleStar = new Label("*");
        roleStar.setTextFill(ColorScheme.ERROR_500);
        
        HBox roleLabelBox = new HBox(2);
        roleLabelBox.getChildren().addAll(roleLabel, roleStar);
        
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("ADMIN", "DEPARTMENT_HEAD", "STUDENT");
        roleComboBox.setPrefHeight(StyleConstants.INPUT_HEIGHT);
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleComboBox.setStyle(StyleConstants.input());
        
        // Focus listener
        roleComboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                roleComboBox.setStyle(StyleConstants.inputFocus());
            } else {
                roleComboBox.setStyle(StyleConstants.input());
            }
        });
        
        roleGroup.getChildren().addAll(roleLabelBox, roleComboBox);

        // Remember me & Forgot password
        HBox optionsRow = new HBox();
        optionsRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(optionsRow, Priority.ALWAYS);
        
        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.setFont(FontLoader.getOutfit(13));
        rememberMe.setTextFill(ColorScheme.GRAY_500);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.setFont(FontLoader.getOutfit(13));
        forgotPassword.setStyle(
            "-fx-text-fill: " + ColorScheme.BRAND_500_HEX + "; " +
            "-fx-border-color: transparent; " +
            "-fx-padding: 0; " +
            "-fx-underline: false;"
        );
        forgotPassword.setOnMouseEntered(e -> forgotPassword.setStyle(
            "-fx-text-fill: " + ColorScheme.BRAND_600_HEX + "; " +
            "-fx-border-color: transparent; " +
            "-fx-padding: 0; " +
            "-fx-underline: true;"
        ));
        forgotPassword.setOnMouseExited(e -> forgotPassword.setStyle(
            "-fx-text-fill: " + ColorScheme.BRAND_500_HEX + "; " +
            "-fx-border-color: transparent; " +
            "-fx-padding: 0; " +
            "-fx-underline: false;"
        ));
        
        optionsRow.getChildren().addAll(rememberMe, spacer, forgotPassword);

        // Sign in button
        Button signInButton = new Button("Sign In");
        signInButton.setMaxWidth(Double.MAX_VALUE);
        signInButton.setPrefHeight(StyleConstants.BUTTON_HEIGHT);
        signInButton.setFont(FontLoader.getOutfitSemiBold(14));
        signInButton.setStyle(StyleConstants.buttonPrimary());
        
        signInButton.setOnMouseEntered(e -> signInButton.setStyle(StyleConstants.buttonPrimaryHover()));
        signInButton.setOnMouseExited(e -> signInButton.setStyle(StyleConstants.buttonPrimary()));
        
        signInButton.setOnAction(e -> handleLogin(
            usernameField.getText().trim(),
            passwordField.getText().trim(),
            roleComboBox.getValue(),
            errorContainer,
            errorLabel
        ));

        // Add all to form
        formContainer.getChildren().addAll(
            header,
            errorContainer,
            usernameGroup,
            passwordGroup,
            roleGroup,
            optionsRow,
            signInButton
        );

        // Focus on username field
        usernameField.requestFocus();
        
        // Enter key navigation
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> roleComboBox.requestFocus());
        passwordVisible.setOnAction(e -> roleComboBox.requestFocus());
        roleComboBox.setOnAction(e -> signInButton.fire());

        panel.getChildren().add(formContainer);
        return panel;
    }

    private void handleLogin(String username, String password, String role, VBox errorContainer, Label errorLabel) {
        // Hide error
        errorContainer.setVisible(false);
        errorContainer.setManaged(false);

        // Validate username
        ValidationResult usernameResult = ValidationUtil.validateRequired(username, "Username");
        if (!usernameResult.isValid()) {
            showError(errorContainer, errorLabel, usernameResult.getErrorMessage());
            return;
        }

        // Validate password
        ValidationResult passwordResult = ValidationUtil.validatePassword(password);
        if (!passwordResult.isValid()) {
            showError(errorContainer, errorLabel, passwordResult.getErrorMessage());
            return;
        }

        // Validate role
        if (role == null || role.isEmpty()) {
            showError(errorContainer, errorLabel, "Please select your role.");
            return;
        }

        // Attempt login
        try {
            User user = userDao.loginWithRole(username, password, role);
            if (user != null) {
                // Check if student account is approved
                if (user.isStudent() && !user.isApproved()) {
                    showError(errorContainer, errorLabel, "Your account is pending admin approval. Please wait for approval before logging in.");
                    return;
                }

                // Route to appropriate dashboard based on role
                if (user.isAdmin()) {
                    new AdminDashboard(stage, user).show();
                } else if (user.isDepartmentHead()) {
                    new DepartmentHeadDashboard(stage, user).show();
                } else if (user.isStudent()) {
                    new StudentDashboard(stage, user).show();
                } else {
                    showError(errorContainer, errorLabel, "Unknown user role. Please contact administrator.");
                }
            } else {
                showError(errorContainer, errorLabel, "Invalid username, password, or role. Please try again.");
            }
        } catch (SQLException e) {
            showError(errorContainer, errorLabel, "Database error: Unable to connect. Please try again later.");
            e.printStackTrace();
        }
    }

    private void showError(VBox container, Label label, String message) {
        label.setText(message);
        container.setVisible(true);
        container.setManaged(true);
    }
}
