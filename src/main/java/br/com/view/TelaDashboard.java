package br.com.view;

import br.com.controller.MovimentacaoController;
import br.com.controller.TipoDespesaController;
import br.com.controller.VeiculoController;
import br.com.model.Movimentacao;
import br.com.model.TipoDespesa;
import br.com.model.Veiculo;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TelaDashboard extends JPanel {

    private final MovimentacaoController movController  = new MovimentacaoController();
    private final VeiculoController      veicController = new VeiculoController();
    private final TipoDespesaController  tipoController = new TipoDespesaController();

    private static final Font FONT_TITULO   = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBTIT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_CARD_VAL = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_CARD_LBL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_SECAO    = new Font("Segoe UI", Font.BOLD, 14);

    private JPanel painelCards;
    private JPanel painelGrafico;
    private JPanel painelRanking;
    private JLabel lblAtualizado;

    public TelaDashboard() {
        setLayout(new BorderLayout());
        setBackground(ModernColors.BG_PRIMARY);
        construirUI();
    }

    /**
     * Chamado pela TelaPrincipal sempre que esta aba for selecionada.
     * Recarrega todos os dados do zero para garantir informações atuais.
     */
    public void atualizarDados() {
        try {
            List<Movimentacao> todas = movController.obterTodasMovimentacoes();
            List<Veiculo>      veics = veicController.obterTodosVeiculos();
            List<TipoDespesa>  tipos = tipoController.obterTodosTipos();

            String mesAtual = mesAnoFormatado(LocalDate.now());
            List<Movimentacao> doMes = todas.stream()
                    .filter(m -> mesAnoDeData(m.getData()).equals(mesAtual))
                    .collect(Collectors.toList());

            preencherCards(veics, doMes);
            preencherGrafico(doMes, tipos);
            preencherRanking(doMes, veics);

            lblAtualizado.setText("Atualizado: " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dashboard: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        revalidate();
        repaint();
    }

    // -------------------------------------------------------------------------
    // Construção da estrutura da tela
    // -------------------------------------------------------------------------

    private void construirUI() {
        JPanel conteudo = new JPanel(new BorderLayout(0, 14));
        conteudo.setBackground(ModernColors.BG_PRIMARY);
        conteudo.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        conteudo.add(criarCabecalho(), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new BorderLayout(14, 14));
        corpo.setOpaque(false);

        painelCards = new JPanel(new GridLayout(1, 4, 12, 0));
        painelCards.setOpaque(false);
        corpo.add(painelCards, BorderLayout.NORTH);

        JPanel inferior = new JPanel(new GridLayout(1, 2, 14, 0));
        inferior.setOpaque(false);

        painelGrafico = new JPanel(new BorderLayout());
        painelGrafico.setOpaque(false);

        painelRanking = new JPanel(new BorderLayout());
        painelRanking.setOpaque(false);

        inferior.add(painelGrafico);
        inferior.add(painelRanking);
        corpo.add(inferior, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ModernColors.BG_PRIMARY);
        conteudo.add(scroll, BorderLayout.CENTER);

        add(conteudo, BorderLayout.CENTER);
    }

    private JPanel criarCabecalho() {
        RoundedPanel p = new RoundedPanel(16, ModernColors.WHITE);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        p.setPreferredSize(new Dimension(0, 72));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Dashboard");
        titulo.setFont(FONT_TITULO);
        titulo.setForeground(ModernColors.NAVY);

        JLabel sub = new JLabel("Resumo executivo da frota — mês atual");
        sub.setFont(FONT_SUBTIT);
        sub.setForeground(ModernColors.TEXT_GRAY);

        textos.add(titulo);
        textos.add(sub);

        lblAtualizado = new JLabel("—");
        lblAtualizado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAtualizado.setForeground(ModernColors.TEXT_GRAY);

        p.add(textos, BorderLayout.WEST);
        p.add(lblAtualizado, BorderLayout.EAST);
        return p;
    }

    // -------------------------------------------------------------------------
    // Cards de KPI
    // -------------------------------------------------------------------------

    private void preencherCards(List<Veiculo> veics, List<Movimentacao> doMes) {
        painelCards.removeAll();

        long ativos   = veics.stream().filter(Veiculo::getAtivo).count();
        long inativos = veics.size() - ativos;
        double totalMes  = doMes.stream().mapToDouble(Movimentacao::getValor).sum();
        double maiorDesp = doMes.stream().mapToDouble(Movimentacao::getValor).max().orElse(0);
        long tiposUsados = doMes.stream().map(Movimentacao::getIdTipoDespesa).distinct().count();

        painelCards.add(kpiCard("Veículos Ativos",
                String.valueOf(ativos),
                "(" + inativos + " inativos)",
                ModernColors.SUCCESS_GREEN));

        painelCards.add(kpiCard("Gasto no Mês",
                "R$ " + fmt(totalMes),
                doMes.size() + " movimentações",
                ModernColors.PRIMARY_BLUE));

        painelCards.add(kpiCard("Maior Despesa",
                "R$ " + fmt(maiorDesp),
                "no mês atual",
                ModernColors.WARNING_ORANGE));

        painelCards.add(kpiCard("Tipos Usados",
                String.valueOf(tiposUsados),
                "categorias no mês",
                ModernColors.TEAL));
    }

    private JPanel kpiCard(String titulo, String valor, String rodape, Color cor) {
        RoundedPanel card = new RoundedPanel(14, ModernColors.WHITE);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Barra colorida decorativa no topo do card
        JPanel barra = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                g2.fillRoundRect(0, 0, 44, 4, 4, 4);
                g2.dispose();
            }
        };
        barra.setOpaque(false);
        barra.setPreferredSize(new Dimension(0, 10));

        JLabel lTit = new JLabel(titulo);
        lTit.setFont(FONT_CARD_LBL);
        lTit.setForeground(ModernColors.TEXT_GRAY);

        JLabel lVal = new JLabel(valor);
        lVal.setFont(FONT_CARD_VAL);
        lVal.setForeground(cor);

        JLabel lRod = new JLabel(rodape);
        lRod.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lRod.setForeground(ModernColors.TEXT_GRAY);

        JPanel centro = new JPanel(new GridLayout(3, 1, 0, 2));
        centro.setOpaque(false);
        centro.add(lTit);
        centro.add(lVal);
        centro.add(lRod);

        card.add(barra,  BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);
        return card;
    }

    // -------------------------------------------------------------------------
    // Gráfico de barras por tipo de despesa
    // -------------------------------------------------------------------------

    private void preencherGrafico(List<Movimentacao> doMes, List<TipoDespesa> tipos) {
        painelGrafico.removeAll();

        RoundedPanel painel = new RoundedPanel(14, ModernColors.WHITE);
        painel.setLayout(new BorderLayout(0, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel sec = new JLabel("Gastos por Tipo — Mês Atual");
        sec.setFont(FONT_SECAO);
        sec.setForeground(ModernColors.NAVY);
        painel.add(sec, BorderLayout.NORTH);

        // Monta mapa id → nome do tipo
        Map<Long, String> nomes = new HashMap<>();
        for (TipoDespesa t : tipos) nomes.put(t.getIdTipoDespesa(), t.getDescricao());

        // Soma o valor por tipo de despesa
        Map<Long, Double> soma = new LinkedHashMap<>();
        for (Movimentacao m : doMes) soma.merge(m.getIdTipoDespesa(), m.getValor(), Double::sum);

        if (soma.isEmpty()) {
            painel.add(labelVazio("Nenhum dado no mês atual"), BorderLayout.CENTER);
        } else {
            double max = soma.values().stream().mapToDouble(d -> d).max().orElse(1);

            JPanel barras = new JPanel();
            barras.setLayout(new BoxLayout(barras, BoxLayout.Y_AXIS));
            barras.setOpaque(false);

            soma.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .limit(6)
                    .forEach(e -> {
                        String nome = nomes.getOrDefault(e.getKey(), "Tipo " + e.getKey());
                        barras.add(linhaGrafico(nome, e.getValue(), e.getValue() / max));
                        barras.add(Box.createRigidArea(new Dimension(0, 8)));
                    });

            JScrollPane sp = new JScrollPane(barras);
            sp.setBorder(BorderFactory.createEmptyBorder());
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);
            painel.add(sp, BorderLayout.CENTER);
        }

        painelGrafico.add(painel, BorderLayout.CENTER);
    }

    private JPanel linhaGrafico(String nome, double valor, double pct) {
        JPanel linha = new JPanel(new BorderLayout(8, 2));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lNome = new JLabel(nome);
        lNome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lNome.setForeground(ModernColors.DARK_GRAY);
        lNome.setPreferredSize(new Dimension(100, 20));

        // Barra proporcional desenhada via paintComponent
        JPanel barra = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ModernColors.BG_SECONDARY);
                g2.fillRoundRect(0, 3, getWidth(), 14, 7, 7);
                int w = (int) (getWidth() * pct);
                g2.setColor(ModernColors.PRIMARY_BLUE);
                g2.fillRoundRect(0, 3, Math.max(w, 6), 14, 7, 7);
                g2.dispose();
            }
        };
        barra.setOpaque(false);

        JLabel lVal = new JLabel("R$ " + fmt(valor));
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lVal.setForeground(ModernColors.TEXT_GRAY);
        lVal.setPreferredSize(new Dimension(90, 20));
        lVal.setHorizontalAlignment(JLabel.RIGHT);

        linha.add(lNome, BorderLayout.WEST);
        linha.add(barra, BorderLayout.CENTER);
        linha.add(lVal,  BorderLayout.EAST);
        return linha;
    }

    // -------------------------------------------------------------------------
    // Ranking de veículos com mais gasto
    // -------------------------------------------------------------------------

    private void preencherRanking(List<Movimentacao> doMes, List<Veiculo> veics) {
        painelRanking.removeAll();

        RoundedPanel painel = new RoundedPanel(14, ModernColors.WHITE);
        painel.setLayout(new BorderLayout(0, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel sec = new JLabel("Top Veículos — Mês Atual");
        sec.setFont(FONT_SECAO);
        sec.setForeground(ModernColors.NAVY);
        painel.add(sec, BorderLayout.NORTH);

        // Monta mapa id → nome do veículo
        Map<Long, String> nomesVeic = new HashMap<>();
        for (Veiculo v : veics)
            nomesVeic.put(v.getIdVeiculo(),
                    v.getPlaca() + " — " + v.getMarca() + " " + v.getModelo());

        // Soma o gasto por veículo
        Map<Long, Double> gasto = new LinkedHashMap<>();
        for (Movimentacao m : doMes) gasto.merge(m.getIdVeiculo(), m.getValor(), Double::sum);

        if (gasto.isEmpty()) {
            painel.add(labelVazio("Nenhum dado no mês atual"), BorderLayout.CENTER);
        } else {
            JPanel lista = new JPanel();
            lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
            lista.setOpaque(false);

            String[] medalhas = {"1.", "2.", "3."};
            int[] pos = {0};

            gasto.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .limit(5)
                    .forEach(e -> {
                        String med  = pos[0] < medalhas.length ? medalhas[pos[0]] : "  " + (pos[0] + 1) + ".";
                        String nome = nomesVeic.getOrDefault(e.getKey(), "Veículo " + e.getKey());
                        lista.add(linhaRanking(med, nome, "R$ " + fmt(e.getValue())));
                        lista.add(Box.createRigidArea(new Dimension(0, 6)));
                        pos[0]++;
                    });

            painel.add(lista, BorderLayout.CENTER);
        }

        painelRanking.add(painel, BorderLayout.CENTER);
    }

    private JPanel linhaRanking(String medal, String nome, String valor) {
        RoundedPanel linha = new RoundedPanel(8, ModernColors.BG_SECONDARY);
        linha.setLayout(new BorderLayout(10, 0));
        linha.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel lMed = new JLabel(medal);
        lMed.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lNom = new JLabel(nome);
        lNom.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lNom.setForeground(ModernColors.DARK_GRAY);

        JLabel lVal = new JLabel(valor);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lVal.setForeground(ModernColors.PRIMARY_BLUE);

        linha.add(lMed, BorderLayout.WEST);
        linha.add(lNom, BorderLayout.CENTER);
        linha.add(lVal, BorderLayout.EAST);
        return linha;
    }

    // -------------------------------------------------------------------------
    // Utilitários
    // -------------------------------------------------------------------------

    private JLabel labelVazio(String msg) {
        JLabel l = new JLabel(msg, JLabel.CENTER);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        l.setForeground(ModernColors.TEXT_GRAY);
        return l;
    }

    /** Formata LocalDate como MM/yyyy */
    private String mesAnoFormatado(LocalDate d) {
        return d.format(DateTimeFormatter.ofPattern("MM/yyyy"));
    }

    /** Extrai MM/yyyy de uma string dd/MM/yyyy */
    private String mesAnoDeData(String data) {
        if (data == null || data.length() < 7) return "";
        try {
            String[] p = data.split("/");
            return p[1] + "/" + p[2];
        } catch (Exception e) {
            return "";
        }
    }

    /** Formata double como "1.234,56" */
    private String fmt(double v) {
        return String.format("%.2f", v).replace(".", ",");
    }
}
