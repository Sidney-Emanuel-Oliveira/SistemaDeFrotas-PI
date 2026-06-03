package br.com.view;

import br.com.controller.MovimentacaoController;
import br.com.controller.VeiculoController;
import br.com.controller.TipoDespesaController;
import br.com.model.Movimentacao;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.ui.components.MovementCard;
import br.com.ui.components.MovementCard.MovementCardListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;


public class TelaMovimentacao extends JPanel {
    private MovimentacaoController movimentacaoController;
    private VeiculoController veiculoController;
    private TipoDespesaController tipoDespesaController;

    private JPanel mainPanel;
    private JPanel cardsPanel;
    private JButton btnNovaMovimentacao;

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public TelaMovimentacao() {
        movimentacaoController = new MovimentacaoController();
        veiculoController = new VeiculoController();
        tipoDespesaController = new TipoDespesaController();

        setLayout(new BorderLayout());
        setBackground(ModernColors.BG_PRIMARY);

        mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBackground(ModernColors.BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JPanel headerPanel = criarPainelCabecalho();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = criarPainelCards();
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel criarPainelCabecalho() {
        RoundedPanel panel = new RoundedPanel(16, ModernColors.WHITE);
        panel.setLayout(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        panel.setPreferredSize(new Dimension(0, 88));

        JPanel tituloPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        tituloPanel.setOpaque(false);

        JLabel titulo = new JLabel("Movimentações");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ModernColors.NAVY);

        JLabel subtitulo = new JLabel("Registre despesas, abastecimentos, multas, IPVA e manutenções");
        subtitulo.setFont(SUBTITLE_FONT);
        subtitulo.setForeground(ModernColors.TEXT_GRAY);

        tituloPanel.add(titulo);
        tituloPanel.add(subtitulo);

        btnNovaMovimentacao = new ModernButton("+ Nova", ModernColors.PRIMARY_BLUE);
        btnNovaMovimentacao.setPreferredSize(new Dimension(132, 46));
        btnNovaMovimentacao.addActionListener(e -> abrirDialogNovaMovimentacao());

        panel.add(tituloPanel, BorderLayout.WEST);
        panel.add(btnNovaMovimentacao, BorderLayout.EAST);

        return panel;
    }

    
    private JScrollPane criarPainelCards() {
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(ModernColors.BG_PRIMARY);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 18, 0));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBackground(ModernColors.BG_PRIMARY);
        scrollPane.getViewport().setBackground(ModernColors.BG_PRIMARY);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        carregarMovimentacoes();

        return scrollPane;
    }

    
    private void carregarMovimentacoes() {
        cardsPanel.removeAll();

        try {
            List<Movimentacao> movimentacoes = movimentacaoController.obterTodasMovimentacoes();

            
            if (movimentacoes.isEmpty()) {
                RoundedPanel emptyPanel = new RoundedPanel(16, ModernColors.WHITE);
                emptyPanel.setLayout(new BorderLayout());
                emptyPanel.setBorder(BorderFactory.createEmptyBorder(36, 40, 36, 40));
                emptyPanel.setPreferredSize(new Dimension(0, 150));
                emptyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

                JLabel emptyLabel = new JLabel("Nenhuma movimentação cadastrada");
                emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                emptyLabel.setForeground(ModernColors.TEXT_GRAY);
                emptyLabel.setHorizontalAlignment(JLabel.CENTER);
                emptyPanel.add(emptyLabel, BorderLayout.CENTER);

                cardsPanel.add(emptyPanel);
            } else {
                
                movimentacoes.sort((m1, m2) -> m2.getIdMovimentacao().compareTo(m1.getIdMovimentacao()));

                for (Movimentacao m : movimentacoes) {
                    MovementCard card = new MovementCard(m);
                    card.addListener(new MovementCardListener() {
                        @Override
                        public void onEditClicked(Movimentacao mov) {
                            abrirDialogEditarMovimentacao(mov);
                        }

                        @Override
                        public void onDeleteClicked(Movimentacao mov) {
                            confirmarDelecao(mov);
                        }

                        @Override
                        public void onCardClicked(Movimentacao mov) {
                            System.out.println("Card clicado: " + mov.getIdMovimentacao());
                        }
                    });

                    cardsPanel.add(card);
                    cardsPanel.add(Box.createVerticalStrut(10)); 
                }
            }

            
            cardsPanel.add(Box.createVerticalGlue());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar movimentações: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    
    public void atualizarDados() {
        carregarMovimentacoes();
    }

    private void abrirDialogNovaMovimentacao() {
        MovementFormDialog dialog = new MovementFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                null
        );
        dialog.setCallback(this::carregarMovimentacoes);
        dialog.setVisible(true);
    }

    private void abrirDialogEditarMovimentacao(Movimentacao mov) {
        MovementFormDialog dialog = new MovementFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                mov
        );
        dialog.setCallback(this::carregarMovimentacoes);
        dialog.setVisible(true);
    }

    private void confirmarDelecao(Movimentacao mov) {
        int resultado = JOptionPane.showConfirmDialog(this,
                "Deseja deletar esta movimentação?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resultado == JOptionPane.YES_OPTION) {
            try {
                movimentacaoController.deletarMovimentacao(mov.getIdMovimentacao());
                JOptionPane.showMessageDialog(this,
                        "Movimentação deletada com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                carregarMovimentacoes();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao deletar: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}