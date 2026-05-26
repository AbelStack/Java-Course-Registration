package com.university.crs.gui;

import javafx.scene.paint.Color;

/**
 * Color scheme based on the client project design system.
 * All colors are from the Tailwind-based Vue.js client application.
 */
public class ColorScheme {
    
    // ── Brand Colors ──────────────────────────────────────────────────────
    public static final Color BRAND_500 = Color.web("#465fff");
    public static final Color BRAND_600 = Color.web("#3641f5");
    public static final Color BRAND_300 = Color.web("#9cb9ff");
    public static final Color BRAND_50 = Color.web("#ecf3ff");
    
    // Company specific colors
    public static final Color COMPANY_BLUE = Color.web("#0a5098");
    public static final Color COMPANY_ORANGE = Color.web("#f3a21b");
    
    // ── Gray Scale ────────────────────────────────────────────────────────
    public static final Color GRAY_25 = Color.web("#fcfcfd");
    public static final Color GRAY_50 = Color.web("#f9fafb");
    public static final Color GRAY_100 = Color.web("#f2f4f7");
    public static final Color GRAY_200 = Color.web("#e4e7ec");
    public static final Color GRAY_300 = Color.web("#d0d5dd");
    public static final Color GRAY_400 = Color.web("#98a2b3");
    public static final Color GRAY_500 = Color.web("#667085");
    public static final Color GRAY_600 = Color.web("#475467");
    public static final Color GRAY_700 = Color.web("#344054");
    public static final Color GRAY_800 = Color.web("#1d2939");
    public static final Color GRAY_900 = Color.web("#101828");
    public static final Color GRAY_950 = Color.web("#0c111d");
    
    // ── Status Colors ─────────────────────────────────────────────────────
    public static final Color PRIMARY_50 = Color.web("#ecf3ff");
    public static final Color PRIMARY_500 = Color.web("#465fff");
    public static final Color PRIMARY_600 = Color.web("#3641f5");
    public static final Color PRIMARY_700 = Color.web("#2a33cc");
    
    public static final Color INFO_500 = Color.web("#0ea5e9");
    
    public static final Color SUCCESS_50 = Color.web("#ecfdf3");
    public static final Color SUCCESS_200 = Color.web("#a7f3d0");
    public static final Color SUCCESS_500 = Color.web("#12b76a");
    public static final Color SUCCESS_600 = Color.web("#039855");
    public static final Color SUCCESS_700 = Color.web("#027a48");
    
    public static final Color ERROR_50 = Color.web("#fef3f2");
    public static final Color ERROR_200 = Color.web("#fecdca");
    public static final Color ERROR_300 = Color.web("#fda29b");
    public static final Color ERROR_500 = Color.web("#f04438");
    public static final Color ERROR_600 = Color.web("#d92d20");
    public static final Color ERROR_700 = Color.web("#b42318");
    
    public static final Color WARNING_50 = Color.web("#fffaeb");
    public static final Color WARNING_500 = Color.web("#f79009");
    public static final Color WARNING_600 = Color.web("#dc6803");
    public static final Color WARNING_700 = Color.web("#b54708");
    
    // White
    public static final Color WHITE = Color.WHITE;
    
    // ── Hex Strings (for CSS) ─────────────────────────────────────────────
    public static final String BRAND_500_HEX = "#465fff";
    public static final String BRAND_600_HEX = "#3641f5";
    public static final String BRAND_300_HEX = "#9cb9ff";
    public static final String BRAND_50_HEX = "#ecf3ff";
    
    public static final String COMPANY_BLUE_HEX = "#0a5098";
    public static final String COMPANY_ORANGE_HEX = "#f3a21b";
    
    public static final String GRAY_50_HEX = "#f9fafb";
    public static final String GRAY_100_HEX = "#f2f4f7";
    public static final String GRAY_200_HEX = "#e4e7ec";
    public static final String GRAY_300_HEX = "#d0d5dd";
    public static final String GRAY_400_HEX = "#98a2b3";
    public static final String GRAY_500_HEX = "#667085";
    public static final String GRAY_600_HEX = "#475467";
    public static final String GRAY_700_HEX = "#344054";
    public static final String GRAY_800_HEX = "#1d2939";
    public static final String GRAY_900_HEX = "#101828";
    
    public static final String PRIMARY_50_HEX = "#ecf3ff";
    public static final String PRIMARY_500_HEX = "#465fff";
    public static final String PRIMARY_600_HEX = "#3641f5";
    public static final String PRIMARY_700_HEX = "#2a33cc";
    
    public static final String INFO_500_HEX = "#0ea5e9";
    
    public static final String SUCCESS_50_HEX = "#ecfdf3";
    public static final String SUCCESS_200_HEX = "#a7f3d0";
    public static final String SUCCESS_500_HEX = "#12b76a";
    public static final String SUCCESS_600_HEX = "#039855";
    public static final String SUCCESS_700_HEX = "#027a48";
    
    public static final String ERROR_50_HEX = "#fef3f2";
    public static final String ERROR_200_HEX = "#fecdca";
    public static final String ERROR_300_HEX = "#fda29b";
    public static final String ERROR_500_HEX = "#f04438";
    public static final String ERROR_600_HEX = "#d92d20";
    public static final String ERROR_700_HEX = "#b42318";
    
    public static final String WARNING_50_HEX = "#fffaeb";
    public static final String WARNING_500_HEX = "#f79009";
    public static final String WARNING_600_HEX = "#dc6803";
    public static final String WARNING_700_HEX = "#b54708";
    
    // ── Legacy Compatibility ──────────────────────────────────────────────
    // Keep these for backward compatibility with existing code
    public static final Color BACKGROUND = GRAY_50;
    public static final Color DARK_TEXT = GRAY_900;
    public static final Color MEDIUM_TEXT = GRAY_700;
    public static final Color LIGHT_TEXT = GRAY_500;
    public static final Color SOFT_GRAY = GRAY_200;
    
    public static final Color BUTTON_PRIMARY = COMPANY_BLUE;
    public static final Color BUTTON_HOVER = COMPANY_ORANGE;
    public static final Color LIGHT_BLUE = BRAND_300;
    public static final Color MEDIUM_BLUE = BRAND_500;
    public static final Color DEEP_BLUE = BRAND_600;
    
    public static final Color SUCCESS = SUCCESS_500;
    public static final Color DANGER = ERROR_500;
    public static final Color WARNING = WARNING_500;
    
    public static final String BACKGROUND_HEX = GRAY_50_HEX;
    public static final String SOFT_GRAY_HEX = GRAY_200_HEX;
    public static final String BUTTON_PRIMARY_HEX = COMPANY_BLUE_HEX;
    public static final String BUTTON_HOVER_HEX = COMPANY_ORANGE_HEX;
    public static final String LIGHT_BLUE_HEX = BRAND_300_HEX;
    public static final String MEDIUM_BLUE_HEX = BRAND_500_HEX;
    public static final String DANGER_HEX = ERROR_500_HEX;
    public static final String SUCCESS_HEX = SUCCESS_500_HEX;
    public static final String WARNING_HEX = WARNING_500_HEX;
    public static final String PURPLE_HEX = "#7a5af8";
    
    // Gradients (for sidebar, etc.)
    public static final String GRADIENT_DARK_BLUE = "linear-gradient(180deg, #0a5098 0%, #083d73 100%)";
    public static final String GRADIENT_SIDEBAR = "#ffffff";
    
    // ── Helper Methods ────────────────────────────────────────────────────
    
    /**
     * Get primary button style (for backward compatibility)
     */
    public static String getPrimaryButtonStyle() {
        return StyleConstants.buttonPrimary();
    }
    
    /**
     * Get primary button hover style (for backward compatibility)
     */
    public static String getPrimaryButtonHoverStyle() {
        return StyleConstants.buttonPrimaryHover();
    }
    
    /**
     * Get success button style
     */
    public static String getSuccessButtonStyle() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;",
            SUCCESS_600_HEX
        );
    }
    
    /**
     * Get success button hover style
     */
    public static String getSuccessButtonHoverStyle() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;",
            SUCCESS_700_HEX
        );
    }
    
    /**
     * Get danger button style
     */
    public static String getDangerButtonStyle() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;",
            ERROR_600_HEX
        );
    }
    
    /**
     * Get danger button hover style
     */
    public static String getDangerButtonHoverStyle() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;",
            ERROR_700_HEX
        );
    }
}
