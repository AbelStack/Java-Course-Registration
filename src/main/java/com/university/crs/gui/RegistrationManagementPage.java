package com.university.crs.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Registration Management page for Admin.
 * Admin can open/close registration periods and manage academic years/semesters.
 */
public class RegistrationManagementPage {

    public Node build() {
        VBox page = new VBox(StyleConstants.SPACING_XL);
        page.setPadding(new Insets(0));
        page.setStyle("-fx-background-color: transparent;");

        // Page header
        VBox headerText = new VBox(4);
        Label heading = new Label("Registration Management");
        heading.setFont(FontLoader.getOutfitSemiBold(24));
        heading.setTextFill(Color.BLACK);
        
        Label subtitle = new Label("Manage registration periods, academic years, and semesters");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(Color.BLACK);
        
        headerText.getChildren().addAll(heading, subtitle);

        // Registration Status Card
        VBox statusCard = buildRegistrationStatusCard();
        
        // Academic Years & Semesters Card
        VBox academicCard = buildAcademicManagementCard();

        page.getChildren().addAll(headerText, statusCard, academicCard);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildRegistrationStatusCard() {
        VBox card = new VBox(StyleConstants.SPACING_LG);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(StyleConstants.SPACING_XL));

        Label cardTitle = new Label("Registration Period Status");
        cardTitle.setFont(FontLoader.getOutfitSemiBold(18));
        cardTitle.setTextFill(Color.BLACK);

        // Current status
        HBox statusRow = new HBox(StyleConstants.SPACING_MD);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("Current Status:");
        statusLabel.setFont(FontLoader.getOutfit(14));
        statusLabel.setTextFill(Color.BLACK);
        
        Label statusBadge = new Label("CLOSED");
        statusBadge.setFont(FontLoader.getOutfitSemiBold(12));
        statusBadge.setTextFill(ColorScheme.ERROR_700);
        statusBadge.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-padding: 6 16; " +
            "-fx-background-radius: 12;",
            ColorScheme.ERROR_50_HEX
        ));
        
        statusRow.getChildren().addAll(statusLabel, statusBadge);

        // Action buttons
        HBox actionRow = new HBox(StyleConstants.SPACING_MD);
        actionRow.setPadding(new Insets(StyleConstants.SPACING_MD, 0, 0, 0));
        
        Button openBtn = new Button("Open Registration");
        openBtn.setFont(FontLoader.getOutfitSemiBold(14));
        openBtn.setTextFill(Color.WHITE);
        openBtn.setPrefHeight(45);
        openBtn.setPrefWidth(180);
        openBtn.setStyle(StyleConstants.buttonSuccess());
        openBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Open Registration");
            confirm.setHeaderText("Are you sure you want to open registration?");
            confirm.setContentText("Students will be able to submit course registration requests.");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // TODO: Update registration status in database
                    statusBadge.setText("OPEN");
                    statusBadge.setTextFill(ColorScheme.SUCCESS_700);
                    statusBadge.setStyle(String.format(
                        "-fx-background-color: %s; " +
                        "-fx-padding: 6 16; " +
                        "-fx-background-radius: 12;",
                        ColorScheme.SUCCESS_50_HEX
                    ));
                }
            });
        });
        
        Button closeBtn = new Button("Close Registration");
        closeBtn.setFont(FontLoader.getOutfitSemiBold(14));
        closeBtn.setTextFill(Color.WHITE);
        closeBtn.setPrefHeight(45);
        closeBtn.setPrefWidth(180);
        closeBtn.setStyle(StyleConstants.buttonDanger());
        closeBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Close Registration");
            confirm.setHeaderText("Are you sure you want to close registration?");
            confirm.setContentText("Students will not be able to submit new registration requests.");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // TODO: Update registration status in database
                    statusBadge.setText("CLOSED");
                    statusBadge.setTextFill(ColorScheme.ERROR_700);
                    statusBadge.setStyle(String.format(
                        "-fx-background-color: %s; " +
                        "-fx-padding: 6 16; " +
                        "-fx-background-radius: 12;",
                        ColorScheme.ERROR_50_HEX
                    ));
                }
            });
        });
        
        actionRow.getChildren().addAll(openBtn, closeBtn);

        card.getChildren().addAll(cardTitle, statusRow, actionRow);
        return card;
    }

    private VBox buildAcademicManagementCard() {
        VBox card = new VBox(StyleConstants.SPACING_LG);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(StyleConstants.SPACING_XL));

        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(headerRow, Priority.ALWAYS);
        
        Label cardTitle = new Label("Academic Years & Semesters");
        cardTitle.setFont(FontLoader.getOutfitSemiBold(18));
        cardTitle.setTextFill(Color.BLACK);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addBtn = new Button("+ Add Year/Semester");
        addBtn.setFont(FontLoader.getOutfitSemiBold(14));
        addBtn.setTextFill(Color.WHITE);
        addBtn.setPrefHeight(40);
        addBtn.setStyle(StyleConstants.buttonPrimary());
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(StyleConstants.buttonPrimaryHover()));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(StyleConstants.buttonPrimary()));
        addBtn.setOnAction(e -> showAddAcademicPeriodDialog());
        
        headerRow.getChildren().addAll(cardTitle, spacer, addBtn);

        // Academic periods list
        VBox periodsList = new VBox(StyleConstants.SPACING_SM);
        periodsList.setPadding(new Insets(StyleConstants.SPACING_MD, 0, 0, 0));
        
        // TODO: Load from database
        Label emptyLabel = new Label("No academic periods configured. Click 'Add Year/Semester' to create one.");
        emptyLabel.setFont(FontLoader.getOutfit(14));
        emptyLabel.setTextFill(Color.BLACK);
        emptyLabel.setPadding(new Insets(20, 0, 20, 0));
        periodsList.getChildren().add(emptyLabel);

        card.getChildren().addAll(headerRow, periodsList);
        return card;
    }

    private void showAddAcademicPeriodDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Academic Period");
        dialog.setHeaderText("Create a new academic year or semester");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField yearField = new TextField();
        yearField.setPromptText("e.g., 2024-2025");
        
        ComboBox<String> semesterCombo = new ComboBox<>();
        semesterCombo.getItems().addAll("Fall", "Spring", "Summer");
        semesterCombo.setPromptText("Select Semester");

        grid.add(new Label("Academic Year:"), 0, 0);
        grid.add(yearField, 1, 0);
        grid.add(new Label("Semester:"), 0, 1);
        grid.add(semesterCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Save to database
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Academic period created successfully!");
                success.showAndWait();
            }
        });
    }
}
