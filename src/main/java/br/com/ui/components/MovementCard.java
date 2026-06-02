package br.com.ui.components;

import br.com.model.Movimentacao;
import br.com.model.TipoDespesa;
import br.com.controller.TipoDespesaController;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.utils.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Componente visual customizado que renderiza os dados de uma movimentação em formato de card
public class MovementCard extends RoundedPanel {
    private Movimentacao movimentacao;
    private List<MovementCardListener> listeners = new ArrayList<>();
    private TipoDespesaController tipoDespesaController;
    private boolean isHovered = false;

    // Interface para comunicação de eventos do card (cliques, edição e exclusão) com o painel pai
    public interface MovementCardListener {
        void onEditClicked(Movimentacao mov);
        void onDeleteClicked(Movimentacao mov);
        void onCardClicked(Movimentacao mov);
    }

    public MovementCard(Movimentacao movimentacao) {
        super(12, ModernColors.WHITE);
        this.movimentacao = movimentacao;
        this.tipoDespesaController = new TipoDespesaController();

        // Configuração estrutural do card (Layout, espaçamentos internos e tamanho fixo)
        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setPreferredSize(new Dimension(900, 112));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        initializeComponents();

        // Gerencia os efeitos visuais e eventos disparados pelas interações do mouse
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                isHovered = true;
                setBackgroundColor(ModernColors.CARD_HOVER);
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHovered = false;
                setBackgroundColor(ModernColors.WHITE);
                repaint();
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Só dispara o clique do card se o usuário não tiver clicado no painel de botões
                if (!e.getComponent().equals(getButtonPanel())) {
                    notifyCardClicked();
                }
            }
        });
    }

    // Customização da borda arredondada do card utilizando renderização 2D e Antialiasing
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Destaca a borda em azul caso o mouse esteja passando por cima do card
        g2d.setColor(isHovered ? ModernColors.PRIMARY_BLUE : ModernColors.BORDER_GRAY);
        g2d.setStroke(new BasicStroke(isHovered ? 2f : 1f));
        g2d.drawRoundRect(2, 2, getWidth() - 9, getHeight() - 9, 14, 14);
        g2d.dispose();
    }

    // Organiza as três seções principais do card usando as regiões do BorderLayout
    private void initializeComponents() {
        // Ícone dinâmico no canto esquerdo
        JPanel iconPanel = criarPainelIcone();
        add(iconPanel, BorderLayout.WEST);

        // Informações textuais (veículo, tipo, data e valor) no centro
        JPanel infoPanel = criarPainelInformacoes();
        add(infoPanel, BorderLayout.CENTER);

        // Botões de ação rápida no canto direito
        JPanel buttonPanel = criarPainelBotoes();
        add(buttonPanel, BorderLayout.EAST);
    }

    // Constrói a seção do ícone, aplicando um fundo arredondado e carregando a imagem correspondente ao tipo
    private JPanel criarPainelIcone() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = getBackgroundColor();
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(88, 76));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String tipoName = getTipoDespesaInfo();
        ImageIcon icon = IconLoader.loadIconForExpenseType(tipoName, 54, 54);
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setVerticalAlignment(JLabel.CENTER);
        panel.add(iconLabel, BorderLayout.CENTER);

        return panel;
    }

    // Monta o painel central organizando as informações em 3 linhas verticais (GridLayout)
    private JPanel criarPainelInformacoes() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 6));
        panel.setOpaque(false);

        // Linha 1: Dados de identificação (Veículo e Tipo de Despesa)
        JPanel linha1 = new JPanel(new BorderLayout(10, 0));
        linha1.setOpaque(false);

        JLabel veiculoLabel = new JLabel(getVeiculoInfo());
        veiculoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        veiculoLabel.setForeground(ModernColors.NAVY);

        JLabel tipoLabel = new JLabel("• " + getTipoDespesaInfo());
        tipoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tipoLabel.setForeground(getColorForType());

        linha1.add(veiculoLabel, BorderLayout.WEST);
        linha1.add(tipoLabel, BorderLayout.CENTER);

        // Linha 2: Metadados da despesa (Data e Descrição complementar)
        JPanel linha2 = new JPanel(new BorderLayout(10, 0));
        linha2.setOpaque(false);

        JLabel dataLabel = new JLabel(movimentacao.getData());
        dataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dataLabel.setForeground(ModernColors.TEXT_GRAY);

        JLabel descLabel = new JLabel("• " + movimentacao.getDescricao());
        descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        descLabel.setForeground(new Color(140, 140, 140));

        linha2.add(dataLabel, BorderLayout.WEST);
        linha2.add(descLabel, BorderLayout.CENTER);

        // Linha 3: Valores financeiros e dados de consumo/desempenho (se houver)
        JPanel linha3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha3.setOpaque(false);

        JLabel valorLabel = new JLabel("R$ " + String.format("%.2f", movimentacao.getValor()));
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valorLabel.setForeground(ModernColors.PRIMARY_BLUE);
        linha3.add(valorLabel);

        // Renderiza o consumo médio de combustível caso a movimentação possua km e litros válidos
        if (movimentacao.possuiDadosConsumo()) {
            JLabel consumoLabel = new JLabel(String.format("  •  %.2f km/L", movimentacao.calcularConsumoMedioKmPorLitro()));
            consumoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            consumoLabel.setForeground(ModernColors.TEXT_GRAY);
            linha3.add(consumoLabel);
        }

        panel.add(linha1);
        panel.add(linha2);
        panel.add(linha3);

        return panel;
    }

    // Instancia os botões de ação e vincula as chamadas de notificação dos listeners
    private JPanel criarPainelBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 18));
        panel.setOpaque(false);

        ModernButton btnEditar = new ModernButton("Editar", ModernColors.PRIMARY_BLUE);
        btnEditar.setPreferredSize(new Dimension(80, 32));
        btnEditar.addActionListener(e -> notifyEditClicked());

        ModernButton btnExcluir = new ModernButton("Excluir", ModernColors.DANGER_RED);
        btnExcluir.setPreferredSize(new Dimension(80, 32));
        btnExcluir.addActionListener(e -> notifyDeleteClicked());

        panel.add(btnEditar);
        panel.add(btnExcluir);

        return panel;
    }

    // Varre os componentes filhos para localizar o painel de botões e isolar seu clique do resto do card
    private JPanel getButtonPanel() {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel p = (JPanel) comp;
                if (p.getComponentCount() > 0 && p.getComponent(0) instanceof ModernButton) {
                    return p;
                }
            }
        }
        return null;
    }

    // Define de forma dinâmica a cor de fundo do ícone baseando-se na categoria da despesa
    private Color getBackgroundColor() {
        try {
            TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacao.getIdTipoDespesa());
            if (tipo != null) {
                String desc = tipo.getDescricao().toLowerCase();
                if (desc.contains("combustível") || desc.contains("combustivel")) return new Color(255, 243, 224);
                if (desc.contains("seguro")) return new Color(232, 245, 233);
                if (desc.contains("lavagem")) return new Color(225, 245, 254);
                if (desc.contains("manutenção") || desc.contains("manutencao")) return new Color(243, 229, 245);
                if (desc.contains("ipva")) return new Color(255, 249, 196);
                if (desc.contains("multa")) return new Color(251, 235, 235);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ModernColors.LIGHT_BLUE;
    }

    // Garante que o texto de identificação não estoure o tamanho do layout aplicando reticências (...)
    private String getVeiculoInfo() {
        return movimentacao.getDescricao().length() > 30
                ? movimentacao.getDescricao().substring(0, 27) + "..."
                : movimentacao.getDescricao();
    }

    // Busca de forma assíncrona/via controller a descrição da categoria vinculada ao card
    private String getTipoDespesaInfo() {
        try {
            TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacao.getIdTipoDespesa());
            if (tipo != null) {
                return tipo.getDescricao();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Despesa";
    }

    // Retorna a cor temática para o texto da categoria de despesa (combinando com a cor do painel do ícone)
    private Color getColorForType() {
        try {
            TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacao.getIdTipoDespesa());
            if (tipo != null) {
                String desc = tipo.getDescricao().toLowerCase();
                if (desc.contains("combustível") || desc.contains("combustivel")) return new Color(255, 152, 0);
                if (desc.contains("seguro")) return new Color(76, 175, 80);
                if (desc.contains("lavagem")) return new Color(33, 150, 243);
                if (desc.contains("manutenção") || desc.contains("manutencao")) return new Color(156, 39, 176);
                if (desc.contains("ipva")) return new Color(255, 193, 7);
                if (desc.contains("multa")) return new Color(244, 67, 54);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ModernColors.PRIMARY_BLUE;
    }

    public void addListener(MovementCardListener listener) {
        listeners.add(listener);
    }

    // Métodos de notificação que repassam as ações internas do card para os listeners registrados
    private void notifyEditClicked() {
        for (MovementCardListener listener : listeners) {
            listener.onEditClicked(movimentacao);
        }
    }

    private void notifyDeleteClicked() {
        for (MovementCardListener listener : listeners) {
            listener.onDeleteClicked(movimentacao);
        }
    }

    private void notifyCardClicked() {
        for (MovementCardListener listener : listeners) {
            listener.onCardClicked(movimentacao);
        }
    }

    public Movimentacao getMovimentacao() {
        return movimentacao;
    }
}