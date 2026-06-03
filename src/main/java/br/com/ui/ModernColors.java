package br.com.ui;

import java.awt.*;

public class ModernColors {

    private static boolean darkTheme = false;

    public static Color PRIMARY_BLUE;
    public static Color ACCENT_BLUE;
    public static Color LIGHT_BLUE;
    public static Color DEEP_BLUE;
    public static Color NAVY;

    public static Color SUCCESS_GREEN;
    public static Color DANGER_RED;
    public static Color WARNING_ORANGE;
    public static Color INFO_BLUE;
    public static Color TEAL;

    public static Color WHITE;
    public static Color LIGHT_GRAY;
    public static Color BORDER_GRAY;
    public static Color FIELD_BORDER;
    public static Color TEXT_GRAY;
    public static Color DARK_GRAY;
    public static Color VERY_LIGHT_GRAY;

    public static Color BG_PRIMARY;
    public static Color BG_SECONDARY;
    public static Color CARD_HOVER;

    public static Color SHADOW;
    public static Color SHADOW_LIGHT;

    static {
        aplicarTemaClaro();
    }

    public static boolean isDarkTheme() {
        return darkTheme;
    }

    public static void alternarTema() {
        aplicarTema(!darkTheme);
    }

    public static void aplicarTema(boolean escuro) {
        if (escuro) {
            aplicarTemaEscuro();
        } else {
            aplicarTemaClaro();
        }
    }

    private static void aplicarTemaClaro() {
        darkTheme = false;
        PRIMARY_BLUE = new Color(37, 99, 235);
        ACCENT_BLUE = new Color(59, 130, 246);
        LIGHT_BLUE = new Color(219, 234, 254);
        DEEP_BLUE = new Color(30, 64, 175);
        NAVY = new Color(15, 23, 42);

        SUCCESS_GREEN = new Color(34, 197, 94);
        DANGER_RED = new Color(239, 68, 68);
        WARNING_ORANGE = new Color(245, 158, 11);
        INFO_BLUE = new Color(14, 165, 233);
        TEAL = new Color(13, 148, 136);

        WHITE = new Color(255, 255, 255);
        LIGHT_GRAY = new Color(241, 245, 249);
        BORDER_GRAY = new Color(226, 232, 240);
        FIELD_BORDER = new Color(203, 213, 225);
        TEXT_GRAY = new Color(100, 116, 139);
        DARK_GRAY = new Color(51, 65, 85);
        VERY_LIGHT_GRAY = new Color(248, 250, 252);

        BG_PRIMARY = new Color(248, 250, 252);
        BG_SECONDARY = new Color(241, 245, 249);
        CARD_HOVER = new Color(239, 246, 255);

        SHADOW = new Color(15, 23, 42, 26);
        SHADOW_LIGHT = new Color(15, 23, 42, 12);
    }

    private static void aplicarTemaEscuro() {
        darkTheme = true;
        PRIMARY_BLUE = new Color(59, 130, 246);
        ACCENT_BLUE = new Color(96, 165, 250);
        LIGHT_BLUE = new Color(30, 58, 138);
        DEEP_BLUE = new Color(37, 99, 235);
        NAVY = new Color(226, 232, 240);

        SUCCESS_GREEN = new Color(34, 197, 94);
        DANGER_RED = new Color(248, 113, 113);
        WARNING_ORANGE = new Color(251, 191, 36);
        INFO_BLUE = new Color(56, 189, 248);
        TEAL = new Color(45, 212, 191);

        WHITE = new Color(15, 23, 42);
        LIGHT_GRAY = new Color(30, 41, 59);
        BORDER_GRAY = new Color(51, 65, 85);
        FIELD_BORDER = new Color(71, 85, 105);
        TEXT_GRAY = new Color(148, 163, 184);
        DARK_GRAY = new Color(226, 232, 240);
        VERY_LIGHT_GRAY = new Color(17, 24, 39);

        BG_PRIMARY = new Color(2, 6, 23);
        BG_SECONDARY = new Color(15, 23, 42);
        CARD_HOVER = new Color(30, 41, 59);

        SHADOW = new Color(0, 0, 0, 90);
        SHADOW_LIGHT = new Color(0, 0, 0, 60);
    }

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
