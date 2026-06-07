package br.com.controller;

import br.com.dao.MovimentacaoDAO;
import br.com.dao.TipoDespesaDAO;
import br.com.model.TipoDespesa;
import br.com.utils.Validacoes;

import java.io.IOException;
import java.util.List;

public class TipoDespesaController {
    private TipoDespesaDAO tipoDespesaDAO;
    private MovimentacaoDAO movimentacaoDAO;

    public TipoDespesaController() {
        this.tipoDespesaDAO = new TipoDespesaDAO();
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    public void salvarTipoDespesa(String descricao) throws IOException {

        if (!Validacoes.validarCampoVazio(descricao)) {
            throw new IllegalArgumentException("Descrição não pode estar vazia!");
        }

        Long novoId = tipoDespesaDAO.obterProximoId();

        TipoDespesa tipoDespesa = new TipoDespesa(novoId, descricao);

        tipoDespesaDAO.salvar(tipoDespesa);
    }


    public void atualizarTipoDespesa(Long id, String descricao) throws IOException {

        if (!Validacoes.validarCampoVazio(descricao)) {
            throw new IllegalArgumentException("Descrição não pode estar vazia!");
        }


        TipoDespesa tipoDespesa = new TipoDespesa(id, descricao);


        tipoDespesaDAO.salvar(tipoDespesa);
    }

    // Retorna todos os tipos de despesas cadastrados
    public List<TipoDespesa> obterTodosTipos() throws IOException {
        return tipoDespesaDAO.obterTodos();
    }

    // Busca um tipo de despesa específico pelo ID
    public TipoDespesa obterTipoPorId(Long id) throws IOException {
        return tipoDespesaDAO.obterPorId(id);
    }

    // Verifica se um tipo de despesa possui movimentações vinculadas
    public boolean temMovimentacoesVinculadas(Long id) throws IOException {
        return movimentacaoDAO.existeMovimentacaoPorIdTipo(id);
    }

    // Deleta um tipo de despesa pelo ID
    public void deletarTipoDespesa(Long id) throws IOException {
        tipoDespesaDAO.deletar(id);
    }
}