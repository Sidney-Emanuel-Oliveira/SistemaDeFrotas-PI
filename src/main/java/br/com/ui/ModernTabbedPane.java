package br.com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;


public class ModernTabbedPane extends JTabbedPane {

    public ModernTabbedPane() {
        super();
        setUI(new ModernTabbedPaneUI());
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setBackground(ModernColors.BG_SECONDARY);
        setForeground(ModernColors.DARK_GRAY);
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(false);
        setOpaque(true);
    }

    
    private static class ModernTabbedPaneUI extends BasicTabbedPaneUI {

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(12, 26, 12, 26); 
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(0, 0, 0, 0);
        }

        
        @Override
        protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(ModernColors.BG_SECONDARY);
            g2d.fillRect(0, 0, tabPane.getWidth(), calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight));
            g2d.dispose();
            super.paintTabArea(g, tabPlacement, selectedIndex);
        }

        
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean isHover = tabIndex == getRolloverTab();

            if (isSelected) {
                
                Color inicio = isHover ? ModernColors.DEEP_BLUE : ModernColors.ACCENT_BLUE;
                Color fim = isHover ? (ModernColors.isDarkTheme() ? ModernColors.BG_PRIMARY : ModernColors.NAVY) : ModernColors.PRIMARY_BLUE;
                GradientPaint gradient = new GradientPaint(x, y, inicio, x + w, y + h, fim);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(x + 5, y + 5, w - 10, h - 7, 12, 12);
            } else {
                
                g2d.setColor(isHover ? ModernColors.LIGHT_BLUE : ModernColors.BG_SECONDARY);
                g2d.fillRoundRect(x + 5, y + 6, w - 10, h - 8, 12, 12);
            }
            g2d.dispose();
        }

        
        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!isSelected) {
                g2d.setColor(ModernColors.FIELD_BORDER);
                g2d.drawLine(x + 14, y + h - 3, x + w - 14, y + h - 3);
            }
            g2d.dispose();
        }

        
        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            g.setColor(ModernColors.BORDER_GRAY);
            g.drawLine(0, 0, tabPane.getWidth(), 0);
        }

        
        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                 int tabIndex, String title, Rectangle textRect,
                                 boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setFont(font);
            boolean isHover = tabIndex == getRolloverTab();
            if (isSelected) {
                g2d.setColor(Color.WHITE);
            } else if (isHover) {
                g2d.setColor(ModernColors.DEEP_BLUE);
            } else {
                g2d.setColor(ModernColors.DARK_GRAY);
            }
            int x = textRect.x + 5;
            int y = textRect.y + metrics.getAscent();
            g2d.drawString(title, x, y);
            g2d.dispose();
        }

        
        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return fontHeight + 28;
        }

        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 24;
        }
    }
}