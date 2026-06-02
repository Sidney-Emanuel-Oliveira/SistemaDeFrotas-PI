package br.com.ui.components;

import br.com.model.Veiculo;
import br.com.model.Movimentacao;
import br.com.model.TipoDespesa;
import br.com.controller.VeiculoController;
import br.com.controller.MovimentacaoController;
import br.com.controller.TipoDespesaController;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

// Painel de exibição detalhada que consolida os dados cadastrais do veículo e suas despesas recentes
public class VehicleDetailsPanel extends RoundedPanel {
    private Veiculo veiculo;
    private VeiculoController veiculoController;
    private MovimentacaoController movimentacaoController;
    private TipoDespesaController tipoDespesaController;
    private Runnable onBackCallback;
    private Runnable onEditCallback;

    public VehicleDetailsPanel(Veiculo veiculo) {
        super(12, ModernColors.WHITE);
        this.veiculo = veiculo;
        this.veiculoController = new VeiculoController();
        this.movimentacaoController = new MovimentacaoController();
        this.tipoDespesaController = new TipoDespesaController();

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initializeComponents();
    }

    private void initializeComponents() {
        // --- SEÇÃO SUPERIOR: Cabeçalho e Botões de Controle ---
        RoundedPanel headerPanel = new RoundedPanel(10, new Color(245, 247, 250));
        headerPanel.setLayout(new BorderLayout(20, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel leftHeaderPanel = new JPanel();
        leftHeaderPanel.setLayout(new BoxLayout(leftHeaderPanel, BoxLayout.Y_AXIS));
        leftHeaderPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Veículo " + veiculo.getPlaca() + " - " + veiculo.getMarca());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ModernColors.DARK_GRAY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(veiculo.getModelo() + " • Ano " + veiculo.getFabricateYear());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ModernColors.TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        leftHeaderPanel.add(titleLabel);
        leftHeaderPanel.add(subtitleLabel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        // Despacha a ação de retorno para o Runnable configurado pela tela pai
        ModernButton backBtn = new ModernButton("Voltar", ModernColors.TEXT_GRAY);
        backBtn.setPreferredSize(new Dimension(100, 38));
        backBtn.addActionListener(e -> {
            if (onBackCallback != null) onBackCallback.run();
        });

        // Despacha a ação de edição para o Runnable configurado pela tela pai
        ModernButton editBtn = new ModernButton("Editar", ModernColors.PRIMARY_BLUE);
        editBtn.setPreferredSize(new Dimension(100, 38));
        editBtn.addActionListener(e -> {
            if (onEditCallback != null) onEditCallback.run();
        });

        buttonsPanel.add(backBtn);
        buttonsPanel.add(editBtn);

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- SEÇÃO CENTRAL: Grid Splitter (Informações | Despesas) ---
        JPanel centralPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centralPanel.setOpaque(false);
        centralPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel infoPanel = criarPainelInformacoes();
        centralPanel.add(infoPanel);

        JPanel expensesPanel = criarPainelDespesas();
        centralPanel.add(expensesPanel);

        add(centralPanel, BorderLayout.CENTER);
    }

    // Constrói o bloco esquerdo contendo a ficha técnica com linhas estilizadas e selo de status
    private JPanel criarPainelInformacoes() {
        JPanel containerPanel = new JPanel(new BorderLayout(0, 15));
        containerPanel.setOpaque(false);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);

        JLabel sectionTitle = new JLabel("Informações");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionTitle.setForeground(ModernColors.DARK_GRAY);

        headerPanel.add(sectionTitle);
        containerPanel.add(headerPanel, BorderLayout.NORTH);

        RoundedPanel infoCard = new RoundedPanel(12, ModernColors.WHITE);
        infoCard.setLayout(new BorderLayout());
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.PRIMARY_BLUE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        contentPanel.add(criarLinhaInfoMelhorada("ID", String.valueOf(veiculo.getIdVeiculo())));
        contentPanel.add(criarSeparador());
        contentPanel.add(criarLinhaInfoMelhorada("Placa", veiculo.getPlaca()));
        contentPanel.add(criarSeparador());
        contentPanel.add(criarLinhaInfoMelhorada("Marca", veiculo.getMarca()));
        contentPanel.add(criarSeparador());
        contentPanel.add(criarLinhaInfoMelhorada("Modelo", veiculo.getModelo()));
        contentPanel.add(criarSeparador());
        contentPanel.add(criarLinhaInfoMelhorada("Ano", veiculo.getFabricateYear()));
        contentPanel.add(criarSeparador());
        contentPanel.add(criarLinhaInfoMelhorada("Tipo", veiculo.getTipo()));
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusLbl = new JLabel("Status:");
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLbl.setForeground(ModernColors.TEXT_GRAY);

        JLabel statusBadge = new JLabel(veiculo.getAtivo() ? " ● Ativo" : " ● Inativo");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusBadge.setForeground(veiculo.getAtivo() ? ModernColors.SUCCESS_GREEN : ModernColors.DANGER_RED);

        statusPanel.add(statusLbl);
        statusPanel.add(Box.createHorizontalStrut(10));
        statusPanel.add(statusBadge);

        contentPanel.add(statusPanel);
        infoCard.add(contentPanel, BorderLayout.NORTH);
        containerPanel.add(infoCard, BorderLayout.CENTER);

        return containerPanel;
    }

    private JPanel criarSeparador() {
        JPanel separador = new JPanel();
        separador.setOpaque(false);
        separador.setPreferredSize(new Dimension(0, 12));
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        return separador;
    }

    // Renderiza uma linha customizada adicionando uma pequena barra vertical azul à esquerda do rótulo
    private JPanel criarLinhaInfoMelhorada(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JPanel labelPanel = new JPanel(new BorderLayout(8, 0));
        labelPanel.setOpaque(false);
        labelPanel.setPreferredSize(new Dimension(90, 28));

        JPanel blueBar = new JPanel();
        blueBar.setBackground(ModernColors.PRIMARY_BLUE);
        blueBar.setPreferredSize(new Dimension(3, 20));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLabel.setForeground(ModernColors.PRIMARY_BLUE);

        labelPanel.add(blueBar, BorderLayout.WEST);
        labelPanel.add(lblLabel, BorderLayout.CENTER);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValue.setForeground(ModernColors.DARK_GRAY);

        panel.add(labelPanel, BorderLayout.WEST);
        panel.add(lblValue, BorderLayout.CENTER);

        return panel;
    }

    // Constrói o bloco direito contendo a tabela com as 10 movimentações mais recentes do veículo
    private JPanel criarPainelDespesas() {
        JPanel containerPanel = new JPanel(new BorderLayout(0, 15));
        containerPanel.setOpaque(false);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);

        JLabel sectionTitle = new JLabel("Despesas Recentes");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionTitle.setForeground(ModernColors.DARK_GRAY);
        headerPanel.add(sectionTitle);
        containerPanel.add(headerPanel, BorderLayout.NORTH);

        RoundedPanel tableCard = new RoundedPanel(12, ModernColors.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 237), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        try {
            // Filtra e classifica as movimentações da frota associadas a este veículo específico
            List<Movimentacao> movimentacoes = movimentacaoController.obterTodasMovimentacoes();
            movimentacoes = movimentacoes.stream()
                    .filter(m -> m.getIdVeiculo().equals(veiculo.getIdVeiculo()))
                    .sorted((a, b) -> b.getIdMovimentacao().compareTo(a.getIdMovimentacao())) // Ordem decrescente (ID)
                    .limit(10)
                    .toList();

            // Exibe tela vazia amigável caso não existam lançamentos históricos
            if (movimentacoes.isEmpty()) {
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setOpaque(false);
                emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

                JLabel emptyLabel = new JLabel("📋 Nenhuma despesa registrada");
                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                emptyLabel.setForeground(ModernColors.TEXT_GRAY);
                emptyLabel.setHorizontalAlignment(JLabel.CENTER);

                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                tableCard.add(emptyPanel, BorderLayout.CENTER);
            } else {
                // Instancia o modelo da tabela desativando a edição direta das células pelo usuário
                DefaultTableModel tableModel = new DefaultTableModel(
                        new String[]{"Data", "Tipo", "Descrição", "Valor"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                double totalDespesas = 0.0;
                for (Movimentacao m : movimentacoes) {
                    TipoDespesa tipoDespesa = tipoDespesaController.obterTipoPorId(m.getIdTipoDespesa());
                    String tipo = tipoDespesa != null ? tipoDespesa.getDescricao() : "N/A";
                    tableModel.addRow(new Object[]{
                            m.getData(),
                            tipo,
                            m.getDescricao(),
                            String.format("R$ %.2f", m.getValor())
                    });
                    totalDespesas += m.getValor();
                }

                JTable table = new JTable(tableModel);
                table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                table.setRowHeight(32);
                table.setBackground(ModernColors.WHITE);
                table.setGridColor(new Color(240, 242, 245));
                table.setShowVerticalLines(true);
                table.setShowHorizontalLines(true);
                table.setIntercellSpacing(new Dimension(1, 1));
                table.setSelectionBackground(new Color(232, 240, 254));
                table.setSelectionForeground(ModernColors.DARK_GRAY);

                // Estilização customizada do cabeçalho da tabela
                table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
                table.getTableHeader().setBackground(new Color(248, 249, 250));
                table.getTableHeader().setForeground(ModernColors.DARK_GRAY);
                table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 233, 237)));
                table.getTableHeader().setPreferredSize(new Dimension(0, 38));

                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                tableCard.add(scrollPane, BorderLayout.CENTER);

                // Adiciona o painel de rodapé consolidando o somatório total financeiro filtrado
                if (totalDespesas > 0) {
                    JPanel footerContainer = new JPanel(new BorderLayout());
                    footerContainer.setOpaque(false);
                    footerContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

                    RoundedPanel totalCard = new RoundedPanel(8, new Color(240, 248, 255));
                    totalCard.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ModernColors.PRIMARY_BLUE, 2),
                            BorderFactory.createEmptyBorder(12, 15, 12, 15)
                    ));
                    totalCard.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

                    JLabel totalLabel = new JLabel("Total das últimas despesas: ");
                    totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    totalLabel.setForeground(ModernColors.TEXT_GRAY);

                    JLabel totalValue = new JLabel(String.format("R$ %.2f", totalDespesas));
                    totalValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    totalValue.setForeground(ModernColors.PRIMARY_BLUE);

                    totalCard.add(totalLabel);
                    totalCard.add(totalValue);

                    footerContainer.add(totalCard, BorderLayout.CENTER);
                    tableCard.add(footerContainer, BorderLayout.SOUTH);
                }
            }
        } catch (IOException e) {
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.setOpaque(false);
            errorPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

            JLabel errorLabel = new JLabel("⚠️ Erro ao carregar despesas");
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(ModernColors.DANGER_RED);
            errorLabel.setHorizontalAlignment(JLabel.CENTER);

            errorPanel.add(errorLabel, BorderLayout.CENTER);
            tableCard.add(errorPanel, BorderLayout.CENTER);
        }

        containerPanel.add(tableCard, BorderLayout.CENTER);
        return containerPanel;
    }

    public void setOnBackCallback(Runnable callback) {
        this.onBackCallback = callback;
    }

    public void setOnEditCallback(Runnable callback) {
        this.onEditCallback = callback;
    }
}