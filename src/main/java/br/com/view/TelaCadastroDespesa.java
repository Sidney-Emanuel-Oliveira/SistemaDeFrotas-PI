package br.com.view;

import br.com.controller.TipoDespesaController;
import br.com.model.TipoDespesa;
import br.com.ui.ModernColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;


public class TelaCadastroDespesa extends JPanel {

    private static final int DESCRICAO_MAX = 100;

    private TipoDespesaController controller;
    private JTextField txtDescricao;
    private JLabel lblContador;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnSalvar, btnLimpar, btnEditar, btnDeletar, btnAtualizar;

    /** ID do tipo selecionado para edição; null quando nenhum está em edição. */
    private Long idSelecionado = null;

    public TelaCadastroDespesa() {
        controller = new TipoDespesaController();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ModernColors.BG_PRIMARY);

        add(criarPainelEntrada(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        carregarTabela();
    }

    // -------------------------------------------------------------------------
    // Construção do painel de entrada
    // -------------------------------------------------------------------------

    private JPanel criarPainelEntrada() {
        // Campo de descrição + contador de caracteres
        JPanel linhaDescricao = new JPanel(new BorderLayout(5, 0));
        txtDescricao = new JTextField();
        txtDescricao.setToolTipText("Mínimo 2 e máximo " + DESCRICAO_MAX + " caracteres.");

        lblContador = new JLabel("0/" + DESCRICAO_MAX);
        lblContador.setForeground(Color.GRAY);
        lblContador.setFont(lblContador.getFont().deriveFont(11f));

        txtDescricao.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { atualizarContador(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { atualizarContador(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { atualizarContador(); }
        });

        linhaDescricao.add(txtDescricao, BorderLayout.CENTER);
        linhaDescricao.add(lblContador, BorderLayout.EAST);

        // Painel do formulário
        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Cadastro de Tipo de Despesa"));
        formulario.add(new JLabel("Descrição:"));
        formulario.add(linhaDescricao);

        // Botões
        btnSalvar   = new JButton("Salvar");
        btnAtualizar = new JButton("Atualizar");
        btnLimpar   = new JButton("Limpar");
        btnEditar   = new JButton("✏ Editar Selecionado");
        btnDeletar  = new JButton("🗑 Deletar Selecionado");

        btnAtualizar.setVisible(false);
        btnEditar.setEnabled(false);
        btnDeletar.setEnabled(false);

        btnSalvar.addActionListener(e -> salvarTipoDespesa());
        btnAtualizar.addActionListener(e -> atualizarTipoDespesa());
        btnLimpar.addActionListener(e -> limparCampos());
        btnEditar.addActionListener(e -> editarTipoSelecionado());
        btnDeletar.addActionListener(e -> deletarTipoSelecionado());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnDeletar);

        JPanel painelCompleto = new JPanel(new BorderLayout());
        painelCompleto.add(formulario, BorderLayout.CENTER);
        painelCompleto.add(painelBotoes, BorderLayout.SOUTH);
        return painelCompleto;
    }

    // -------------------------------------------------------------------------
    // Construção do painel de tabela
    // -------------------------------------------------------------------------

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Tipos de Despesas Cadastrados"));

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Descrição"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);

        // Habilita/desabilita botões conforme seleção na tabela
        tabela.getSelectionModel().addListSelectionListener(e -> {
            boolean selecionado = tabela.getSelectedRow() != -1;
            btnEditar.setEnabled(selecionado);
            btnDeletar.setEnabled(selecionado);
        });

        // Duplo-clique carrega o item para edição
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabela.getSelectedRow() != -1) {
                    editarTipoSelecionado();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabela);
        painel.add(scrollPane, BorderLayout.CENTER);

        JLabel dica = new JLabel("  Dica: duplo-clique em uma linha para editar rapidamente.");
        dica.setForeground(Color.GRAY);
        dica.setFont(dica.getFont().deriveFont(Font.ITALIC, 11f));
        painel.add(dica, BorderLayout.SOUTH);

        return painel;
    }

    // -------------------------------------------------------------------------
    // Ações dos botões
    // -------------------------------------------------------------------------

    private void salvarTipoDespesa() {
        String descricao = txtDescricao.getText();
        try {
            controller.salvarTipoDespesa(descricao);
            mostrarSucesso("Tipo de despesa salvo com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (IllegalArgumentException e) {
            marcarCampoInvalido();
            mostrarErro(e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro de I/O ao salvar: " + e.getMessage());
        }
    }

    private void atualizarTipoDespesa() {
        if (idSelecionado == null) {
            mostrarErro("Nenhum tipo selecionado para atualização!");
            return;
        }
        String descricao = txtDescricao.getText();
        try {
            controller.atualizarTipoDespesa(idSelecionado, descricao);
            mostrarSucesso("Tipo de despesa atualizado com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (IllegalArgumentException e) {
            marcarCampoInvalido();
            mostrarErro(e.getMessage());
        } catch (IOException e) {
            mostrarErro("Erro de I/O ao atualizar: " + e.getMessage());
        }
    }

    private void editarTipoSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            mostrarErro("Selecione um tipo para editar!");
            return;
        }
        idSelecionado = Long.parseLong(modeloTabela.getValueAt(linha, 0).toString());
        String descricao = modeloTabela.getValueAt(linha, 1).toString();
        txtDescricao.setText(descricao);
        txtDescricao.requestFocusInWindow();

        // Alterna visibilidade dos botões para modo de edição
        btnSalvar.setVisible(false);
        btnAtualizar.setVisible(true);
        restaurarCampoPadrao();
    }

    private void deletarTipoSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            mostrarErro("Selecione um tipo para deletar!");
            return;
        }

        Long id = Long.parseLong(modeloTabela.getValueAt(linha, 0).toString());
        String descricao = modeloTabela.getValueAt(linha, 1).toString();

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o tipo \"" + descricao + "\"?\nEsta ação não pode ser desfeita.",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                controller.deletarTipoDespesa(id);
                mostrarSucesso("Tipo de despesa excluído com sucesso!");
                limparCampos();
                carregarTabela();
            } catch (IllegalStateException e) {
                // Tipo possui movimentações vinculadas — alerta diferenciado
                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage() + "\n\nDica: reatribua ou exclua as movimentações antes de remover este tipo.",
                        "Exclusão bloqueada",
                        JOptionPane.WARNING_MESSAGE
                );
            } catch (IllegalArgumentException e) {
                mostrarErro(e.getMessage());
            } catch (IOException e) {
                mostrarErro("Erro de I/O ao excluir: " + e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------------
    // Utilitários de UI
    // -------------------------------------------------------------------------

    private void limparCampos() {
        txtDescricao.setText("");
        idSelecionado = null;
        btnSalvar.setVisible(true);
        btnAtualizar.setVisible(false);
        btnEditar.setEnabled(false);
        btnDeletar.setEnabled(false);
        tabela.clearSelection();
        restaurarCampoPadrao();
    }

    private void atualizarContador() {
        int tamanho = txtDescricao.getText().trim().length();
        lblContador.setText(tamanho + "/" + DESCRICAO_MAX);
        if (tamanho > DESCRICAO_MAX) {
            lblContador.setForeground(Color.RED);
        } else if (tamanho > DESCRICAO_MAX * 0.85) {
            lblContador.setForeground(Color.ORANGE);
        } else {
            lblContador.setForeground(Color.GRAY);
        }
        // Remove marcação de erro quando o usuário começa a digitar
        if (tamanho > 0) {
            restaurarCampoPadrao();
        }
    }

    /** Marca o campo com borda vermelha para indicar erro de validação. */
    private void marcarCampoInvalido() {
        txtDescricao.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        txtDescricao.requestFocusInWindow();
    }

    /** Restaura a borda padrão do campo. */
    private void restaurarCampoPadrao() {
        txtDescricao.setBorder(UIManager.getBorder("TextField.border"));
    }

    private void mostrarSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro de validação", JOptionPane.ERROR_MESSAGE);
    }

    private void carregarTabela() {
        try {
            modeloTabela.setRowCount(0);
            List<TipoDespesa> tipos = controller.obterTodosTipos();
            for (TipoDespesa t : tipos) {
                modeloTabela.addRow(new Object[]{t.getIdTipoDespesa(), t.getDescricao()});
            }
        } catch (IOException e) {
            mostrarErro("Erro ao carregar tipos: " + e.getMessage());
        }
    }
}
