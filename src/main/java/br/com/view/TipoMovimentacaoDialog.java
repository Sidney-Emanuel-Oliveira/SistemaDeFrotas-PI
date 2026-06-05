package br.com.view;

import br.com.controller.TipoDespesaController;
import br.com.model.TipoDespesa;
import br.com.ui.ModernButton;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Dialog moderno para gerenciamento de Tipos de Movimentação.
 * Permite adicionar, editar e excluir tipos de despesas diretamente
 * a partir da tela de Movimentações.
 */
public class TipoMovimentacaoDialog extends JDialog {

    private static final Font TITLE_FONT   = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FIELD_FONT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font TABLE_FONT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font HEADER_FONT  = new Font("Segoe UI", Font.BOLD, 13);

    private final TipoDespesaController controller;
    private final Runnable onClose;

    private JTextField txtDescricao;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnSalvar;
    private JButton btnCancelarEdicao;
    private JLabel lblSecao;

    private Long idEmEdicao = null;

    // -------------------------------------------------------------------------
    public TipoMovimentacaoDialog(JFrame owner, Runnable onClose) {
        super(owner, "Gerenciar Tipos de Movimentação", true);
        this.controller = new TipoDespesaController();
        this.onClose    = onClose;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(560, 620);
        setLocationRelativeTo(owner);
        setResizable(false);

        construirUI();
        carregarTabela();
    }

    // -------------------------------------------------------------------------
    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ModernColors.BG_PRIMARY);
        root.add(criarCabecalho(), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new BorderLayout());
        corpo.setBackground(ModernColors.BG_PRIMARY);
        corpo.setBorder(new EmptyBorder(16, 20, 20, 20));
        corpo.add(criarPainelFormulario(), BorderLayout.NORTH);
        corpo.add(criarPainelTabela(),     BorderLayout.CENTER);
        root.add(corpo, BorderLayout.CENTER);

        add(root);
    }

    // --- Cabeçalho escuro igual ao MovementFormDialog ------------------------
    private JPanel criarCabecalho() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernColors.isDarkTheme()
                ? ModernColors.BG_SECONDARY
                : new Color(15, 23, 42));
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Ícone de categoria (tag simples em SVG-like via painted panel)
        JPanel iconBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(58, 58));
        iconBox.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel icone = new JLabel("🏷", JLabel.CENTER);
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconBox.add(icone, BorderLayout.CENTER);

        JLabel titulo    = new JLabel("Tipos de Movimentação");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Cadastre e gerencie categorias de despesas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(255, 255, 255, 200));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 3));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esquerda.setOpaque(false);
        esquerda.add(iconBox);
        esquerda.add(Box.createRigidArea(new Dimension(16, 0)));
        esquerda.add(textos);

        header.add(esquerda, BorderLayout.WEST);
        return header;
    }

    // --- Formulário de cadastro / edição ------------------------------------
    private JPanel criarPainelFormulario() {
        RoundedPanel card = new RoundedPanel(12, ModernColors.WHITE);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Título da seção (muda entre "Novo tipo" e "Editando")
        lblSecao = new JLabel("Novo Tipo de Movimentação");
        lblSecao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSecao.setForeground(ModernColors.PRIMARY_BLUE);
        card.add(lblSecao, BorderLayout.NORTH);

        // Campo
        JPanel campoPanel = new JPanel(new BorderLayout(0, 6));
        campoPanel.setOpaque(false);

        JLabel lbl = new JLabel("Descrição:");
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(ModernColors.DARK_GRAY);
        campoPanel.add(lbl, BorderLayout.NORTH);

        txtDescricao = criarCampoTexto();
        txtDescricao.setToolTipText("Ex: Combustível, Seguro, Manutenção...");
        campoPanel.add(txtDescricao, BorderLayout.CENTER);

        card.add(campoPanel, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelBotoes.setOpaque(false);

        btnCancelarEdicao = new ModernButton("Cancelar", ModernColors.TEXT_GRAY);
        btnCancelarEdicao.setPreferredSize(new Dimension(100, 38));
        btnCancelarEdicao.setVisible(false);
        btnCancelarEdicao.addActionListener(e -> cancelarEdicao());

        btnSalvar = new ModernButton("+ Adicionar", ModernColors.PRIMARY_BLUE);
        btnSalvar.setPreferredSize(new Dimension(120, 38));
        btnSalvar.addActionListener(e -> salvar());

        painelBotoes.add(btnCancelarEdicao);
        painelBotoes.add(btnSalvar);

        card.add(painelBotoes, BorderLayout.SOUTH);
        return card;
    }

    // --- Tabela de tipos cadastrados ----------------------------------------
    private JPanel criarPainelTabela() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(14, 0, 0, 0));

        JLabel lblLista = new JLabel("Tipos Cadastrados");
        lblLista.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLista.setForeground(ModernColors.NAVY);
        wrapper.add(lblLista, BorderLayout.NORTH);

        RoundedPanel tabelaCard = new RoundedPanel(12, ModernColors.WHITE);
        tabelaCard.setLayout(new BorderLayout());
        tabelaCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                new EmptyBorder(0, 0, 0, 0)
        ));

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Descrição"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(modeloTabela);
        tabela.setFont(TABLE_FONT);
        tabela.setRowHeight(36);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(ModernColors.BORDER_GRAY);
        tabela.setBackground(ModernColors.WHITE);
        tabela.setForeground(ModernColors.DARK_GRAY);
        tabela.setSelectionBackground(ModernColors.LIGHT_BLUE);
        tabela.setSelectionForeground(ModernColors.NAVY);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setFocusable(false);

        // Largura das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(0).setMaxWidth(70);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(300);

        // Centralizar coluna ID
        DefaultTableCellRenderer centralized = new DefaultTableCellRenderer();
        centralized.setHorizontalAlignment(JLabel.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centralized);

        // Header estilizado
        JTableHeader header = tabela.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(ModernColors.BG_SECONDARY);
        header.setForeground(ModernColors.TEXT_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernColors.BORDER_GRAY));
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ModernColors.WHITE);

        tabelaCard.add(scroll, BorderLayout.CENTER);
        tabelaCard.add(criarRodapeAcoes(), BorderLayout.SOUTH);

        wrapper.add(tabelaCard, BorderLayout.CENTER);
        return wrapper;
    }

    // --- Rodapé com botões de ação da tabela --------------------------------
    private JPanel criarRodapeAcoes() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernColors.BORDER_GRAY));

        JButton btnEditar = new ModernButton("✏ Editar", ModernColors.WARNING_ORANGE);
        btnEditar.setPreferredSize(new Dimension(100, 34));
        btnEditar.addActionListener(e -> preencherEdicao());

        JButton btnDeletar = new ModernButton("🗑 Excluir", ModernColors.DANGER_RED);
        btnDeletar.setPreferredSize(new Dimension(100, 34));
        btnDeletar.addActionListener(e -> excluirSelecionado());

        rodape.add(btnEditar);
        rodape.add(btnDeletar);
        return rodape;
    }

    // -------------------------------------------------------------------------
    // Lógica
    // -------------------------------------------------------------------------

    private void salvar() {
        String descricao = txtDescricao.getText().trim();
        if (descricao.isEmpty()) {
            mostrarErro("A descrição não pode estar vazia.");
            return;
        }
        try {
            if (idEmEdicao == null) {
                controller.salvarTipoDespesa(descricao);
                mostrarSucesso("Tipo \"" + descricao + "\" adicionado com sucesso!");
            } else {
                controller.atualizarTipoDespesa(idEmEdicao, descricao);
                mostrarSucesso("Tipo atualizado com sucesso!");
                cancelarEdicao();
            }
            txtDescricao.setText("");
            carregarTabela();
            if (onClose != null) onClose.run();
        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        } catch (IOException ex) {
            mostrarErro("Erro ao salvar: " + ex.getMessage());
        }
    }

    private void preencherEdicao() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            mostrarErro("Selecione um tipo na lista para editar.");
            return;
        }
        idEmEdicao = Long.parseLong(modeloTabela.getValueAt(linha, 0).toString());
        txtDescricao.setText(modeloTabela.getValueAt(linha, 1).toString());
        txtDescricao.requestFocus();

        lblSecao.setText("Editando Tipo de Movimentação");
        lblSecao.setForeground(ModernColors.WARNING_ORANGE);
        btnSalvar.setText("Salvar");
        btnCancelarEdicao.setVisible(true);
    }

    private void cancelarEdicao() {
        idEmEdicao = null;
        txtDescricao.setText("");
        lblSecao.setText("Novo Tipo de Movimentação");
        lblSecao.setForeground(ModernColors.PRIMARY_BLUE);
        btnSalvar.setText("+ Adicionar");
        btnCancelarEdicao.setVisible(false);
    }

    private void excluirSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            mostrarErro("Selecione um tipo na lista para excluir.");
            return;
        }
        Long id          = Long.parseLong(modeloTabela.getValueAt(linha, 0).toString());
        String descricao = modeloTabela.getValueAt(linha, 1).toString();

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o tipo \"" + descricao + "\"?\n" +
                        "Movimentações já cadastradas com este tipo não serão afetadas.",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                controller.deletarTipoDespesa(id);
                mostrarSucesso("Tipo excluído com sucesso!");
                cancelarEdicao();
                carregarTabela();
                if (onClose != null) onClose.run();
            } catch (IOException ex) {
                mostrarErro("Erro ao excluir: " + ex.getMessage());
            }
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            List<TipoDespesa> tipos = controller.obterTodosTipos();
            for (TipoDespesa t : tipos) {
                modeloTabela.addRow(new Object[]{t.getIdTipoDespesa(), t.getDescricao()});
            }
        } catch (IOException e) {
            mostrarErro("Erro ao carregar tipos: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers de UI
    // -------------------------------------------------------------------------

    private JTextField criarCampoTexto() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setBackground(ModernColors.WHITE);
        field.setForeground(ModernColors.DARK_GRAY);
        field.setCaretColor(ModernColors.DARK_GRAY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ModernColors.PRIMARY_BLUE, 2),
                        new EmptyBorder(10, 12, 10, 12)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ModernColors.FIELD_BORDER, 1),
                        new EmptyBorder(10, 12, 10, 12)));
            }
        });
        // Salvar com Enter
        field.addActionListener(e -> salvar());
        return field;
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarSucesso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}
