package br.com.view;

import br.com.controller.TipoDespesaController;
import br.com.model.TipoDespesa;
import br.com.ui.ModernColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Tela de cadastro e gerenciamento de tipos de despesas
 * Permite criar, editar e deletar categorias de despesas
 */
public class TelaCadastroDespesa extends JPanel {
    // Controller para operações de tipos de despesas
    private TipoDespesaController controller;
    // Campo de entrada para descrição
    private JTextField txtDescricao;
    // Tabela que exibe tipos cadastrados
    private JTable tabela;
    // Modelo da tabela
    private DefaultTableModel modeloTabela;
    // Botões de operações
    private JButton btnSalvar, btnLimpar, btnEditar, btnDeletar, btnAtualizar;

    // ID do tipo selecionado para edição
    private Long idSelecionado = null;

    // Construtor que inicializa a tela
    public TelaCadastroDespesa() {
        controller = new TipoDespesaController();
        setLayout(new BorderLayout(10, 10)); 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(ModernColors.BG_PRIMARY);
        
        JPanel painelEntrada = criarPainelEntrada();
        add(painelEntrada, BorderLayout.NORTH);

        JPanel painelTabela = criarPainelTabela();
        add(painelTabela, BorderLayout.CENTER);

        carregarTabela(); 
    }

    // Cria painel com campos de entrada e botões
    private JPanel criarPainelEntrada() {
        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro de Tipo de Despesa"));

        painel.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        painel.add(txtDescricao);

        
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar");
        btnEditar = new JButton("Editar Selecionado");
        btnAtualizar = new JButton("Atualizar");
        btnDeletar = new JButton("Deletar Selecionado");

        
        btnAtualizar.setVisible(false);

        
        btnSalvar.addActionListener(e -> salvarTipoDespesa());
        btnLimpar.addActionListener(e -> limparCampos());
        btnEditar.addActionListener(e -> editarTipoSelecionado());
        btnAtualizar.addActionListener(e -> atualizarTipoDespesa());
        btnDeletar.addActionListener(e -> deletarTipoSelecionado());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnDeletar);

        JPanel painelCompleto = new JPanel(new BorderLayout());
        painelCompleto.add(painel, BorderLayout.CENTER);
        painelCompleto.add(painelBotoes, BorderLayout.SOUTH);

        return painelCompleto;
    }

    
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
        JScrollPane scrollPane = new JScrollPane(tabela);
        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }

    
    private void salvarTipoDespesa() {
        try {
            String descricao = txtDescricao.getText();
            controller.salvarTipoDespesa(descricao);
            JOptionPane.showMessageDialog(this, "Tipo de despesa salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            carregarTabela();
        } catch (IllegalArgumentException e) { 
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) { 
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void atualizarTipoDespesa() {
        try {
            if (idSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Nenhum tipo selecionado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String descricao = txtDescricao.getText();
            controller.atualizarTipoDespesa(idSelecionado, descricao);
            JOptionPane.showMessageDialog(this, "Tipo de despesa updated com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            
            limparCampos();
            btnSalvar.setVisible(true);
            btnAtualizar.setVisible(false);
            carregarTabela();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void editarTipoSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) { 
            JOptionPane.showMessageDialog(this, "Selecione um tipo para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        idSelecionado = Long.parseLong(modeloTabela.getValueAt(linhaSelecionada, 0).toString());
        txtDescricao.setText(modeloTabela.getValueAt(linhaSelecionada, 1).toString());

        
        btnSalvar.setVisible(false);
        btnAtualizar.setVisible(true);
    }

    
    private void deletarTipoSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um tipo para deletar!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = Long.parseLong(modeloTabela.getValueAt(linhaSelecionada, 0).toString());
        int resultado = JOptionPane.showConfirmDialog(this, "Deseja deletar este tipo de despesa?", "Confirmação", JOptionPane.YES_NO_OPTION);

        if (resultado == JOptionPane.YES_OPTION) {
            try {
                controller.deletarTipoDespesa(id);
                JOptionPane.showMessageDialog(this, "Tipo de despesa deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarTabela();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    private void limparCampos() {
        txtDescricao.setText("");
        idSelecionado = null;
        btnSalvar.setVisible(true);
        btnAtualizar.setVisible(false);
    }

    
    private void carregarTabela() {
        try {
            modeloTabela.setRowCount(0); 
            List<TipoDespesa> tipos = controller.obterTodosTipos();

            
            for (TipoDespesa t : tipos) {
                modeloTabela.addRow(new Object[]{
                        t.getIdTipoDespesa(),
                        t.getDescricao()
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar tipos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}