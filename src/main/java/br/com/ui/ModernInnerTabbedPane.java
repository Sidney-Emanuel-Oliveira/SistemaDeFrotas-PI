package br.com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;


public class ModernInnerTabbedPane extends JTabbedPane {

    public ModernInnerTabbedPane() {
        super();
        setUI(new ModernInnerTabbedPaneUI());
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setBackground(ModernColors.WHITE);
        setForeground(ModernColors.DARK_GRAY);
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(false); 
        setOpaque(true);
    }

    
    private static class ModernInnerTabbedPaneUI extends BasicTabbedPaneUI {

        
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(9, 20, 9, 20); 
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(0, 0, 0, 0);
        }

        
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean isHover = tabIndex == getRolloverTab(); 

            if (isSelected) {
                
                g2d.setColor(isHover ? ModernColors.DEEP_BLUE : ModernColors.PRIMARY_BLUE);
                g2d.fillRoundRect(x + 3, y + 3, w - 6, h - 5, 10, 10);
            } else {
                
                g2d.setColor(isHover ? ModernColors.LIGHT_BLUE : ModernColors.BG_SECONDARY);
                g2d.fillRoundRect(x + 3, y + 5, w - 6, h - 7, 10, 10);
            }
            g2d.dispose();
        }

        
        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
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

            
            int x = textRect.x + 6;
            int y = textRect.y + metrics.getAscent();
            g2d.drawString(title, x, y);
            g2d.dispose();
        }

        
        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return fontHeight + 20;
        }

        
        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 24;
        }
    }
}