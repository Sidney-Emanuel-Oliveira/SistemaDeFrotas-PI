package br.com.view;

import br.com.controller.RelatoriosController;
import br.com.controller.VeiculoController;
import br.com.model.Movimentacao;
import br.com.model.Veiculo;
import br.com.model.VeiculoComboItem;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.ui.ModernInnerTabbedPane;
import br.com.ui.ModernComboBox;
import br.com.ui.WrapLayout;
import br.com.utils.GeradorCSV;
import br.com.utils.IconLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Tela de relatórios e análises
 * Exibe despesas, consumo, IPVA, multas e matrizes de análise com opção de exportar para CSV
 */
public class TelaRelatorios extends JPanel {
    // Controller para obter dados de relatórios
    private RelatoriosController relatoriosController;
    // Controller para obter dados de veículos
    private VeiculoController veiculoController;
    // Combos para seleção de período e veículo
    private JComboBox<Integer> cmbAno;
    private JComboBox<Integer> cmbMes;
    private JComboBox<Integer> cmbAnoFinal;
    private JComboBox<Integer> cmbMesFinal;
    private JComboBox<VeiculoComboItem> cmbVeiculo;
    // Áreas para exibição de relatórios (texto e tabela)
    private JTextArea areaRelatorio;
    private JTable tabelaRelatorio;
    private DefaultTableModel modeloTabela;
    // Labels para títulos
    private JLabel lblTitulo;
    private JLabel lblTipoRelatorio;

    private boolean relatorioGerado = false;
    private String tipoRelatorioAtual = "";

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // Construtor que inicializa a tela de relatórios
    public TelaRelatorios() {
        relatoriosController = new RelatoriosController();
        veiculoController = new VeiculoController();

        setLayout(new BorderLayout());
        setBackground(ModernColors.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JPanel headerPanel = criarPainelCabecalho();
        add(headerPanel, BorderLayout.NORTH);

        JComponent mainPanel = criarPainelPrincipal();
        add(mainPanel, BorderLayout.CENTER);
    }

    // Cria painel de cabeçalho com título
    private JPanel criarPainelCabecalho() {
        RoundedPanel panel = new RoundedPanel(16, ModernColors.WHITE);
        panel.setLayout(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        panel.setPreferredSize(new Dimension(0, 88));

        JPanel tituloPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        tituloPanel.setOpaque(false);

        lblTitulo = new JLabel("Relatórios");
        lblTitulo.setFont(TITLE_FONT);
        lblTitulo.setForeground(ModernColors.NAVY);

        JLabel subtitulo = new JLabel("Gere consultas financeiras e análises exigidas pelo Projeto Integrador");
        subtitulo.setFont(SUBTITLE_FONT);
        subtitulo.setForeground(ModernColors.TEXT_GRAY);

        tituloPanel.add(lblTitulo);
        tituloPanel.add(subtitulo);

        panel.add(tituloPanel, BorderLayout.WEST);
        return panel;
    }

    private JComponent criarPainelPrincipal() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ModernColors.BG_PRIMARY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel filtrosPanel = criarPainelFiltros();
        filtrosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filtrosPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        contentPanel.add(filtrosPanel);
        contentPanel.add(Box.createVerticalStrut(14));

        JPanel relatoriosPanel = criarPainelRelatorios();
        relatoriosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        relatoriosPanel.setPreferredSize(new Dimension(0, 520));
        relatoriosPanel.setMinimumSize(new Dimension(0, 420));
        relatoriosPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 720));
        contentPanel.add(relatoriosPanel);
        contentPanel.add(Box.createVerticalStrut(14));

        JPanel botoesPanel = criarPainelBotoes();
        botoesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        botoesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        contentPanel.add(botoesPanel);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(ModernColors.BG_PRIMARY);
        scroll.getViewport().setBackground(ModernColors.BG_PRIMARY);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JPanel criarPainelFiltros() {
        RoundedPanel panel = new RoundedPanel(16, ModernColors.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        panel.setPreferredSize(new Dimension(0, 270));
        panel.setMinimumSize(new Dimension(0, 260));

        JLabel titleLabel = new JLabel("Filtros e Opções de Relatório");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ModernColors.NAVY);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JPanel botoesRelatorios = criarPainelBotoesRelatorios();
        botoesRelatorios.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(botoesRelatorios);
        contentPanel.add(Box.createVerticalStrut(8));

        JPanel seletores = criarPainelSeletores();
        seletores.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(seletores);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    
    private JPanel criarPainelBotoesRelatorios() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        JPanel panel1 = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 4));
        panel1.setOpaque(false);
        panel1.setPreferredSize(new Dimension(0, 72));
        panel1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        String[] opecos = {
                "Despesas por Veículo",
                "Despesas por Mês",
                "Combustível por Mês",
                "IPVA por Ano",
                "Veículos Inativos",
                "Multas por Ano",
                "Média por Categoria",
                "Consumo Médio",
                "Média IPVA",
                "Maior/Menor Consumo"
        };

        for (String opcao : opecos) {
            ModernButton btn = new ModernButton(opcao, ModernColors.PRIMARY_BLUE);
            btn.setPreferredSize(new Dimension(148, 30));

            btn.addActionListener(e -> {
                try {
                    atualizarDados();
                    switch (opcao) {
                        case "Despesas por Veículo" -> gerarRelatorioDespesasVeiculo();
                        case "Despesas por Mês" -> gerarRelatorioDespesasMes();
                        case "Combustível por Mês" -> gerarRelatorioCombustivelMes();
                        case "IPVA por Ano" -> gerarRelatorioIPVAAno();
                        case "Veículos Inativos" -> gerarRelatorioVeiculosInativos();
                        case "Multas por Ano" -> gerarRelatorioMultas();
                        case "Média por Categoria" -> gerarRelatorioMediaDespesasCategoria();
                        case "Consumo Médio" -> gerarRelatorioConsumoMedio();
                        case "Média IPVA" -> gerarRelatorioCustoMedioIPVA();
                        case "Maior/Menor Consumo" -> gerarRelatorioMaiorMenorConsumo();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel1.add(btn);
        }

        JPanel panel2 = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 4));
        panel2.setOpaque(false);
        panel2.setPreferredSize(new Dimension(0, 42));
        panel2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel lblMatrizes = new JLabel("Análise Matricial:");
        lblMatrizes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMatrizes.setForeground(ModernColors.TEAL);
        panel2.add(lblMatrizes);

        String[] opcoesMatrizes = {
                "Matriz A (Abastecimentos)",
                "Matriz B (Custo Médio)",
                "Matriz C (Gasto Total)",
                "Relatório Completo"
        };

        Color corMatriz = ModernColors.TEAL;

        for (String opcao : opcoesMatrizes) {
            ModernButton btn = new ModernButton(opcao, corMatriz);
            btn.setPreferredSize(new Dimension(166, 30));

            btn.addActionListener(e -> {
                try {
                    atualizarDados();
                    switch (opcao) {
                        case "Matriz A (Abastecimentos)" -> gerarRelatorioMatrizA();
                        case "Matriz B (Custo Médio)" -> gerarRelatorioMatrizB();
                        case "Matriz C (Gasto Total)" -> gerarRelatorioMatrizC();
                        case "Relatório Completo" -> gerarRelatorioMatrizCompleto();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel2.add(btn);
        }

        mainPanel.add(panel1);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(panel2);

        return mainPanel;
    }

    
    private JPanel criarPainelSeletores() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        JPanel panel1 = new JPanel(new WrapLayout(FlowLayout.LEFT, 14, 3));
        panel1.setOpaque(false);
        panel1.setPreferredSize(new Dimension(0, 42));
        panel1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel lblAno = new JLabel("Ano:");
        lblAno.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblAno.setForeground(ModernColors.DARK_GRAY);
        cmbAno = new ModernComboBox<>();
        int anoAtual = LocalDate.now().getYear();
        for (int i = anoAtual - 10; i <= anoAtual; i++) {
            cmbAno.addItem(i);
        }
        cmbAno.setSelectedItem(anoAtual);
        cmbAno.setPreferredSize(new Dimension(118, 32));
        panel1.add(lblAno);
        panel1.add(cmbAno);

        JLabel lblMes = new JLabel("Mês:");
        lblMes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMes.setForeground(ModernColors.DARK_GRAY);
        cmbMes = new ModernComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cmbMes.addItem(i);
        }
        cmbMes.setSelectedItem(LocalDate.now().getMonthValue());
        cmbMes.setPreferredSize(new Dimension(92, 32));
        panel1.add(lblMes);
        panel1.add(cmbMes);

        JLabel lblVeiculo = new JLabel("Veículo:");
        lblVeiculo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblVeiculo.setForeground(ModernColors.DARK_GRAY);
        cmbVeiculo = new ModernComboBox<>();
        carregarVeiculos();
        cmbVeiculo.setPreferredSize(new Dimension(260, 32));
        panel1.add(lblVeiculo);
        panel1.add(cmbVeiculo);

        JPanel panel2 = new JPanel(new WrapLayout(FlowLayout.LEFT, 14, 3));
        panel2.setOpaque(false);
        panel2.setPreferredSize(new Dimension(0, 42));
        panel2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel lblPeriodoMatriz = new JLabel("Período (Matrizes):");
        lblPeriodoMatriz.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPeriodoMatriz.setForeground(new Color(0, 150, 136));
        panel2.add(lblPeriodoMatriz);

        JLabel lblMesFinal = new JLabel("Mês:");
        lblMesFinal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMesFinal.setForeground(ModernColors.DARK_GRAY);
        cmbMesFinal = new ModernComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cmbMesFinal.addItem(i);
        }
        cmbMesFinal.setSelectedItem(LocalDate.now().getMonthValue());
        cmbMesFinal.setPreferredSize(new Dimension(88, 32));

        JLabel lblAnoFinal = new JLabel("Ano:");
        lblAnoFinal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblAnoFinal.setForeground(ModernColors.DARK_GRAY);
        cmbAnoFinal = new ModernComboBox<>();
        for (int i = anoAtual - 10; i <= anoAtual; i++) {
            cmbAnoFinal.addItem(i);
        }
        cmbAnoFinal.setSelectedItem(anoAtual);
        cmbAnoFinal.setPreferredSize(new Dimension(118, 32));

        panel2.add(lblMesFinal);
        panel2.add(cmbMesFinal);
        panel2.add(lblAnoFinal);
        panel2.add(cmbAnoFinal);

        mainPanel.add(panel1);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(panel2);

        return mainPanel;
    }

    
    private JPanel criarPainelRelatorios() {
        RoundedPanel panel = new RoundedPanel(16, ModernColors.WHITE);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        panel.setPreferredSize(new Dimension(0, 520));
        panel.setMinimumSize(new Dimension(0, 420));

        JPanel tituloRelatorioPanel = new JPanel(new BorderLayout());
        tituloRelatorioPanel.setOpaque(false);
        tituloRelatorioPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        lblTipoRelatorio = new JLabel("Selecione um tipo de relatório acima");
        lblTipoRelatorio.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTipoRelatorio.setForeground(ModernColors.PRIMARY_BLUE);
        lblTipoRelatorio.setHorizontalAlignment(SwingConstants.LEFT);

        ImageIcon tableIcon = IconLoader.loadTableIcon(20, 20);
        JLabel iconRelatorio = new JLabel(tableIcon);

        JPanel tituloComIcone = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tituloComIcone.setOpaque(false);
        tituloComIcone.add(iconRelatorio);
        tituloComIcone.add(lblTipoRelatorio);

        tituloRelatorioPanel.add(tituloComIcone, BorderLayout.WEST);

        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(ModernColors.BORDER_GRAY);
        separator.setBackground(ModernColors.BORDER_GRAY);
        tituloRelatorioPanel.add(separator, BorderLayout.SOUTH);

        panel.add(tituloRelatorioPanel, BorderLayout.NORTH);

        JTabbedPane abas = new ModernInnerTabbedPane();
        abas.setPreferredSize(new Dimension(0, 430));
        abas.setMinimumSize(new Dimension(0, 330));

        areaRelatorio = new JTextArea();
        areaRelatorio.setEditable(false);
        areaRelatorio.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaRelatorio.setBackground(ModernColors.WHITE);
        areaRelatorio.setForeground(ModernColors.DARK_GRAY);
        areaRelatorio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollTexto = new JScrollPane(areaRelatorio);
        scrollTexto.setPreferredSize(new Dimension(0, 390));
        scrollTexto.setBorder(BorderFactory.createLineBorder(ModernColors.BORDER_GRAY));
        abas.addTab("Texto", scrollTexto);

        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaRelatorio = new JTable(modeloTabela);
        tabelaRelatorio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelaRelatorio.setRowHeight(28);
        tabelaRelatorio.setBackground(ModernColors.WHITE);
        tabelaRelatorio.setGridColor(ModernColors.BORDER_GRAY);
        tabelaRelatorio.setSelectionBackground(ModernColors.LIGHT_BLUE);
        tabelaRelatorio.setSelectionForeground(ModernColors.DARK_GRAY);
        tabelaRelatorio.setFillsViewportHeight(true);

        JTableHeader header = tabelaRelatorio.getTableHeader();
        header.setBackground(ModernColors.isDarkTheme() ? ModernColors.BG_SECONDARY : new Color(15, 23, 42));
        header.setForeground(ModernColors.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollTabela = new JScrollPane(tabelaRelatorio);
        scrollTabela.setPreferredSize(new Dimension(0, 390));
        scrollTabela.setBorder(BorderFactory.createLineBorder(ModernColors.BORDER_GRAY));
        abas.addTab("Tabela", scrollTabela);

        panel.add(abas, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelBotoes() {
        RoundedPanel panel = new RoundedPanel(16, ModernColors.WHITE);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        panel.setPreferredSize(new Dimension(0, 56));

        ModernButton btnExportar = new ModernButton("Exportar CSV", ModernColors.PRIMARY_BLUE);
        btnExportar.setPreferredSize(new Dimension(132, 34));
        btnExportar.addActionListener(e -> exportarCSV());

        ModernButton btnLimpar = new ModernButton("Limpar", ModernColors.TEXT_GRAY);
        btnLimpar.setPreferredSize(new Dimension(104, 34));
        btnLimpar.addActionListener(e -> limparResultado());

        panel.add(btnExportar);
        panel.add(btnLimpar);

        return panel;
    }

    
    public void atualizarDados() {
        if (cmbVeiculo != null) {
            carregarVeiculosPreservandoSelecao(obterIdVeiculoSelecionado());
        }
    }

    private void carregarVeiculos() {
        carregarVeiculosPreservandoSelecao("");
    }

    private void carregarVeiculosPreservandoSelecao(String idSelecionado) {
        try {
            cmbVeiculo.removeAllItems();
            cmbVeiculo.addItem(new VeiculoComboItem());

            int indiceParaSelecionar = 0;
            List<Veiculo> veiculos = veiculoController.obterTodosVeiculos();
            for (Veiculo v : veiculos) {
                VeiculoComboItem item = new VeiculoComboItem(v);
                cmbVeiculo.addItem(item);

                if (idSelecionado != null && !idSelecionado.isBlank()
                        && String.valueOf(v.getIdVeiculo()).equals(idSelecionado)) {
                    indiceParaSelecionar = cmbVeiculo.getItemCount() - 1;
                }
            }

            if (cmbVeiculo.getItemCount() > 0) {
                cmbVeiculo.setSelectedIndex(indiceParaSelecionar);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar veículos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obterIdVeiculoSelecionado() {
        Object selecionado = cmbVeiculo.getSelectedItem();

        if (selecionado instanceof VeiculoComboItem item && !item.isTodos() && item.getVeiculo() != null) {
            return String.valueOf(item.getVeiculo().getIdVeiculo());
        }

        return "";
    }

    private void atualizarTituloRelatorio(String tipoRelatorio) {
        tipoRelatorioAtual = tipoRelatorio;
        lblTipoRelatorio.setText(tipoRelatorio);
        relatorioGerado = true;
    }

    private void gerarRelatorioDespesasVeiculo() throws IOException {
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();
        if (itemSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (itemSelecionado.isTodos()) {
            gerarRelatorioDespesasTodosVeiculos();
            return;
        }

        Veiculo veiculo = itemSelecionado.getVeiculo();
        List<Movimentacao> movimentacoes = relatoriosController.obterDespesasVeiculo(veiculo.getIdVeiculo());
        double total = relatoriosController.obterTotalDespesasVeiculo(veiculo.getIdVeiculo());

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE DESPESAS DO VEÍCULO\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        sb.append("Veículo: ").append(veiculo.getMarca()).append(" ").append(veiculo.getModelo()).append("\n");
        sb.append("Placa: ").append(veiculo.getPlaca()).append("\n");
        sb.append("Ano: ").append(veiculo.getFabricateYear()).append("\n");
        sb.append("Status: ").append(veiculo.getAtivo() ? "Ativo" : "Inativo").append("\n\n");

        sb.append("Data       │ Descrição            │ Tipo           │ Valor\n");
        sb.append("───────────┼──────────────────────┼────────────────┼─────────────\n");

        for (Movimentacao m : movimentacoes) {
            sb.append(String.format("%-10s │ %-20s │ %-14s │ R$ %8.2f\n",
                    m.getData(), m.getDescricao(), m.getTipo(), m.getValor()));
        }

        sb.append("───────────┴──────────────────────┴────────────────┴─────────────\n");
        sb.append(String.format("TOTAL: R$ %.2f\n", total));

        areaRelatorio.setText(sb.toString());
        preencherTabelaDespesasVeiculo(movimentacoes, total);

        relatorioGerado = true;
        tipoRelatorioAtual = "Despesas por Veículo";
    }

    private void gerarRelatorioDespesasTodosVeiculos() throws IOException {
        List<Veiculo> veiculos = veiculoController.obterTodosVeiculos();
        List<Movimentacao> todasMovimentacoes = relatoriosController.obterTodasMovimentacoes();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE DESPESAS - TODOS OS VEÍCULOS\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        double totalGeral = 0.0;

        modeloTabela.setColumnIdentifiers(new String[]{"Placa", "Veículo", "Data", "Descrição", "Tipo", "Valor"});
        modeloTabela.setRowCount(0);

        for (Veiculo veiculo : veiculos) {
            List<Movimentacao> movimentacoesVeiculo = todasMovimentacoes.stream()
                    .filter(m -> m.getIdVeiculo().equals(veiculo.getIdVeiculo()))
                    .toList();

            if (!movimentacoesVeiculo.isEmpty()) {
                double totalVeiculo = movimentacoesVeiculo.stream()
                        .mapToDouble(Movimentacao::getValor)
                        .sum();

                sb.append("┌─────────────────────────────────────────────────────────────────┐\n");
                sb.append(String.format("│ Veículo: %-20s │ Placa: %-10s          │\n",
                        veiculo.getMarca() + " " + veiculo.getModelo(), veiculo.getPlaca()));
                sb.append("└─────────────────────────────────────────────────────────────────┘\n");
                sb.append("Data       │ Descrição            │ Tipo           │ Valor\n");
                sb.append("───────────┼──────────────────────┼────────────────┼─────────────\n");

                for (Movimentacao m : movimentacoesVeiculo) {
                    sb.append(String.format("%-10s │ %-20s │ %-14s │ R$ %8.2f\n",
                            m.getData(), m.getDescricao(), m.getTipo(), m.getValor()));

                    modeloTabela.addRow(new Object[]{
                            veiculo.getPlaca(),
                            veiculo.getMarca() + " " + veiculo.getModelo(),
                            m.getData(),
                            m.getDescricao(),
                            m.getTipo(),
                            String.format("R$ %.2f", m.getValor())
                    });
                }

                sb.append("                                      SUBTOTAL │ R$ ").append(String.format("%8.2f", totalVeiculo)).append("\n\n");
                totalGeral += totalVeiculo;
            }
        }

        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append(String.format("TOTAL GERAL: R$ %.2f\n", totalGeral));
        sb.append("═══════════════════════════════════════════════════════════════════════\n");

        areaRelatorio.setText(sb.toString());
        modeloTabela.addRow(new Object[]{"", "", "", "", "TOTAL GERAL", String.format("R$ %.2f", totalGeral)});
        tabelaRelatorio.setModel(modeloTabela);

        atualizarTituloRelatorio("Despesas - Todos os Veículos");
    }

    private void preencherTabelaDespesasVeiculo(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Descrição", "Tipo", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            modeloTabela.addRow(new Object[]{
                    m.getData(),
                    m.getDescricao(),
                    m.getTipo(),
                    String.format("R$ %.2f", m.getValor())
            });
        }

        modeloTabela.addRow(new Object[]{"", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioDespesasMes() throws IOException {
        int mes = (int) cmbMes.getSelectedItem();
        int ano = (int) cmbAno.getSelectedItem();
        String mesAno = String.format("%02d/%d", mes, ano);
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();
        List<Movimentacao> movimentacoesMes = movimentacoes.stream()
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    String mesAnoMovimentacao = dataParts[1] + "/" + dataParts[2];
                    boolean mesCorreto = mesAnoMovimentacao.equals(mesAno);

                    if (itemSelecionado != null && !itemSelecionado.isTodos()) {
                        Veiculo veiculoSelecionado = itemSelecionado.getVeiculo();
                        return mesCorreto && m.getIdVeiculo().equals(veiculoSelecionado.getIdVeiculo());
                    }
                    return mesCorreto;
                })
                .toList();

        double total = movimentacoesMes.stream().mapToDouble(Movimentacao::getValor).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE DESPESAS - ").append(mesAno);
        if (itemSelecionado != null && !itemSelecionado.isTodos()) {
            Veiculo v = itemSelecionado.getVeiculo();
            sb.append(" - ").append(v.getPlaca());
        }
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");

        sb.append("Data       │ Veículo          │ Descrição        │ Tipo      │ Valor\n");
        sb.append("───────────┼──────────────────┼──────────────────┼───────────┼─────────────\n");

        for (Movimentacao m : movimentacoesMes) {
            Veiculo veiculo = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
            String nomeVeiculo = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";

            String veiculoFormatado = nomeVeiculo.length() > 16 ? nomeVeiculo.substring(0, 13) + "..." : nomeVeiculo;
            String descricaoFormatada = m.getDescricao().length() > 16 ? m.getDescricao().substring(0, 13) + "..." : m.getDescricao();
            String tipoFormatado = m.getTipo().length() > 9 ? m.getTipo().substring(0, 6) + "..." : m.getTipo();

            sb.append(String.format("%-10s │ %-16s │ %-16s │ %-9s │ R$ %8.2f\n",
                    m.getData(), veiculoFormatado, descricaoFormatada, tipoFormatado, m.getValor()));
        }

        sb.append("───────────┴──────────────────┴──────────────────┴───────────┴─────────────\n");
        sb.append(String.format("TOTAL DO MÊS: R$ %.2f\n", total));

        areaRelatorio.setText(sb.toString());
        preencherTabelaDespesasMes(movimentacoesMes, total);

        String mesNome = java.time.Month.of(mes).getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("pt", "BR"));
        atualizarTituloRelatorio("Despesas por Mês - " + mesNome + "/" + ano);
    }

    private void preencherTabelaDespesasMes(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Veículo", "Descrição", "Tipo", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            try {
                Veiculo v = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
                String nomeVeiculo = v != null ? v.getMarca() + " " + v.getModelo() : "N/A";
                modeloTabela.addRow(new Object[]{
                        m.getData(),
                        nomeVeiculo,
                        m.getDescricao(),
                        m.getTipo(),
                        String.format("R$ %.2f", m.getValor())
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        modeloTabela.addRow(new Object[]{"", "", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioCombustivelMes() throws IOException {
        int mes = (int) cmbMes.getSelectedItem();
        int ano = (int) cmbAno.getSelectedItem();
        String mesAno = String.format("%02d/%d", mes, ano);
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();
        List<Movimentacao> combustivelisMes = movimentacoes.stream()
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    String mesAnoMovimentacao = dataParts[1] + "/" + dataParts[2];

                    boolean mesCorreto = mesAnoMovimentacao.equals(mesAno) && m.getIdTipoDespesa().equals(1L);

                    if (itemSelecionado != null && !itemSelecionado.isTodos()) {
                        Veiculo veiculoSelecionado = itemSelecionado.getVeiculo();
                        return mesCorreto && m.getIdVeiculo().equals(veiculoSelecionado.getIdVeiculo());
                    }
                    return mesCorreto;
                })
                .toList();

        double total = combustivelisMes.stream().mapToDouble(Movimentacao::getValor).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE COMBUSTÍVEL - ").append(mesAno);
        if (itemSelecionado != null && !itemSelecionado.isTodos()) {
            Veiculo v = itemSelecionado.getVeiculo();
            sb.append(" - ").append(v.getPlaca());
        }
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");

        sb.append("Data       │ Veículo              │ Descrição            │ Valor\n");
        sb.append("───────────┼──────────────────────┼──────────────────────┼─────────────\n");

        for (Movimentacao m : combustivelisMes) {
            Veiculo veiculo = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
            String nomeVeiculo = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";
            sb.append(String.format("%-10s │ %-20s │ %-20s │ R$ %8.2f\n",
                    m.getData(), nomeVeiculo, m.getDescricao(), m.getValor()));
        }

        sb.append("───────────┴──────────────────────┴──────────────────────┴─────────────\n");
        sb.append(String.format("TOTAL DE COMBUSTÍVEL: R$ %.2f\n", total));

        areaRelatorio.setText(sb.toString());
        preencherTabelaCombustivelMes(combustivelisMes, total);

        String mesNome = java.time.Month.of(mes).getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("pt", "BR"));
        atualizarTituloRelatorio("Combustível por Mês - " + mesNome + "/" + ano);
    }

    private void preencherTabelaCombustivelMes(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Veículo", "Descrição", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            try {
                Veiculo v = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
                String nomeVeiculo = v != null ? v.getMarca() + " " + v.getModelo() : "N/A";
                modeloTabela.addRow(new Object[]{
                        m.getData(),
                        nomeVeiculo,
                        m.getDescricao(),
                        String.format("R$ %.2f", m.getValor())
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        modeloTabela.addRow(new Object[]{"", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioIPVAAno() throws IOException {
        int anoSelecionado = (int) cmbAno.getSelectedItem();
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();
        List<Movimentacao> ipvasAno = movimentacoes.stream()
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    int anoMovimentacao = Integer.parseInt(dataParts[2]);

                    boolean anoCorreto = anoMovimentacao == anoSelecionado && m.getIdTipoDespesa().equals(2L);

                    if (itemSelecionado != null && !itemSelecionado.isTodos()) {
                        Veiculo veiculoSelecionado = itemSelecionado.getVeiculo();
                        return anoCorreto && m.getIdVeiculo().equals(veiculoSelecionado.getIdVeiculo());
                    }
                    return anoCorreto;
                })
                .toList();

        double total = ipvasAno.stream().mapToDouble(Movimentacao::getValor).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE IPVA - ").append(anoSelecionado);
        if (itemSelecionado != null && !itemSelecionado.isTodos()) {
            Veiculo v = itemSelecionado.getVeiculo();
            sb.append(" - ").append(v.getPlaca());
        }
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");

        sb.append("Data       │ Veículo              │ Descrição            │ Valor\n");
        sb.append("───────────┼──────────────────────┼──────────────────────┼─────────────\n");

        for (Movimentacao m : ipvasAno) {
            Veiculo veiculo = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
            String nomeVeiculo = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";
            sb.append(String.format("%-10s │ %-20s │ %-20s │ R$ %8.2f\n",
                    m.getData(), nomeVeiculo, m.getDescricao(), m.getValor()));
        }

        sb.append("───────────┴──────────────────────┴──────────────────────┴─────────────\n");
        sb.append(String.format("TOTAL DE IPVA: R$ %.2f\n", total));

        areaRelatorio.setText(sb.toString());
        preencherTabelaIPVAAno(ipvasAno, total);

        atualizarTituloRelatorio("IPVA por Ano - " + anoSelecionado);
    }

    private void preencherTabelaIPVAAno(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Veículo", "Descrição", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            try {
                Veiculo v = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
                String nomeVeiculo = v != null ? v.getMarca() + " " + v.getModelo() : "N/A";
                modeloTabela.addRow(new Object[]{
                        m.getData(),
                        nomeVeiculo,
                        m.getDescricao(),
                        String.format("R$ %.2f", m.getValor())
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        modeloTabela.addRow(new Object[]{"", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioVeiculosInativos() throws IOException {
        List<Veiculo> veiculosInativos = veiculoController.obterTodosVeiculos().stream()
                .filter(v -> !v.getAtivo())
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE VEÍCULOS INATIVOS\n");
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n\n");

        if (veiculosInativos.isEmpty()) {
            sb.append("Nenhum veículo inativo registrado.\n");
        } else {
            sb.append("Placa       │ Marca                │ Modelo               │ Ano   │ Tipo\n");
            sb.append("────────────┼──────────────────────┼──────────────────────┼───────┼──────────────\n");

            for (Veiculo v : veiculosInativos) {
                sb.append(String.format("%-11s │ %-20s │ %-20s │ %-5s │ %s\n",
                        v.getPlaca(), v.getMarca(), v.getModelo(), v.getFabricateYear(), v.getTipo()));
            }

            sb.append("────────────┴──────────────────────┴──────────────────────┴───────┴──────────────\n");
            sb.append(String.format("TOTAL: %d veículo(s) inativo(s)\n", veiculosInativos.size()));
        }

        areaRelatorio.setText(sb.toString());
        preencherTabelaVeiculosInativos(veiculosInativos);

        atualizarTituloRelatorio("Veículos Inativos");
    }

    private void preencherTabelaVeiculosInativos(List<Veiculo> veiculos) {
        modeloTabela.setColumnIdentifiers(new String[]{"Placa", "Marca", "Modelo", "Ano", "Tipo"});
        modeloTabela.setRowCount(0);

        for (Veiculo v : veiculos) {
            modeloTabela.addRow(new Object[]{
                    v.getPlaca(),
                    v.getMarca(),
                    v.getModelo(),
                    v.getFabricateYear(),
                    v.getTipo()
            });
        }

        modeloTabela.addRow(new Object[]{"", "", "", "", "TOTAL: " + veiculos.size() + " veículo(s)"});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioMultas() throws IOException {
        int anoSelecionado = (int) cmbAno.getSelectedItem();
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();
        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();
        List<Movimentacao> multas = movimentacoes.stream()
                .filter(relatoriosController::isMulta)
                .filter(m -> relatoriosController.obterAno(m).equals(String.valueOf(anoSelecionado)))
                .filter(m -> {
                    if (itemSelecionado != null && !itemSelecionado.isTodos()) {
                        Veiculo veiculoSelecionado = itemSelecionado.getVeiculo();
                        return m.getIdVeiculo().equals(veiculoSelecionado.getIdVeiculo());
                    }
                    return true;
                })
                .toList();

        double total = relatoriosController.calcularTotalRecursivo(multas, 0);

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE MULTAS PAGAS - ").append(anoSelecionado);
        if (itemSelecionado != null && !itemSelecionado.isTodos()) {
            Veiculo v = itemSelecionado.getVeiculo();
            sb.append(" - ").append(v.getPlaca());
        }
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n\n");

        if (multas.isEmpty()) {
            sb.append("Nenhuma multa encontrada para os filtros selecionados.\n");
        } else {
            sb.append("Data       │ Veículo              │ Descrição            │ Valor\n");
            sb.append("───────────┼──────────────────────┼──────────────────────┼─────────────\n");

            for (Movimentacao m : multas) {
                Veiculo veiculo = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
                String nomeVeiculo = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";
                sb.append(String.format("%-10s │ %-20s │ %-20s │ R$ %8.2f\n",
                        m.getData(), nomeVeiculo, m.getDescricao(), m.getValor()));
            }

            sb.append("───────────┴──────────────────────┴──────────────────────┴─────────────\n");
            sb.append(String.format("TOTAL DE MULTAS: R$ %.2f\n", total));
        }

        areaRelatorio.setText(sb.toString());
        preencherTabelaMultas(multas, total);
        atualizarTituloRelatorio("Multas por Ano - " + anoSelecionado);
    }

    private void preencherTabelaMultas(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Veículo", "Descrição", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            try {
                Veiculo v = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
                String nomeVeiculo = v != null ? v.getMarca() + " " + v.getModelo() : "N/A";
                modeloTabela.addRow(new Object[]{
                        m.getData(),
                        nomeVeiculo,
                        m.getDescricao(),
                        String.format("R$ %.2f", m.getValor())
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        modeloTabela.addRow(new Object[]{"", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioMediaDespesasCategoria() throws IOException {
        Map<String, Double> medias = relatoriosController.obterMediaDespesasPorCategoriaVeiculo();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("  MÉDIA DAS DESPESAS POR CATEGORIA DE VEÍCULO\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        modeloTabela.setColumnIdentifiers(new String[]{"Categoria", "Média de Despesas"});
        modeloTabela.setRowCount(0);

        if (medias.isEmpty()) {
            sb.append("Nenhuma despesa encontrada para calcular média por categoria.\n");
        } else {
            sb.append("Categoria            │ Média\n");
            sb.append("─────────────────────┼─────────────\n");
            for (Map.Entry<String, Double> entry : medias.entrySet()) {
                sb.append(String.format("%-20s │ R$ %8.2f\n", entry.getKey(), entry.getValue()));
                modeloTabela.addRow(new Object[]{entry.getKey(), String.format("R$ %.2f", entry.getValue())});
            }
        }

        areaRelatorio.setText(sb.toString());
        tabelaRelatorio.setModel(modeloTabela);
        atualizarTituloRelatorio("Média de Despesas por Categoria");
    }

    private void gerarRelatorioConsumoMedio() throws IOException {
        int mes = (int) cmbMes.getSelectedItem();
        int ano = (int) cmbAno.getSelectedItem();
        String mesAno = String.format("%02d/%d", mes, ano);
        Map<Long, RelatoriosController.ConsumoVeiculo> consumos = relatoriosController.obterConsumoMedioPorVeiculo(mesAno);

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n");
        sb.append("  CONSUMO MÉDIO POR VEÍCULO - ").append(mesAno).append("\n");
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n\n");

        modeloTabela.setColumnIdentifiers(new String[]{"Placa", "Veículo", "Km", "Litros", "Km/L", "Custo/Km", "Registros"});
        modeloTabela.setRowCount(0);

        if (consumos.isEmpty()) {
            sb.append("Nenhum dado de consumo encontrado para o período.\n");
            sb.append("Para este relatório funcionar, cadastre movimentações de combustível informando distância percorrida e litros abastecidos.\n");
        } else {
            sb.append("Placa       │ Veículo              │ Km       │ Litros   │ Km/L    │ Custo/Km\n");
            sb.append("────────────┼──────────────────────┼──────────┼──────────┼─────────┼────────────\n");
            for (RelatoriosController.ConsumoVeiculo consumo : consumos.values()) {
                Veiculo veiculo = veiculoController.obterVeiculoPorId(consumo.getIdVeiculo());
                String placa = veiculo != null ? veiculo.getPlaca() : "N/A";
                String nome = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";
                sb.append(String.format("%-11s │ %-20s │ %8.2f │ %8.2f │ %7.2f │ R$ %7.2f\n",
                        placa, nome, consumo.getDistanciaKm(), consumo.getLitros(), consumo.getKmPorLitro(), consumo.getCustoPorKm()));
                modeloTabela.addRow(new Object[]{
                        placa,
                        nome,
                        String.format("%.2f", consumo.getDistanciaKm()),
                        String.format("%.2f", consumo.getLitros()),
                        String.format("%.2f", consumo.getKmPorLitro()),
                        String.format("R$ %.2f", consumo.getCustoPorKm()),
                        consumo.getQuantidadeRegistros()
                });
            }
        }

        areaRelatorio.setText(sb.toString());
        tabelaRelatorio.setModel(modeloTabela);
        atualizarTituloRelatorio("Consumo Médio por Veículo - " + mesAno);
    }

    private void gerarRelatorioCustoMedioIPVA() throws IOException {
        int anoSelecionado = (int) cmbAno.getSelectedItem();
        String ano = String.valueOf(anoSelecionado);
        List<Movimentacao> ipvasAno = relatoriosController.obterTodasMovimentacoes().stream()
                .filter(relatoriosController::isIPVA)
                .filter(m -> relatoriosController.obterAno(m).equals(ano))
                .toList();
        double total = relatoriosController.calcularTotalRecursivo(ipvasAno, 0);
        double media = relatoriosController.obterCustoMedioIPVAAno(ano);

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("  CUSTO MÉDIO DO IPVA - ").append(ano).append("\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");
        sb.append(String.format("Quantidade de registros: %d\n", ipvasAno.size()));
        sb.append(String.format("Total de IPVA da frota: R$ %.2f\n", total));
        sb.append(String.format("Custo médio de IPVA: R$ %.2f\n", media));

        modeloTabela.setColumnIdentifiers(new String[]{"Indicador", "Valor"});
        modeloTabela.setRowCount(0);
        modeloTabela.addRow(new Object[]{"Quantidade de registros", ipvasAno.size()});
        modeloTabela.addRow(new Object[]{"Total de IPVA", String.format("R$ %.2f", total)});
        modeloTabela.addRow(new Object[]{"Custo médio do IPVA", String.format("R$ %.2f", media)});

        areaRelatorio.setText(sb.toString());
        tabelaRelatorio.setModel(modeloTabela);
        atualizarTituloRelatorio("Custo Médio do IPVA - " + ano);
    }

    private void gerarRelatorioMaiorMenorConsumo() throws IOException {
        int mes = (int) cmbMes.getSelectedItem();
        int ano = (int) cmbAno.getSelectedItem();
        String mesAno = String.format("%02d/%d", mes, ano);
        Map<String, RelatoriosController.ConsumoVeiculo> extremos = relatoriosController.identificarMaiorMenorCustoConsumo(mesAno);

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n");
        sb.append("  VEÍCULO COM MAIOR E MENOR CUSTO DE CONSUMO - ").append(mesAno).append("\n");
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n\n");

        modeloTabela.setColumnIdentifiers(new String[]{"Resultado", "Placa", "Veículo", "Km/L", "Custo/Km", "Custo Combustível"});
        modeloTabela.setRowCount(0);

        if (extremos.isEmpty()) {
            sb.append("Não há dados suficientes para identificar maior/menor custo.\n");
            sb.append("Cadastre despesas de combustível com distância percorrida e litros abastecidos.\n");
        } else {
            sb.append("Resultado               │ Placa       │ Veículo              │ Km/L    │ Custo/Km │ Custo\n");
            sb.append("────────────────────────┼─────────────┼──────────────────────┼─────────┼──────────┼────────────\n");
            for (Map.Entry<String, RelatoriosController.ConsumoVeiculo> entry : extremos.entrySet()) {
                RelatoriosController.ConsumoVeiculo consumo = entry.getValue();
                Veiculo veiculo = veiculoController.obterVeiculoPorId(consumo.getIdVeiculo());
                String placa = veiculo != null ? veiculo.getPlaca() : "N/A";
                String nome = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";
                sb.append(String.format("%-23s │ %-11s │ %-20s │ %7.2f │ R$ %6.2f │ R$ %8.2f\n",
                        entry.getKey(), placa, nome, consumo.getKmPorLitro(), consumo.getCustoPorKm(), consumo.getCustoCombustivel()));
                modeloTabela.addRow(new Object[]{
                        entry.getKey(),
                        placa,
                        nome,
                        String.format("%.2f", consumo.getKmPorLitro()),
                        String.format("R$ %.2f", consumo.getCustoPorKm()),
                        String.format("R$ %.2f", consumo.getCustoCombustivel())
                });
            }
        }

        areaRelatorio.setText(sb.toString());
        tabelaRelatorio.setModel(modeloTabela);
        atualizarTituloRelatorio("Maior/Menor Custo de Consumo - " + mesAno);
    }

    
    private void exportarCSV() {
        if (!relatorioGerado) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, gere um relatório antes de exportar!\n\n" +
                            "Clique em um dos botões:\n" +
                            "• Despesas por Veículo\n" +
                            "• Despesas por Mês\n" +
                            "• Combustível por Mês\n" +
                            "• IPVA por Ano\n" +
                            "• Veículos Inativos\n" +
                            "• Multas por Veículo",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

            String nomeArquivo = "relatorio_" + tipoRelatorioAtual.toLowerCase()
                    .replace(" ", "_")
                    .replace("á", "a")
                    .replace("ã", "a")
                    .replace("í", "i")
                    .replace("ú", "u")
                    + ".csv";

            GeradorCSV.gerarRelatorioDespesasCSV(movimentacoes, nomeArquivo);

            java.io.File arquivo = new java.io.File(nomeArquivo);
            String caminhoCompleto = arquivo.getAbsolutePath();

            JOptionPane.showMessageDialog(this,
                    "Relatório exportado com sucesso!\n\n" +
                            "Tipo: " + tipoRelatorioAtual + "\n" +
                            "Local: " + caminhoCompleto,
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao exportar: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparResultado() {
        areaRelatorio.setText("");
        modeloTabela.setRowCount(0);
        lblTitulo.setText("Relatórios");
        lblTipoRelatorio.setText("Selecione um tipo de relatório acima");

        relatorioGerado = false;
        tipoRelatorioAtual = "";
    }

    
    private void gerarRelatorioMatrizA() throws IOException {
        int mesInicial = (int) cmbMes.getSelectedItem();
        int anoInicial = (int) cmbAno.getSelectedItem();
        int mesFinal = (int) cmbMesFinal.getSelectedItem();
        int anoFinal = (int) cmbAnoFinal.getSelectedItem();

        if (anoInicial * 100 + mesInicial > anoFinal * 100 + mesFinal) {
            JOptionPane.showMessageDialog(this,
                    "O período inicial não pode ser maior que o período final!",
                    "Período Inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();
        Long idVeiculoFiltro = (itemSelecionado != null && itemSelecionado.getVeiculo() != null)
                ? itemSelecionado.getVeiculo().getIdVeiculo()
                : null;

        String relatorio = relatoriosController.gerarRelatorioMatrizAComPeriodo(
                mesInicial, anoInicial, mesFinal, anoFinal, idVeiculoFiltro);

        areaRelatorio.setText(relatorio);

        List<Veiculo> veiculos = relatoriosController.obterTodosVeiculos();

        if (itemSelecionado != null && itemSelecionado.getVeiculo() != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(itemSelecionado.getVeiculo().getIdVeiculo()))
                    .toList();
        }

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .toList();

        List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);

        preencherTabelaMatriz(matrizA, veiculos, meses, "Matriz A - Abastecimentos");

        String tituloVeiculo = (itemSelecionado != null && itemSelecionado.getVeiculo() != null)
                ? " - " + itemSelecionado.getVeiculo().getPlaca()
                : "";
        atualizarTituloRelatorio("Matriz A - Quantidade de Abastecimentos por Veículo/Mês" + tituloVeiculo);
    }

    
    private void gerarRelatorioMatrizB() throws IOException {
        int mesInicial = (int) cmbMes.getSelectedItem();
        int anoInicial = (int) cmbAno.getSelectedItem();
        int mesFinal = (int) cmbMesFinal.getSelectedItem();
        int anoFinal = (int) cmbAnoFinal.getSelectedItem();

        if (anoInicial * 100 + mesInicial > anoFinal * 100 + mesFinal) {
            JOptionPane.showMessageDialog(this,
                    "O período inicial não pode ser maior que o período final!",
                    "Período Inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();
        Long idVeiculoFiltro = (itemSelecionado != null && itemSelecionado.getVeiculo() != null)
                ? itemSelecionado.getVeiculo().getIdVeiculo()
                : null;

        String relatorio = relatoriosController.gerarRelatorioMatrizBComPeriodo(
                mesInicial, anoInicial, mesFinal, anoFinal, idVeiculoFiltro);

        areaRelatorio.setText(relatorio);

        List<Veiculo> veiculos = relatoriosController.obterTodosVeiculos();

        if (itemSelecionado != null && itemSelecionado.getVeiculo() != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(itemSelecionado.getVeiculo().getIdVeiculo()))
                    .toList();
        }

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .toList();

        List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);

        modeloTabela.setColumnIdentifiers(criarCabecalhoTabela(marcas));
        modeloTabela.setRowCount(0);

        for (int i = 0; i < matrizB.length; i++) {
            Object[] linha = new Object[marcas.size() + 1];
            linha[0] = meses.get(i);
            for (int j = 0; j < marcas.size(); j++) {
                linha[j + 1] = String.format("R$ %.2f", matrizB[i][j]);
            }
            modeloTabela.addRow(linha);
        }

        Object[] linhaTotal = new Object[marcas.size() + 1];
        linhaTotal[0] = "TOTAL GERAL";
        double total = br.com.utils.MatrizRelatorios.calcularTotalGeral(matrizB);
        linhaTotal[marcas.size()] = String.format("R$ %.2f", total);
        for (int j = 0; j < marcas.size() - 1; j++) {
            linhaTotal[j + 1] = "";
        }
        modeloTabela.addRow(linhaTotal);

        tabelaRelatorio.setModel(modeloTabela);

        String tituloVeiculo = (itemSelecionado != null && itemSelecionado.getVeiculo() != null)
                ? " - " + itemSelecionado.getVeiculo().getPlaca()
                : "";
        atualizarTituloRelatorio("Matriz B - Custo Médio por Abastecimento/Marca" + tituloVeiculo);
    }

    
    private void gerarRelatorioMatrizC() throws IOException {
        int mesInicial = (int) cmbMes.getSelectedItem();
        int anoInicial = (int) cmbAno.getSelectedItem();
        int mesFinal = (int) cmbMesFinal.getSelectedItem();
        int anoFinal = (int) cmbAnoFinal.getSelectedItem();

        if (anoInicial * 100 + mesInicial > anoFinal * 100 + mesFinal) {
            JOptionPane.showMessageDialog(this,
                    "O período inicial não pode ser maior que o período final!",
                    "Período Inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();
        Long idVeiculoFiltro = (itemSelecionado != null && itemSelecionado.getVeiculo() != null)
                ? itemSelecionado.getVeiculo().getIdVeiculo()
                : null;

        String relatorio = relatoriosController.gerarRelatorioMatrizCComPeriodo(
                mesInicial, anoInicial, mesFinal, anoFinal, idVeiculoFiltro);

        areaRelatorio.setText(relatorio);

        List<Veiculo> veiculos = relatoriosController.obterTodosVeiculos();

        if (itemSelecionado != null && itemSelecionado.getVeiculo() != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(itemSelecionado.getVeiculo().getIdVeiculo()))
                    .toList();
        }

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .toList();

        List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);
        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);

        modeloTabela.setColumnIdentifiers(criarCabecalhoTabela(marcas));
        modeloTabela.setRowCount(0);

        for (int i = 0; i < matrizC.length; i++) {
            Object[] linha = new Object[marcas.size() + 1];
            linha[0] = br.com.utils.MatrizRelatorios.formatarRotuloVeiculo(veiculos.get(i));
            for (int j = 0; j < marcas.size(); j++) {
                linha[j + 1] = String.format("R$ %.2f", matrizC[i][j]);
            }
            modeloTabela.addRow(linha);
        }

        Object[] linhaTotal = new Object[marcas.size() + 1];
        linhaTotal[0] = "TOTAL GERAL";
        double total = br.com.utils.MatrizRelatorios.calcularTotalGeral(matrizC);
        linhaTotal[marcas.size()] = String.format("R$ %.2f", total);
        for (int j = 0; j < marcas.size() - 1; j++) {
            linhaTotal[j + 1] = "";
        }
        modeloTabela.addRow(linhaTotal);

        tabelaRelatorio.setModel(modeloTabela);

        String tituloVeiculo = (itemSelecionado != null && itemSelecionado.getVeiculo() != null)
                ? " - " + itemSelecionado.getVeiculo().getPlaca()
                : "";
        atualizarTituloRelatorio("Matriz C - Gasto Total de Abastecimento por Marca" + tituloVeiculo);
    }

    private void gerarRelatorioMatrizCompleto() throws IOException {
        int mesInicial = (int) cmbMes.getSelectedItem();
        int anoInicial = (int) cmbAno.getSelectedItem();
        int mesFinal = (int) cmbMesFinal.getSelectedItem();
        int anoFinal = (int) cmbAnoFinal.getSelectedItem();

        if (anoInicial * 100 + mesInicial > anoFinal * 100 + mesFinal) {
            JOptionPane.showMessageDialog(this,
                    "O período inicial não pode ser maior que o período final!",
                    "Período Inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();
        Long idVeiculoFiltro = (itemSelecionado != null && itemSelecionado.getVeiculo() != null)
                ? itemSelecionado.getVeiculo().getIdVeiculo()
                : null;

        String relatorio = relatoriosController.gerarRelatorioMatrizCompletoComPeriodo(
                mesInicial, anoInicial, mesFinal, anoFinal, idVeiculoFiltro);

        areaRelatorio.setText(relatorio);

        List<Veiculo> veiculos = relatoriosController.obterTodosVeiculos();

        if (itemSelecionado != null && itemSelecionado.getVeiculo() != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(itemSelecionado.getVeiculo().getIdVeiculo()))
                    .toList();
        }

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .toList();

        List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);
        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);

        modeloTabela.setColumnIdentifiers(criarCabecalhoTabela(marcas));
        modeloTabela.setRowCount(0);

        for (int i = 0; i < matrizC.length; i++) {
            Object[] linha = new Object[marcas.size() + 1];
            linha[0] = br.com.utils.MatrizRelatorios.formatarRotuloVeiculo(veiculos.get(i));
            for (int j = 0; j < marcas.size(); j++) {
                linha[j + 1] = String.format("R$ %.2f", matrizC[i][j]);
            }
            modeloTabela.addRow(linha);
        }

        Object[] linhaTotal = new Object[marcas.size() + 1];
        linhaTotal[0] = "TOTAL GERAL";
        double total = br.com.utils.MatrizRelatorios.calcularTotalGeral(matrizC);
        linhaTotal[marcas.size()] = String.format("R$ %.2f", total);
        for (int j = 0; j < marcas.size() - 1; j++) {
            linhaTotal[j + 1] = "";
        }
        modeloTabela.addRow(linhaTotal);

        tabelaRelatorio.setModel(modeloTabela);

        String tituloVeiculo = (itemSelecionado != null && itemSelecionado.getVeiculo() != null)
                ? " - " + itemSelecionado.getVeiculo().getPlaca()
                : "";
        atualizarTituloRelatorio("Relatório Completo - Todas as Matrizes (A, B e C)" + tituloVeiculo);
    }

    private void preencherTabelaMatriz(double[][] matriz, List<Veiculo> veiculos, List<String> colunas, String titulo) {
        modeloTabela.setColumnIdentifiers(criarCabecalhoTabela(colunas));
        modeloTabela.setRowCount(0);

        for (int i = 0; i < matriz.length; i++) {
            Object[] linha = new Object[colunas.size() + 1];
            linha[0] = br.com.utils.MatrizRelatorios.formatarRotuloVeiculo(veiculos.get(i));

            for (int j = 0; j < colunas.size(); j++) {
                linha[j + 1] = String.format("%.0f", matriz[i][j]);
            }
            modeloTabela.addRow(linha);
        }

        Object[] linhaTotal = new Object[colunas.size() + 1];
        linhaTotal[0] = "TOTAL";
        double total = br.com.utils.MatrizRelatorios.calcularTotalGeral(matriz);
        linhaTotal[colunas.size()] = String.format("%.0f", total);
        for (int j = 0; j < colunas.size() - 1; j++) {
            linhaTotal[j + 1] = "";
        }
        modeloTabela.addRow(linhaTotal);

        tabelaRelatorio.setModel(modeloTabela);
    }

    private String[] criarCabecalhoTabela(List<String> colunas) {
        String[] cabecalho = new String[colunas.size() + 1];
        cabecalho[0] = "Veículo/Período";
        for (int i = 0; i < colunas.size(); i++) {
            cabecalho[i + 1] = colunas.get(i);
        }
        return cabecalho;
    }
}