package br.com.view;

import br.com.controller.VeiculoController;
import br.com.model.Veiculo;
import br.com.model.TipoVeiculo;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.utils.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class VehicleFormDialog extends JDialog {
    private VeiculoController controller;
    private Veiculo veiculoEdicao;
    private Runnable onSuccess;

    
    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtAno;
    private JComboBox<TipoVeiculo> cmbTipo;
    private JCheckBox chkAtivo;

    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public VehicleFormDialog(JFrame owner, Veiculo veiculoEdicao) {
        
        super(owner, veiculoEdicao != null ? "Editar Veículo" : "Novo Veículo", true);
        this.controller = new VeiculoController();
        this.veiculoEdicao = veiculoEdicao;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        setSize(500, 620);
        setLocationRelativeTo(owner); 
        setResizable(false);

        initializeComponents();
    }

    private void initializeComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ModernColors.BG_PRIMARY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel headerPanel = criarPainelCabecalho();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new RoundedPanel(12, ModernColors.WHITE);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1)
        ));

        
        JPanel formPanel = criarPainelFormulario();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = criarPainelBotoes();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(ModernColors.BG_PRIMARY);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 22, 20));
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);

        contentPanel.add(wrapperPanel, BorderLayout.CENTER);

        add(contentPanel);
    }

    private JPanel criarPainelCabecalho() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernColors.isDarkTheme() ? ModernColors.BG_SECONDARY : new Color(15, 23, 42));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        
        ImageIcon vehicleIcon = veiculoEdicao != null
                ? IconLoader.loadIconForType(veiculoEdicao.getTipo(), 40, 40)
                : IconLoader.loadCarIcon(40, 40);
        JLabel iconLabel = new JLabel(vehicleIcon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        String titulo = veiculoEdicao != null ? "Editar Veículo" : "Novo Veículo";
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(TITLE_FONT);
        lblTitulo.setForeground(Color.WHITE);

        String subtitulo = veiculoEdicao != null
                ? "Atualize as informações do veículo"
                : "Preencha os dados do novo veículo";
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(255, 255, 255, 200));

        JPanel textoPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textoPanel.setOpaque(false);
        textoPanel.add(lblTitulo);
        textoPanel.add(lblSubtitulo);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel);
        leftPanel.add(textoPanel);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel criarPainelFormulario() {
        JPanel mainFormPanel = new JPanel();
        mainFormPanel.setLayout(new BoxLayout(mainFormPanel, BoxLayout.Y_AXIS));
        mainFormPanel.setOpaque(false);
        mainFormPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel secaoBasica = new JLabel("Informações Básicas");
        secaoBasica.setFont(new Font("Segoe UI", Font.BOLD, 14));
        secaoBasica.setForeground(ModernColors.PRIMARY_BLUE);
        secaoBasica.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainFormPanel.add(secaoBasica);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainFormPanel.add(criarCampoFormulario("Placa:", "txtPlaca"));
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        mainFormPanel.add(criarCampoFormulario("Marca:", "txtMarca"));
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        mainFormPanel.add(criarCampoFormulario("Modelo:", "txtModelo"));
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        mainFormPanel.add(criarCampoFormulario("Ano:", "txtAno"));
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel secaoClassificacao = new JLabel("Classificação");
        secaoClassificacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        secaoClassificacao.setForeground(ModernColors.PRIMARY_BLUE);
        secaoClassificacao.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainFormPanel.add(secaoClassificacao);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel tipoPanel = new JPanel(new BorderLayout(8, 5));
        tipoPanel.setOpaque(false);
        tipoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        tipoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(LABEL_FONT);
        lblTipo.setForeground(ModernColors.DARK_GRAY);
        tipoPanel.add(lblTipo, BorderLayout.NORTH);

        cmbTipo = new JComboBox<>(TipoVeiculo.valuesCadastro());
        cmbTipo.setFont(FIELD_FONT);
        cmbTipo.setBackground(ModernColors.WHITE);
        cmbTipo.setForeground(ModernColors.DARK_GRAY);
        cmbTipo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        tipoPanel.add(cmbTipo, BorderLayout.CENTER);

        mainFormPanel.add(tipoPanel);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        statusPanel.setOpaque(false);
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        chkAtivo = new JCheckBox("Veículo Ativo");
        chkAtivo.setFont(LABEL_FONT);
        chkAtivo.setForeground(ModernColors.DARK_GRAY);
        chkAtivo.setOpaque(false);
        chkAtivo.setSelected(true);
        chkAtivo.setFocusPainted(false);
        statusPanel.add(chkAtivo);

        mainFormPanel.add(statusPanel);

        
        if (veiculoEdicao != null) {
            txtPlaca.setText(veiculoEdicao.getPlaca());
            txtMarca.setText(veiculoEdicao.getMarca());
            txtModelo.setText(veiculoEdicao.getModelo());
            txtAno.setText(veiculoEdicao.getFabricateYear());
            cmbTipo.setSelectedItem(veiculoEdicao.getTipoVeiculo());
            chkAtivo.setSelected(veiculoEdicao.getAtivo());
        }

        return mainFormPanel;
    }

    private JPanel criarCampoFormulario(String labelText, String fieldName) {
        JPanel panel = new JPanel(new BorderLayout(8, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(ModernColors.DARK_GRAY);
        panel.add(label, BorderLayout.NORTH);

        JTextField field = criarCampoTexto();
        panel.add(field, BorderLayout.CENTER);

        switch (fieldName) {
            case "txtPlaca" -> {
                txtPlaca = field;
                
                txtPlaca.addKeyListener(new java.awt.event.KeyAdapter() {
                    @Override
                    public void keyReleased(java.awt.event.KeyEvent e) {
                        String text = txtPlaca.getText();
                        int caretPosition = txtPlaca.getCaretPosition();
                        txtPlaca.setText(text.toUpperCase());
                        txtPlaca.setCaretPosition(Math.min(caretPosition, txtPlaca.getText().length()));
                    }
                });
            }
            case "txtMarca" -> txtMarca = field;
            case "txtModelo" -> txtModelo = field;
            case "txtAno" -> txtAno = field;
        }

        return panel;
    }

    private JTextField criarCampoTexto() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setBackground(ModernColors.WHITE);
        field.setForeground(ModernColors.DARK_GRAY);
        field.setCaretColor(ModernColors.DARK_GRAY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ModernColors.PRIMARY_BLUE, 2),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
        });

        return field;
    }

    private JPanel criarPainelBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        ModernButton btnCancelar = new ModernButton("Cancelar", ModernColors.TEXT_GRAY);
        btnCancelar.setPreferredSize(new Dimension(120, 42));
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.addActionListener(e -> dispose());

        ModernButton btnSalvar = new ModernButton("Salvar", ModernColors.PRIMARY_BLUE);
        btnSalvar.setPreferredSize(new Dimension(120, 42));
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.addActionListener(e -> salvar());

        panel.add(btnCancelar);
        panel.add(btnSalvar);

        return panel;
    }

    
    private void salvar() {
        try {
            String placa = txtPlaca.getText().trim().toUpperCase();
            String marca = txtMarca.getText().trim();
            String modelo = txtModelo.getText().trim();
            String ano = txtAno.getText().trim();
            TipoVeiculo tipoEnum = (TipoVeiculo) cmbTipo.getSelectedItem();
            String tipo = tipoEnum != null ? tipoEnum.getDescricao() : TipoVeiculo.CARRO.getDescricao();
            Boolean ativo = chkAtivo.isSelected();

            
            if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || ano.isEmpty()) {
                throw new IllegalArgumentException("Todos os campos são obrigatórios!");
            }

            
            if (!placa.matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")) {
                throw new IllegalArgumentException("Placa inválida! Use o formato ABC1234 ou ABC1D23 (Mercosul)");
            }

            
            try {
                int anoInt = Integer.parseInt(ano);
                int anoAtual = java.time.Year.now().getValue();
                if (anoInt < 1900 || anoInt > anoAtual + 1) {
                    throw new IllegalArgumentException("Ano inválido! Deve estar entre 1900 e " + (anoAtual + 1));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Ano deve ser um número válido!");
            }

            
            if (marca.length() < 2) {
                throw new IllegalArgumentException("Marca deve ter pelo menos 2 caracteres!");
            }
            if (modelo.length() < 2) {
                throw new IllegalArgumentException("Modelo deve ter pelo menos 2 caracteres!");
            }

            
            if (veiculoEdicao != null) {
                controller.atualizarVeiculo(veiculoEdicao.getIdVeiculo(), placa, marca, modelo, ano, ativo, tipo);
                JOptionPane.showMessageDialog(this,
                        "Veículo atualizado com sucesso!\nPlaca: " + placa,
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                controller.salvarVeiculo(placa, marca, modelo, ano, ativo, tipo);
                JOptionPane.showMessageDialog(this,
                        "Veículo cadastrado com sucesso!\nPlaca: " + placa,
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            
            if (onSuccess != null) onSuccess.run();
            dispose();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erro de Validação:\n" + e.getMessage(),
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erro ao salvar veículo:\n" + e.getMessage() +
                            "\n\nVerifique se não existe outro veículo com a mesma placa.",
                    "Erro de Sistema",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erro inesperado:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void setCallback(Runnable callback) {
        this.onSuccess = callback;
    }
}