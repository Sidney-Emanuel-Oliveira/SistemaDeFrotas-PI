package br.com.view;

import br.com.controller.TipoDespesaController;
import br.com.model.TipoDespesa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

// Painel de visualização responsável pelo CRUD completo dos tipos de despesa da frota
public class TelaCadastroDespesa extends JPanel {
    private TipoDespesaController controller;
    private JTextField txtDescricao;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnSalvar, btnLimpar, btnEditar, btnDeletar, btnAtualizar;

    // Ponteiro de estado: armazena o ID do registro em edição; se nulo, indica modo de inserção
    private Long idSelecionado = null;

    public TelaCadastroDespesa() {
        controller = new TipoDespesaController();
        setLayout(new BorderLayout(10, 10)); // Gerenciador de bordas com espaçamento entre as seções
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Divisão da Tela: Setor de Inputs na parte superior e listagem de dados na região central
        JPanel painelEntrada = criarPainelEntrada();
        add(painelEntrada, BorderLayout.NORTH);

        JPanel painelTabela = criarPainelTabela();
        add(painelTabela, BorderLayout.CENTER);

        carregarTabela(); // Alimenta a tabela síncronamente logo no início do ciclo de vida da view
    }

    // Monta o formulário estrutural superior e acopla a barra de ferramentas de controle
    private JPanel criarPainelEntrada() {
        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro de Tipo de Despesa"));

        painel.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        painel.add(txtDescricao);

        // Define a barra de ações horizontais
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar");
        btnEditar = new JButton("Editar Selecionado");
        btnAtualizar = new JButton("Atualizar");
        btnDeletar = new JButton("Deletar Selecionado");

        // Inicializa o botão de persistência de alteração oculto por padrão
        btnAtualizar.setVisible(false);

        // Vincula as funções operacionais aos escutadores de ação por Expressão Lambda
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

    // Estrutura a tabela redefinindo as permissões do modelo padrão para bloquear a edição direta nas células
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Tipos de Despesas Cadastrados"));

        // Sobrescreve isCellEditable para travar inputs manuais em cima do grid
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

    // Envia o payload textual do formulário para o controlador gravar uma nova entidade
    private void salvarTipoDespesa() {
        try {
            String descricao = txtDescricao.getText();
            controller.salvarTipoDespesa(descricao);
            JOptionPane.showMessageDialog(this, "Tipo de despesa salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            carregarTabela();
        } catch (IllegalArgumentException e) { // Captura falhas de regras de negócio internas (Campos vazios, etc.)
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) { // Captura problemas físicos de I/O em arquivos de texto
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Coleta a chave do ID e submete as alterações textuais para atualização do registro
    private void atualizarTipoDespesa() {
        try {
            if (idSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Nenhum tipo selecionado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String descricao = txtDescricao.getText();
            controller.atualizarTipoDespesa(idSelecionado, descricao);
            JOptionPane.showMessageDialog(this, "Tipo de despesa updated com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // Reverte a interface gráfica de volta para o estado original de "Salvar"
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

    // Intercepta a linha em foco na tabela, lê as colunas de dados e altera os botões para o Modo de Edição
    private void editarTipoSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) { // Validação de segurança: verifica se há seleção ativa no grid
            JOptionPane.showMessageDialog(this, "Selecione um tipo para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mapeia os dados lidos das colunas 0 (ID) e 1 (Descrição) para as variáveis de controle da view
        idSelecionado = Long.parseLong(modeloTabela.getValueAt(linhaSelecionada, 0).toString());
        txtDescricao.setText(modeloTabela.getValueAt(linhaSelecionada, 1).toString());

        // Altera a visibilidade dos botões chaveando os fluxos operacionais
        btnSalvar.setVisible(false);
        btnAtualizar.setVisible(true);
    }

    // Dispara uma caixa de confirmação antes de executar o comando definitivo de deleção no banco/arquivo
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

    // Reseta todos os dados textuais, ponteiros lógicos e reestabelece a visibilidade padrão da UI
    private void limparCampos() {
        txtDescricao.setText("");
        idSelecionado = null;
        btnSalvar.setVisible(true);
        btnAtualizar.setVisible(false);
    }

    // Consulta a camada de persistência e atualiza de forma incremental o grid visível
    private void carregarTabela() {
        try {
            modeloTabela.setRowCount(0); // Esvazia todas as linhas antigas para evitar duplicação no refresh
            List<TipoDespesa> tipos = controller.obterTodosTipos();

            // Preenche o grid adicionando linha por linha no modelo de vetor de objetos
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