package br.com.view;

import br.com.controller.MovimentacaoController;
import br.com.controller.VeiculoController;
import br.com.controller.TipoDespesaController;
import br.com.model.Movimentacao;
import br.com.model.Veiculo;
import br.com.model.TipoDespesa;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.utils.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


public class MovementFormDialog extends JDialog {
    private MovimentacaoController movimentacaoController;
    private VeiculoController veiculoController;
    private TipoDespesaController tipoDespesaController;
    private Movimentacao movimentacaoEdicao; 
    private Runnable onSuccess;

    private JComboBox<Veiculo> cmbVeiculo;
    private JComboBox<TipoDespesa> cmbTipoDespesa;
    private JTextField txtDescricao;
    private JTextField txtValor;
    private JTextField txtDistanciaKm;
    private JTextField txtLitrosCombustivel;

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public MovementFormDialog(JFrame owner, Movimentacao movimentacaoEdicao) {
        super(owner, movimentacaoEdicao != null ? "Editar Movimentação" : "Nova Movimentação", true);
        this.movimentacaoController = new MovimentacaoController();
        this.veiculoController = new VeiculoController();
        this.tipoDespesaController = new TipoDespesaController();
        this.movimentacaoEdicao = movimentacaoEdicao;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(550, 720);
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

        ImageIcon icon;
        if (movimentacaoEdicao != null) {
            try {
                TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacaoEdicao.getIdTipoDespesa());
                icon = IconLoader.loadIconForExpenseType(tipo != null ? tipo.getDescricao() : "Combustível", 40, 40);
            } catch (IOException e) {
                icon = IconLoader.loadCombustivelIcon(40, 40);
            }
        } else {
            icon = IconLoader.loadCombustivelIcon(40, 40);
        }

        JPanel iconBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(58, 58));
        iconBox.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconBox.add(iconLabel, BorderLayout.CENTER);

        String titulo = movimentacaoEdicao != null ? "Editar Movimentação" : "Nova Movimentação";
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(TITLE_FONT);
        lblTitulo.setForeground(Color.WHITE);

        String subtitulo = movimentacaoEdicao != null
                ? "Atualize os dados da movimentação"
                : "Registre uma nova despesa do veículo";
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(255, 255, 255, 200));

        JPanel textoPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textoPanel.setOpaque(false);
        textoPanel.add(lblTitulo);
        textoPanel.add(lblSubtitulo);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconBox);
        leftPanel.add(Box.createRigidArea(new Dimension(16, 0)));
        leftPanel.add(textoPanel);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        return headerPanel;
    }

    
    private JPanel criarPainelFormulario() {
        JPanel mainFormPanel = new JPanel();
        mainFormPanel.setLayout(new BoxLayout(mainFormPanel, BoxLayout.Y_AXIS));
        mainFormPanel.setOpaque(false);
        mainFormPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel secaoInfo = new JLabel("Informações da Movimentação");
        secaoInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        secaoInfo.setForeground(ModernColors.PRIMARY_BLUE);
        secaoInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainFormPanel.add(secaoInfo);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        
        JPanel veiculoPanel = new JPanel(new BorderLayout(8, 5));
        veiculoPanel.setOpaque(false);
        veiculoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        veiculoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblVeiculo = new JLabel("Veículo:");
        lblVeiculo.setFont(LABEL_FONT);
        lblVeiculo.setForeground(ModernColors.DARK_GRAY);
        veiculoPanel.add(lblVeiculo, BorderLayout.NORTH);

        cmbVeiculo = new JComboBox<>();
        cmbVeiculo.setFont(FIELD_FONT);
        cmbVeiculo.setBackground(ModernColors.WHITE);
        cmbVeiculo.setForeground(ModernColors.DARK_GRAY);
        cmbVeiculo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        carregarVeiculos();
        veiculoPanel.add(cmbVeiculo, BorderLayout.CENTER);
        mainFormPanel.add(veiculoPanel);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        
        JPanel tipoPanel = new JPanel(new BorderLayout(8, 5));
        tipoPanel.setOpaque(false);
        tipoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        tipoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTipo = new JLabel("Tipo de Despesa:");
        lblTipo.setFont(LABEL_FONT);
        lblTipo.setForeground(ModernColors.DARK_GRAY);
        tipoPanel.add(lblTipo, BorderLayout.NORTH);

        cmbTipoDespesa = new JComboBox<>();
        cmbTipoDespesa.setFont(FIELD_FONT);
        cmbTipoDespesa.setBackground(ModernColors.WHITE);
        cmbTipoDespesa.setForeground(ModernColors.DARK_GRAY);
        cmbTipoDespesa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        carregarTiposDespesa();
        tipoPanel.add(cmbTipoDespesa, BorderLayout.CENTER);
        mainFormPanel.add(tipoPanel);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel secaoDetalhes = new JLabel("Detalhes");
        secaoDetalhes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        secaoDetalhes.setForeground(ModernColors.PRIMARY_BLUE);
        secaoDetalhes.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainFormPanel.add(secaoDetalhes);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainFormPanel.add(criarCampoFormulario("Descrição:", "txtDescricao"));
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        mainFormPanel.add(criarCampoFormulario("Valor (R$):", "txtValor"));
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        
        JLabel secaoConsumo = new JLabel("Dados para Consumo Médio (opcional - somente combustível)");
        secaoConsumo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        secaoConsumo.setForeground(ModernColors.PRIMARY_BLUE);
        secaoConsumo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainFormPanel.add(secaoConsumo);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainFormPanel.add(criarCampoFormulario("Distância percorrida (km):", "txtDistanciaKm"));
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainFormPanel.add(criarCampoFormulario("Litros abastecidos:", "txtLitrosCombustivel"));

        
        if (movimentacaoEdicao != null) {
            try {
                Veiculo veiculo = veiculoController.obterVeiculoPorId(movimentacaoEdicao.getIdVeiculo());
                if (veiculo != null) cmbVeiculo.setSelectedItem(veiculo);

                TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacaoEdicao.getIdTipoDespesa());
                if (tipo != null) cmbTipoDespesa.setSelectedItem(tipo);

                txtDescricao.setText(movimentacaoEdicao.getDescricao());
                txtValor.setText(String.valueOf(movimentacaoEdicao.getValor()));
                if (movimentacaoEdicao.getDistanciaPercorridaKm() > 0) {
                    txtDistanciaKm.setText(String.valueOf(movimentacaoEdicao.getDistanciaPercorridaKm()));
                }
                if (movimentacaoEdicao.getLitrosCombustivel() > 0) {
                    txtLitrosCombustivel.setText(String.valueOf(movimentacaoEdicao.getLitrosCombustivel()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            case "txtDescricao" -> txtDescricao = field;
            case "txtValor" -> txtValor = field;
            case "txtDistanciaKm" -> txtDistanciaKm = field;
            case "txtLitrosCombustivel" -> txtLitrosCombustivel = field;
        }

        return panel;
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

    private void carregarVeiculos() {
        try {
            List<Veiculo> veiculos = veiculoController.obterTodosVeiculos();
            for (Veiculo v : veiculos) {
                cmbVeiculo.addItem(v);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar veículos", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTiposDespesa() {
        try {
            List<TipoDespesa> tipos = tipoDespesaController.obterTodosTipos();
            for (TipoDespesa t : tipos) {
                cmbTipoDespesa.addItem(t);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar tipos", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void salvar() {
        try {
            Veiculo veiculoSelecionado = (Veiculo) cmbVeiculo.getSelectedItem();
            TipoDespesa tipoSelecionado = (TipoDespesa) cmbTipoDespesa.getSelectedItem();

            if (veiculoSelecionado == null) {
                throw new IllegalArgumentException("Selecione um veículo!");
            }

            if (tipoSelecionado == null) {
                throw new IllegalArgumentException("Selecione um tipo de despesa!");
            }

            String descricao = txtDescricao.getText().trim();
            String valorStr = txtValor.getText().trim();
            String distanciaKm = txtDistanciaKm.getText().trim();
            String litrosCombustivel = txtLitrosCombustivel.getText().trim();
            boolean possuiDadosConsumo = !distanciaKm.isEmpty() || !litrosCombustivel.isEmpty();

            if (possuiDadosConsumo && !isTipoCombustivel(tipoSelecionado)) {
                throw new IllegalArgumentException("Os campos de distância e litros abastecidos só podem ser preenchidos quando o tipo de despesa for Combustível.");
            }

            if (descricao.isEmpty()) {
                throw new IllegalArgumentException("A descrição é obrigatória!");
            }

            if (descricao.length() < 3) {
                throw new IllegalArgumentException("A descrição deve ter pelo menos 3 caracteres!");
            }

            if (valorStr.isEmpty()) {
                throw new IllegalArgumentException("O valor é obrigatório!");
            }

            double valorDouble;
            try {
                
                String valorNormalizado = valorStr.replace(",", ".");
                valorDouble = Double.parseDouble(valorNormalizado);

                if (valorDouble <= 0) {
                    throw new IllegalArgumentException("O valor deve ser maior que zero!");
                }

                if (valorDouble > 1000000) {
                    throw new IllegalArgumentException("O valor não pode ser maior que R$ 1.000.000,00!");
                }

                
                valorStr = String.format("%.2f", valorDouble).replace(".", ",");

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor inválido! Use o formato: 123.45 ou 123,45");
            }

            
            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            
            if (movimentacaoEdicao != null) {
                movimentacaoController.atualizarMovimentacao(
                        movimentacaoEdicao.getIdMovimentacao(),
                        veiculoSelecionado.getIdVeiculo(),
                        tipoSelecionado.getIdTipoDespesa(),
                        descricao,
                        data,
                        valorStr,
                        tipoSelecionado.getDescricao(),
                        distanciaKm,
                        litrosCombustivel
                );
                JOptionPane.showMessageDialog(this,
                        "✅ Movimentação atualizada com sucesso!\n\n" +
                                "Veículo: " + veiculoSelecionado.getPlaca() + "\n" +
                                "Tipo: " + tipoSelecionado.getDescricao() + "\n" +
                                "Valor: R$ " + valorStr,
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                movimentacaoController.salvarMovimentacao(
                        veiculoSelecionado.getIdVeiculo(),
                        tipoSelecionado.getIdTipoDespesa(),
                        descricao,
                        data,
                        valorStr,
                        tipoSelecionado.getDescricao(),
                        distanciaKm,
                        litrosCombustivel
                );
                JOptionPane.showMessageDialog(this,
                        "✅ Movimentação cadastrada com sucesso!\n\n" +
                                "Veículo: " + veiculoSelecionado.getPlaca() + "\n" +
                                "Tipo: " + tipoSelecionado.getDescricao() + "\n" +
                                "Valor: R$ " + valorStr + "\n" +
                                "Data: " + data,
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
                    "❌ Erro ao salvar movimentação:\n" + e.getMessage() +
                            "\n\nVerifique a conexão com o banco de dados.",
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

    private boolean isTipoCombustivel(TipoDespesa tipo) {
        if (tipo == null || tipo.getDescricao() == null) {
            return false;
        }
        return tipo.getDescricao().toLowerCase(Locale.ROOT).contains("combust");
    }

    public void setCallback(Runnable callback) {
        this.onSuccess = callback;
    }
}