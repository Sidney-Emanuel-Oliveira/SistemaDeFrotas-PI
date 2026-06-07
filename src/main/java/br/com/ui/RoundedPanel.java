package br.com.ui;

import javax.swing.*;
import java.awt.*;


public class RoundedPanel extends JPanel {
    private int arcRadius = 14;
    private Color shadowColor = ModernColors.SHADOW_LIGHT;
    private int shadowSize = 5;
    private Color backgroundColor = ModernColors.WHITE;
    private Color borderColor = ModernColors.BORDER_GRAY;

    public RoundedPanel() {
        this.setOpaque(false); 
        this.setLayout(null);
    }

    public RoundedPanel(LayoutManager layout) {
        this.setOpaque(false);
        this.setLayout(layout);
    }

    public RoundedPanel(int arcRadius) {
        this();
        this.arcRadius = arcRadius;
    }

    public RoundedPanel(int arcRadius, Color backgroundColor) {
        this();
        this.arcRadius = arcRadius;
        this.backgroundColor = backgroundColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        
        int contentWidth = width - shadowSize - 1;
        int contentHeight = height - shadowSize - 1;

        if (shadowSize > 0) {
            g2d.setColor(shadowColor);
            g2d.fillRoundRect(shadowSize, shadowSize, contentWidth - 1, contentHeight - 1, arcRadius, arcRadius);
        }
        
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, contentWidth, contentHeight, arcRadius, arcRadius);
        
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(0, 0, contentWidth, contentHeight, arcRadius, arcRadius);

        g2d.dispose(); 
        super.paintComponent(g);
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setArcRadius(int arcRadius) {
        this.arcRadius = arcRadius;
        repaint();
    }

    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
        repaint();
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }
}