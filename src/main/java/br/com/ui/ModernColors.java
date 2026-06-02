package br.com.ui;

import java.awt.*;


public class ModernColors {
    
    public static final Color PRIMARY_BLUE = new Color(37, 99, 235);       
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);       
    public static final Color LIGHT_BLUE = new Color(219, 234, 254);       
    public static final Color DEEP_BLUE = new Color(30, 64, 175);          
    public static final Color NAVY = new Color(15, 23, 42);                

    
    public static final Color SUCCESS_GREEN = new Color(34, 197, 94);      
    public static final Color DANGER_RED = new Color(239, 68, 68);         
    public static final Color WARNING_ORANGE = new Color(245, 158, 11);    
    public static final Color INFO_BLUE = new Color(14, 165, 233);         
    public static final Color TEAL = new Color(13, 148, 136);              

    
    public static final Color WHITE = new Color(255, 255, 255);            
    public static final Color LIGHT_GRAY = new Color(241, 245, 249);       
    public static final Color BORDER_GRAY = new Color(226, 232, 240);      
    public static final Color FIELD_BORDER = new Color(203, 213, 225);     
    public static final Color TEXT_GRAY = new Color(100, 116, 139);        
    public static final Color DARK_GRAY = new Color(51, 65, 85);           
    public static final Color VERY_LIGHT_GRAY = new Color(248, 250, 252);  

    
    public static final Color BG_PRIMARY = new Color(248, 250, 252);       
    public static final Color BG_SECONDARY = new Color(241, 245, 249);     
    public static final Color CARD_HOVER = new Color(239, 246, 255);       

    
    public static final Color SHADOW = new Color(15, 23, 42, 26);
    public static final Color SHADOW_LIGHT = new Color(15, 23, 42, 12);

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
