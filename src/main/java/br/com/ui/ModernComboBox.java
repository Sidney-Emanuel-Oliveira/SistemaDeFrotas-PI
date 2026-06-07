package br.com.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;


public class ModernComboBox<T> extends JComboBox<T> {

    public ModernComboBox() {
        super();
        personalizarComboBox();
    }

    public ModernComboBox(ComboBoxModel<T> aModel) {
        super(aModel);
        personalizarComboBox();
    }

    private void personalizarComboBox() {
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setBackground(ModernColors.WHITE);
        setForeground(ModernColors.DARK_GRAY);
        setFocusable(false); 
        setPreferredSize(new Dimension(150, 34));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▾");
                button.setFont(new Font("Segoe UI", Font.BOLD, 12));
                button.setForeground(ModernColors.TEXT_GRAY);
                button.setBackground(ModernColors.BG_SECONDARY);
                button.setBorder(new EmptyBorder(0, 6, 0, 6));
                button.setFocusPainted(false);
                return button;
            }
        });
        
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));

                
                if (isSelected) {
                    setBackground(ModernColors.PRIMARY_BLUE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(ModernColors.WHITE);
                    setForeground(ModernColors.DARK_GRAY);
                }

                setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 8));
                return c;
            }
        });
    }
}