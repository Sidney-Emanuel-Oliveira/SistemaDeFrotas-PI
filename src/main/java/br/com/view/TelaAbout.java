package br.com.view;

import javax.swing.*;

public class TelaAbout extends JPanel {

    public TelaAbout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titulo = new JLabel("Sistema de Controle de Frotas");
        titulo.setFont(titulo.getFont().deriveFont(28f));
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel versao = new JLabel("Versão 1.0");
        versao.setFont(versao.getFont().deriveFont(14f));
        versao.setAlignmentX(CENTER_ALIGNMENT);

        JLabel empresa = new JLabel("Desenvolvido para: GynLog");
        empresa.setFont(empresa.getFont().deriveFont(14f));
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
        descricao.setBackground(new java.awt.Color(240, 240, 240));
        descricao.setBorder(BorderFactory.createTitledBorder("Sobre"));

        add(descricao);
        add(Box.createVerticalGlue());
    }
}

