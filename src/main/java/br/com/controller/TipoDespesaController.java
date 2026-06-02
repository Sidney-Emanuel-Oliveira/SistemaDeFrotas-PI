package br.com.controller;

import br.com.dao.TipoDespesaDAO;
import br.com.model.TipoDespesa;
import br.com.utils.Validacoes;

import java.io.IOException;
import java.util.List;

public class TipoDespesaController {
    private TipoDespesaDAO tipoDespesaDAO;

    /**
     * Construtor da classe
     * Inicializa o DAO
     */
    public TipoDespesaController() {
        this.tipoDespesaDAO = new TipoDespesaDAO();
    }

    // Salva um novo tipo de despesa.
    public void salvarTipoDespesa(String descricao) throws IOException {

        // Valida se a descrição foi informada
        if (!Validacoes.validarCampoVazio(descricao)) {
            throw new IllegalArgumentException("Descrição não pode estar vazia!");
        }

        // Obtém o próximo ID disponível
        Long novoId = tipoDespesaDAO.obterProximoId();
        // Cria o objeto TipoDespesa
        TipoDespesa tipoDespesa = new TipoDespesa(novoId, descricao);
        // Salva no arquivo/banco de dados
        tipoDespesaDAO.salvar(tipoDespesa);
    }

    // Atualiza um tipo de despesa existente
    public void atualizarTipoDespesa(Long id, String descricao) throws IOException {
        // Valida se a descrição foi informada
        if (!Validacoes.validarCampoVazio(descricao)) {
            throw new IllegalArgumentException("Descrição não pode estar vazia!");
        }

        // Cria o objeto com os dados atualizados
        TipoDespesa tipoDespesa = new TipoDespesa(id, descricao);

        // Salva as alterações
        tipoDespesaDAO.salvar(tipoDespesa);
    }

    // Retorna todos os tipos de despesa cadastrados
    public List<TipoDespesa> obterTodosTipos() throws IOException {
        return tipoDespesaDAO.obterTodos();
    }

    // Busca um tipo de despesa pelo ID
    public TipoDespesa obterTipoPorId(Long id) throws IOException {
        return tipoDespesaDAO.obterPorId(id);
    }

    // Remove um tipo de despesa pelo ID
    public void deletarTipoDespesa(Long id) throws IOException {
        tipoDespesaDAO.deletar(id);
    }
}

