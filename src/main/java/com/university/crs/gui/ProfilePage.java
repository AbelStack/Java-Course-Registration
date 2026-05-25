package com.university.crs.gui;

import com.university.crs.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Profile page — displays user profile information with edit capability.
 */
public class ProfilePage {

    private final User user;

    public ProfilePage(User user) {
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        page.setAlignment(Pos.TOP_CENTER);

        // Header
        Label heading = new Label("My Profile");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);

        // Profile card
        VBox profileCard = buildProfileCard();

        page.getChildren().addAll(heading, profileCard);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildProfileCard() {
        VBox card = new VBox(30);
        card.setMaxWidth(600);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(40, 50, 40, 50));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3);"
        );

        // Profile avatar with edit icon
        StackPane avatarContainer = createAvatar();

        // Profile information fields
        VBox infoSection = new VBox(20);
        infoSection.setAlignment(Pos.CENTER);

        infoSection.getChildren().addAll(
            createInfoRow("Full Name", getUserFullName()),
            createInfoRow("Username", user.getUsername()),
            createInfoRow("Email", getUserEmail()),
            createInfoRow("Department", getUserDepartment()),
            createInfoRow("Phone", getUserPhone())
        );

        // Edit Profile button
        Button editBtn = new Button("EDIT PROFILE");
        editBtn.setFont(FontLoader.getPoppinsBold(14));
        editBtn.setTextFill(Color.WHITE);
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setPrefHeight(50);
        editBtn.setStyle(ColorScheme.getPrimaryButtonStyle());
        editBtn.setOnMouseEntered(e -> editBtn.setStyle(ColorScheme.getPrimaryButtonHoverStyle()));
        editBtn.setOnMouseExited(e -> editBtn.setStyle(ColorScheme.getPrimaryButtonStyle()));
        editBtn.setOnAction(e -> showEditProfileDialog());

        VBox.setMargin(editBtn, new Insets(10, 0, 0, 0));

        card.getChildren().addAll(avatarContainer, infoSection, editBtn);
        return card;
    }

    private StackPane createAvatar() {
        StackPane container = new StackPane();
        container.setPrefSize(150, 150);

        // Outer circle (light gray background)
        Circle outerCircle = new Circle(75);
        outerCircle.setFill(Color.rgb(240, 242, 245));
        outerCircle.setStroke(Color.rgb(220, 225, 230));
        outerCircle.setStrokeWidth(3);

        // Avatar icon (simple person silhouette)
        VBox avatarIcon = new VBox();
        avatarIcon.setAlignment(Pos.CENTER);
        
        // Head
        Circle head = new Circle(20);
        head.setFill(ColorScheme.DEEP_BLUE);
        
        // Body (using a rounded rectangle shape)
        StackPane body = new StackPane();
        body.setPrefSize(50, 40);
        body.setTranslateY(15);
        body.setStyle(
            "-fx-background-color: " + ColorScheme.MEDIUM_BLUE_HEX + "; " +
            "-fx-background-radius: 25 25 0 0;"
        );
        
        avatarIcon.getChildren().addAll(head, body);

        // Edit icon (small circle with pencil)
        StackPane editIcon = new StackPane();
        editIcon.setPrefSize(35, 35);
        editIcon.setTranslateX(50);
        editIcon.setTranslateY(50);
        editIcon.setStyle(
            "-fx-background-color: " + ColorScheme.MEDIUM_BLUE_HEX + "; " +
            "-fx-background-radius: 50%;"
        );
        
        Label pencil = new Label("✏️");
        pencil.setFont(FontLoader.getInter(16));
        editIcon.getChildren().add(pencil);

        container.getChildren().addAll(outerCircle, avatarIcon, editIcon);
        return container;
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 0, 12, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

        Label labelText = new Label(label);
        labelText.setFont(FontLoader.getInter(14));
        labelText.setTextFill(Color.BLACK);
        labelText.setPrefWidth(150);

        Label valueText = new Label(value);
        valueText.setFont(FontLoader.getInter(15));
        valueText.setTextFill(ColorScheme.DARK_TEXT);
        HBox.setHgrow(valueText, Priority.ALWAYS);

        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    private String getUserFullName() {
        // In a real app, this would come from the database
        return user.isAdmin() ? "Admin User" : "John Doe";
    }

    private String getUserEmail() {
        // In a real app, this would come from the database
        return user.isAdmin() ? "admin@university.com" : "john.doe@email.com";
    }

    private String getUserDepartment() {
        // In a real app, this would come from the database
        return user.isAdmin() ? "Administration" : "Computer Science";
    }

    private String getUserPhone() {
        // In a real app, this would come from the database
        return "+1 123 456 7890";
    }

    private void showEditProfileDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(getUserFullName());
        TextField emailField = new TextField(getUserEmail());
        TextField deptField = new TextField(getUserDepartment());
        TextField phoneField = new TextField(getUserPhone());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Department:"), 0, 2);
        grid.add(deptField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // In a real app, save to database
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Profile updated successfully!");
                success.showAndWait();
            }
        });
    }
}
