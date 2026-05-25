package com.university.crs.gui;

/**
 * Style constants based on the client project design system.
 * Contains measurements, shadows, border radii, and reusable CSS styles.
 */
public class StyleConstants {
    
    // ── Spacing ───────────────────────────────────────────────────────────
    public static final double SPACING_XS = 4;
    public static final double SPACING_SM = 8;
    public static final double SPACING_MD = 12;
    public static final double SPACING_LG = 16;
    public static final double SPACING_XL = 24;
    public static final double SPACING_2XL = 32;
    
    // ── Border Radius ─────────────────────────────────────────────────────
    public static final double RADIUS_SM = 6;
    public static final double RADIUS_MD = 8;
    public static final double RADIUS_LG = 12;
    public static final double RADIUS_XL = 16;
    public static final double RADIUS_FULL = 9999;
    
    // ── Shadows ───────────────────────────────────────────────────────────
    public static final String SHADOW_XS = "dropshadow(gaussian, rgba(16, 24, 40, 0.05), 2, 0, 0, 1)";
    public static final String SHADOW_SM = "dropshadow(gaussian, rgba(16, 24, 40, 0.1), 3, 0, 0, 1), dropshadow(gaussian, rgba(16, 24, 40, 0.06), 2, 0, 0, 1)";
    public static final String SHADOW_MD = "dropshadow(gaussian, rgba(16, 24, 40, 0.1), 8, 0, 0, 4), dropshadow(gaussian, rgba(16, 24, 40, 0.06), 4, 0, 0, 2)";
    public static final String SHADOW_LG = "dropshadow(gaussian, rgba(16, 24, 40, 0.08), 16, 0, 0, 12), dropshadow(gaussian, rgba(16, 24, 40, 0.03), 6, 0, 0, 4)";
    
    // ── Component Heights ─────────────────────────────────────────────────
    public static final double INPUT_HEIGHT = 44;
    public static final double BUTTON_HEIGHT = 48;
    public static final double HEADER_HEIGHT = 64;
    public static final double SIDEBAR_WIDTH_EXPANDED = 290;
    public static final double SIDEBAR_WIDTH_COLLAPSED = 90;
    
    // ── Font Sizes ────────────────────────────────────────────────────────
    public static final double FONT_XS = 12;
    public static final double FONT_SM = 14;
    public static final double FONT_BASE = 16;
    public static final double FONT_LG = 18;
    public static final double FONT_XL = 20;
    public static final double FONT_2XL = 24;
    public static final double FONT_3XL = 30;
    public static final double FONT_4XL = 36;
    
    // ── Reusable CSS Styles ───────────────────────────────────────────────
    
    /**
     * Standard card style (white background, border, shadow)
     */
    public static String card() {
        return String.format(
            "-fx-background-color: white; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-effect: %s;",
            ColorScheme.GRAY_200_HEX,
            RADIUS_LG,
            RADIUS_LG,
            SHADOW_SM
        );
    }
    
    /**
     * Input field style (default state)
     */
    public static String input() {
        return String.format(
            "-fx-background-color: transparent; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 10 14; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-text-fill: %s; " +
            "-fx-prompt-text-fill: %s;",
            ColorScheme.GRAY_300_HEX,
            RADIUS_MD,
            RADIUS_MD,
            FONT_SM,
            ColorScheme.GRAY_900_HEX,
            ColorScheme.GRAY_400_HEX
        );
    }
    
    /**
     * Input field focus state
     */
    public static String inputFocus() {
        return String.format(
            "-fx-background-color: white; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 10 14; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-text-fill: %s;",
            ColorScheme.BRAND_300_HEX,
            RADIUS_MD,
            RADIUS_MD,
            FONT_SM,
            ColorScheme.GRAY_900_HEX
        );
    }
    
    /**
     * Input field error state
     */
    public static String inputError() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 10 14; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-text-fill: %s;",
            ColorScheme.ERROR_50_HEX,
            ColorScheme.ERROR_300_HEX,
            RADIUS_MD,
            RADIUS_MD,
            FONT_SM,
            ColorScheme.GRAY_900_HEX
        );
    }
    
    /**
     * Primary button style
     */
    public static String buttonPrimary() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 12 16; " +
            "-fx-cursor: hand; " +
            "-fx-effect: %s;",
            ColorScheme.COMPANY_BLUE_HEX,
            FONT_SM,
            RADIUS_MD,
            SHADOW_XS
        );
    }
    
    /**
     * Primary button hover state
     */
    public static String buttonPrimaryHover() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 12 16; " +
            "-fx-cursor: hand; " +
            "-fx-effect: %s;",
            ColorScheme.COMPANY_ORANGE_HEX,
            FONT_SM,
            RADIUS_MD,
            SHADOW_XS
        );
    }
    
    /**
     * Secondary button style
     */
    public static String buttonSecondary() {
        return String.format(
            "-fx-background-color: white; " +
            "-fx-border-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-padding: 12 16; " +
            "-fx-cursor: hand; " +
            "-fx-effect: %s;",
            ColorScheme.GRAY_300_HEX,
            ColorScheme.GRAY_700_HEX,
            FONT_SM,
            RADIUS_MD,
            RADIUS_MD,
            SHADOW_XS
        );
    }
    
    /**
     * Success button style
     */
    public static String buttonSuccess() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 12 16; " +
            "-fx-cursor: hand;",
            ColorScheme.SUCCESS_500_HEX,
            FONT_SM,
            RADIUS_MD
        );
    }
    
    /**
     * Danger button style
     */
    public static String buttonDanger() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 12 16; " +
            "-fx-cursor: hand;",
            ColorScheme.ERROR_500_HEX,
            FONT_SM,
            RADIUS_MD
        );
    }
    
    /**
     * Label style
     */
    public static String label() {
        return String.format(
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-text-fill: black;",
            FONT_SM
        );
    }
    
    /**
     * Status badge - approved
     */
    public static String badgeSuccess() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 2 8;",
            ColorScheme.SUCCESS_50_HEX,
            ColorScheme.SUCCESS_700_HEX,
            FONT_XS,
            RADIUS_FULL
        );
    }
    
    /**
     * Status badge - pending
     */
    public static String badgeWarning() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 2 8;",
            ColorScheme.WARNING_50_HEX,
            ColorScheme.WARNING_700_HEX,
            FONT_XS,
            RADIUS_FULL
        );
    }
    
    /**
     * Status badge - rejected/error
     */
    public static String badgeError() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 2 8;",
            ColorScheme.ERROR_50_HEX,
            ColorScheme.ERROR_700_HEX,
            FONT_XS,
            RADIUS_FULL
        );
    }
    
    /**
     * Sidebar menu item (inactive)
     */
    public static String menuItem() {
        return String.format(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 12; " +
            "-fx-cursor: hand;",
            FONT_SM,
            RADIUS_MD
        );
    }
    
    /**
     * Sidebar menu item (active)
     */
    public static String menuItemActive() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 12; " +
            "-fx-cursor: hand;",
            ColorScheme.GRAY_100_HEX,
            FONT_SM,
            RADIUS_MD
        );
    }
    
    /**
     * Sidebar menu item (hover)
     */
    public static String menuItemHover() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-padding: 12; " +
            "-fx-cursor: hand;",
            ColorScheme.GRAY_100_HEX,
            FONT_SM,
            RADIUS_MD
        );
    }
}
