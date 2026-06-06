package br.com.view;

import br.com.ui.ModernColors;

import javax.swing.*;


public class TelaAbout extends JPanel {

    public TelaAbout() {
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        setBackground(ModernColors.BG_PRIMARY);

        
        JLabel titulo = new JLabel("Sistema de Controle de Frotas");
        titulo.setFont(titulo.getFont().deriveFont(28f));
        titulo.setForeground(ModernColors.NAVY);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel versao = new JLabel("Versão 1.0");
        versao.setFont(versao.getFont().deriveFont(14f));
        versao.setForeground(ModernColors.TEXT_GRAY);
        versao.setAlignmentX(CENTER_ALIGNMENT);

        JLabel empresa = new JLabel("Desenvolvido para: GynLog");
        empresa.setFont(empresa.getFont().deriveFont(14f));
        empresa.setForeground(ModernColors.TEXT_GRAY);
        empresa.setAlignmentX(CENTER_ALIGNMENT);

        
        add(Box.createVerticalStrut(30));
        add(titulo);
        add(Box.createVerticalStrut(10));
        add(versao);
        add(Box.createVerticalStrut(10));
        add(empresa);
        add(Box.createVerticalStrut(50));

        
        JTextArea descricao = new JTextArea();
        descricao.setText("O Sistema de Controle de Frotas é uma solução completa para gerenciar gastos de frotas veiculares.\n\n" +
                "Funcionalidades:\n" +
                "- Cadastro completo de veículos\n" +
                "- Registro de tipos de despesas\n" +
                "- Movimentações de despesas\n" +
                "- Relatórios detalhados de gastos\n" +
                "- Exportação de dados em CSV\n" +
                "- Relatórios de combustível, IPVA, multas e manutenção\n" +
                "\n" +
                "Desenvolvido em Java com Interface Gráfica em Swing\n" +
                "Dados armazenados em arquivos de texto");

        
        descricao.setEditable(false);
        descricao.setLineWrap(true);      
        descricao.setWrapStyleWord(true);
        descricao.setBackground(ModernColors.isDarkTheme() ? ModernColors.BG_SECONDARY : ModernColors.WHITE);
        descricao.setForeground(ModernColors.isDarkTheme() ? ModernColors.DARK_GRAY : ModernColors.DARK_GRAY);
        descricao.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ModernColors.BORDER_GRAY),
                "Sobre",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12),
                ModernColors.NAVY
        ));

        add(descricao);

        
        add(Box.createVerticalGlue());
    }
}