package br.com.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    private Color primaryColor;
    private Color hoverColor;
    private Color pressedColor;
    private int arcRadius = 10;
    private boolean isHovered = false;

    public ModernButton(String text, Color primaryColor) {
        super(text);
        this.primaryColor = primaryColor;
        
        this.hoverColor = ajustarBrilho(primaryColor, 1.08f);
        this.pressedColor = ajustarBrilho(primaryColor, 0.88f);

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBorder(new EmptyBorder(8, 16, 8, 16));
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }
    
    private Color ajustarBrilho(Color color, float factor) {
        return new Color(
                Math.max(0, Math.min(255, (int) (color.getRed() * factor))),
                Math.max(0, Math.min(255, (int) (color.getGreen() * factor))),
                Math.max(0, Math.min(255, (int) (color.getBlue() * factor)))
        );
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        
        Color bgColor = primaryColor;
        if (!isEnabled()) {
            bgColor = ModernColors.FIELD_BORDER;
        } else if (getModel().isPressed()) {
            bgColor = pressedColor;
        } else if (isHovered) {
            bgColor = hoverColor;
        }
        
        int shadowOffset = isHovered && isEnabled() ? 3 : 2;
        g2d.setColor(ModernColors.withAlpha(ModernColors.isDarkTheme() ? Color.BLACK : ModernColors.NAVY, isHovered ? 45 : 22));
        g2d.fillRoundRect(2, shadowOffset, getWidth() - 4, getHeight() - shadowOffset - 1, arcRadius, arcRadius);

        
        GradientPaint gradient = new GradientPaint(
                0, 0, ajustarBrilho(bgColor, 1.04f),
                0, getHeight(), bgColor
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, arcRadius, arcRadius);
        
        g2d.setColor(ModernColors.withAlpha(Color.WHITE, isHovered ? 45 : 25));
        g2d.drawRoundRect(1, 1, getWidth() - 5, getHeight() - 6, arcRadius, arcRadius);
        
        FontMetrics fm = g2d.getFontMetrics(getFont());
        int stringX = (getWidth() - fm.stringWidth(getText())) / 2;
        int stringY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() - 2;

        g2d.setColor(isEnabled() ? getForeground() : ModernColors.TEXT_GRAY);
        g2d.setFont(getFont());
        g2d.drawString(getText(), stringX, stringY);
        g2d.dispose();
    }

    public void setArcRadius(int radius) {
        this.arcRadius = radius;
        repaint();
    }
}