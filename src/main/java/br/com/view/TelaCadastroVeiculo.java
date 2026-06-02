package br.com.view;

import br.com.controller.VeiculoController;
import br.com.model.Veiculo;
import br.com.model.TipoVeiculo;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.ui.ModernComboBox;
import br.com.ui.WrapLayout;
import br.com.ui.components.VehicleCard;
import br.com.ui.components.VehicleDetailsPanel;
import br.com.utils.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class TelaCadastroVeiculo extends JPanel {
    private VeiculoController controller;
    private JPanel mainPanel;
    private JScrollPane scrollCardsPanel;
    private JPanel detailsPanel;
    private JButton btnNovoVeiculo;
    private JTextField txtPesquisaPlaca;
    private JComboBox<String> cmbFiltroTipo;
    private JComboBox<String> cmbOrdenacao;
    private JPanel cardsPanel;

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public TelaCadastroVeiculo() {
        controller = new VeiculoController();
        setLayout(new BorderLayout());
        setBackground(ModernColors.BG_PRIMARY);

        mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBackground(ModernColors.BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JPanel headerPanel = criarPainelCabecalho();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        scrollCardsPanel = criarPainelCards();
        mainPanel.add(scrollCardsPanel, BorderLayout.CENTER);

        detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(ModernColors.BG_PRIMARY);
        detailsPanel.setVisible(false);

        CardLayout layout = new CardLayout();
        JPanel container = new JPanel(layout);
        container.setBackground(ModernColors.BG_PRIMARY);
        container.add(mainPanel, "lista");
        container.add(detailsPanel, "detalhes");

        add(container, BorderLayout.CENTER);
    }

    private JPanel criarPainelCabecalho() {
        RoundedPanel panel = new RoundedPanel(16, ModernColors.WHITE);
        panel.setLayout(new BorderLayout(18, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        panel.setPreferredSize(new Dimension(0, 112));

        JPanel tituloPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        tituloPanel.setOpaque(false);

        JLabel titulo = new JLabel("Veículos");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ModernColors.NAVY);

        JLabel subtitulo = new JLabel("Cadastre, filtre e organize a frota da GynLog");
        subtitulo.setFont(SUBTITLE_FONT);
        subtitulo.setForeground(ModernColors.TEXT_GRAY);

        tituloPanel.add(titulo);
        tituloPanel.add(subtitulo);

        JPanel filtrosPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        filtrosPanel.setOpaque(false);

        ImageIcon searchIcon = IconLoader.loadSearchIcon(20, 20);
        JLabel iconPesquisa = new JLabel(searchIcon);

        JLabel lblPesquisa = criarLabelFiltro("Pesquisar");
        txtPesquisaPlaca = new JTextField();
        txtPesquisaPlaca.setPreferredSize(new Dimension(210, 36));
        txtPesquisaPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPesquisaPlaca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtPesquisaPlaca.setToolTipText("Busca por placa, marca ou modelo");
        txtPesquisaPlaca.setBackground(ModernColors.WHITE);
        txtPesquisaPlaca.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                aplicarFiltrosVeiculos();
            }
        });
        txtPesquisaPlaca.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                txtPesquisaPlaca.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernColors.PRIMARY_BLUE, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                txtPesquisaPlaca.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        JLabel lblTipo = criarLabelFiltro("Tipo");
        cmbFiltroTipo = new ModernComboBox<>();
        cmbFiltroTipo.addItem("Todos");
        for (TipoVeiculo tipo : TipoVeiculo.values()) {
            cmbFiltroTipo.addItem(tipo.getDescricao());
        }
        cmbFiltroTipo.setPreferredSize(new Dimension(135, 36));
        cmbFiltroTipo.addActionListener(e -> aplicarFiltrosVeiculos());

        JLabel lblOrdenacao = criarLabelFiltro("Ordenar");
        cmbOrdenacao = new ModernComboBox<>(new DefaultComboBoxModel<>(new String[]{
                "ID / Ordem de cadastro",
                "Placa",
                "Marca",
                "Modelo",
                "Ano de fabricação",
                "Tipo de veículo"
        }));
        cmbOrdenacao.setPreferredSize(new Dimension(190, 36));
        cmbOrdenacao.addActionListener(e -> aplicarFiltrosVeiculos());

        filtrosPanel.add(iconPesquisa);
        filtrosPanel.add(lblPesquisa);
        filtrosPanel.add(txtPesquisaPlaca);
        filtrosPanel.add(lblTipo);
        filtrosPanel.add(cmbFiltroTipo);
        filtrosPanel.add(lblOrdenacao);
        filtrosPanel.add(cmbOrdenacao);

        btnNovoVeiculo = new ModernButton("+ Novo", ModernColors.PRIMARY_BLUE);
        btnNovoVeiculo.setPreferredSize(new Dimension(126, 46));
        btnNovoVeiculo.addActionListener(e -> abrirDialogNovoVeiculo());

        panel.add(tituloPanel, BorderLayout.WEST);
        panel.add(filtrosPanel, BorderLayout.CENTER);
        panel.add(btnNovoVeiculo, BorderLayout.EAST);

        return panel;
    }

    private JLabel criarLabelFiltro(String texto) {
        JLabel label = new JLabel(texto + ":");
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(ModernColors.DARK_GRAY);
        return label;
    }

    private JScrollPane criarPainelCards() {
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 18, 18));
        cardsPanel.setBackground(ModernColors.BG_PRIMARY);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 20, 2));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBackground(ModernColors.BG_PRIMARY);
        scrollPane.getViewport().setBackground(ModernColors.BG_PRIMARY);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        carregarCardsVeiculos(cardsPanel);

        return scrollPane;
    }

    private void carregarCardsVeiculos(JPanel cardsPanel) {
        cardsPanel.removeAll();
        try {
            List<Veiculo> veiculos = controller.obterVeiculosFiltradosOrdenados(
                    txtPesquisaPlaca != null ? txtPesquisaPlaca.getText() : "",
                    cmbFiltroTipo != null ? (String) cmbFiltroTipo.getSelectedItem() : "Todos",
                    cmbOrdenacao != null ? (String) cmbOrdenacao.getSelectedItem() : "ID / Ordem de cadastro");

            if (veiculos.isEmpty()) {
                RoundedPanel emptyPanel = new RoundedPanel(16, ModernColors.WHITE);
                emptyPanel.setLayout(new BorderLayout());
                emptyPanel.setPreferredSize(new Dimension(520, 130));
                emptyPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

                JLabel emptyLabel = new JLabel("Nenhum veículo encontrado para os filtros informados");
                emptyLabel.setFont(SUBTITLE_FONT);
                emptyLabel.setForeground(ModernColors.TEXT_GRAY);
                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                cardsPanel.add(emptyPanel);
            } else {
                for (Veiculo v : veiculos) {
                    VehicleCard card = new VehicleCard(v);
                    card.addListener(new VehicleCard.VehicleCardListener() {
                        @Override
                        public void onEditClicked(Veiculo veiculo) {
                            abrirDialogEditarVeiculo(veiculo);
                        }

                        @Override
                        public void onDeleteClicked(Veiculo veiculo) {
                            confirmarDelecao(veiculo);
                        }

                        @Override
                        public void onCardClicked(Veiculo veiculo) {
                            exibirDetalhesVeiculo(veiculo);
                        }
                    });
                    cardsPanel.add(card);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar veículos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void exibirDetalhesVeiculo(Veiculo veiculo) {
        detailsPanel.removeAll();

        VehicleDetailsPanel detailsView = new VehicleDetailsPanel(veiculo);
        detailsView.setOnBackCallback(() -> {
            Component comp = scrollCardsPanel.getViewport().getView();
            if (comp instanceof JPanel) {
                carregarCardsVeiculos((JPanel) comp);
            }
            detailsPanel.removeAll();
            detailsPanel.setVisible(false);
            mainPanel.setVisible(true);
            revalidate();
            repaint();
        });
        detailsView.setOnEditCallback(() -> abrirDialogEditarVeiculo(veiculo));

        detailsPanel.add(detailsView, BorderLayout.CENTER);
        detailsPanel.setVisible(true);
        mainPanel.setVisible(false);
        revalidate();
        repaint();
    }

    private void abrirDialogNovoVeiculo() {
        VehicleFormDialog dialog = new VehicleFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setCallback(() -> {
            Component comp = scrollCardsPanel.getViewport().getView();
            if (comp instanceof JPanel) {
                carregarCardsVeiculos((JPanel) comp);
            }
        });
        dialog.setVisible(true);
    }

    private void abrirDialogEditarVeiculo(Veiculo veiculo) {
        VehicleFormDialog dialog = new VehicleFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), veiculo);
        dialog.setCallback(() -> {
            if (detailsPanel.isVisible()) {
                detailsPanel.removeAll();
                detailsPanel.setVisible(false);
                mainPanel.setVisible(true);
            }
            Component comp = scrollCardsPanel.getViewport().getView();
            if (comp instanceof JPanel) {
                carregarCardsVeiculos((JPanel) comp);
            }
            revalidate();
            repaint();
        });
        dialog.setVisible(true);
    }

    private void confirmarDelecao(Veiculo veiculo) {
        int resultado = JOptionPane.showConfirmDialog(this,
                "Deseja deletar o veículo " + veiculo.getPlaca() + "?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resultado == JOptionPane.YES_OPTION) {
            try {
                controller.deletarVeiculo(veiculo.getIdVeiculo());
                JOptionPane.showMessageDialog(this, "Veículo deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                Component comp = scrollCardsPanel.getViewport().getView();
                if (comp instanceof JPanel) {
                    carregarCardsVeiculos((JPanel) comp);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aplicarFiltrosVeiculos() {
        if (cardsPanel == null) {
            return;
        }
        carregarCardsVeiculos(cardsPanel);
    }
}
