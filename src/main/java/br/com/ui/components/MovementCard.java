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


public class MovementCard extends RoundedPanel {
    private Movimentacao movimentacao;
    private List<MovementCardListener> listeners = new ArrayList<>();
    private TipoDespesaController tipoDespesaController;
    private boolean isHovered = false;

    public interface MovementCardListener {
        void onEditClicked(Movimentacao mov);
        void onDeleteClicked(Movimentacao mov);
        void onCardClicked(Movimentacao mov);
    }

    public MovementCard(Movimentacao movimentacao) {
        super(12, ModernColors.WHITE);
        this.movimentacao = movimentacao;
        this.tipoDespesaController = new TipoDespesaController();

        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setPreferredSize(new Dimension(900, 112));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        initializeComponents();

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
                if (!e.getComponent().equals(getButtonPanel())) {
                    notifyCardClicked();
                }
            }
        });
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(isHovered ? ModernColors.PRIMARY_BLUE : ModernColors.BORDER_GRAY);
        g2d.setStroke(new BasicStroke(isHovered ? 2f : 1f));
        g2d.drawRoundRect(2, 2, getWidth() - 9, getHeight() - 9, 14, 14);
        g2d.dispose();
    }

    private void initializeComponents() {
        
        JPanel iconPanel = criarPainelIcone();
        add(iconPanel, BorderLayout.WEST);

        
        JPanel infoPanel = criarPainelInformacoes();
        add(infoPanel, BorderLayout.CENTER);

        
        JPanel buttonPanel = criarPainelBotoes();
        add(buttonPanel, BorderLayout.EAST);
    }

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


    private JPanel criarPainelInformacoes() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 6));
        panel.setOpaque(false);

        
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

        
        JPanel linha3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha3.setOpaque(false);

        JLabel valorLabel = new JLabel("R$ " + String.format("%.2f", movimentacao.getValor()));
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valorLabel.setForeground(ModernColors.PRIMARY_BLUE);

        linha3.add(valorLabel);

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

    private String getVeiculoInfo() {
        
        return movimentacao.getDescricao().length() > 30
            ? movimentacao.getDescricao().substring(0, 27) + "..."
            : movimentacao.getDescricao();
    }

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

