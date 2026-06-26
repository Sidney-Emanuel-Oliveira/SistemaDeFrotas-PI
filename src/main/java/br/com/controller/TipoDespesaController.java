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

    // Tamanho mínimo e máximo para a descrição do tipo de despesa
    private static final int DESCRICAO_MIN = 2;
    private static final int DESCRICAO_MAX = 100;

    public TipoDespesaController() {
        this.tipoDespesaDAO = new TipoDespesaDAO();
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    /**
     * Salva um novo tipo de despesa.
     * Valida:
     *  - Campo não vazio
     *  - Tamanho mínimo e máximo
     *  - Duplicidade de descrição (case-insensitive)
     */
    public void salvarTipoDespesa(String descricao) throws IOException {
        validarDescricao(descricao);
        verificarDuplicidade(descricao, null);

        Long novoId = tipoDespesaDAO.obterProximoId();
        TipoDespesa tipoDespesa = new TipoDespesa(novoId, descricao.trim());
        tipoDespesaDAO.salvar(tipoDespesa);
    }

    /**
     * Atualiza um tipo de despesa existente.
     * Valida:
     *  - ID não nulo e existente
     *  - Campo não vazio
     *  - Tamanho mínimo e máximo
     *  - Duplicidade de descrição (case-insensitive, ignorando o próprio registro)
     */
    public void atualizarTipoDespesa(Long id, String descricao) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID do tipo de despesa é obrigatório para atualização!");
        }

        TipoDespesa existente = tipoDespesaDAO.obterPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Tipo de despesa com ID " + id + " não encontrado!");
        }

        validarDescricao(descricao);
        verificarDuplicidade(descricao, id);

        TipoDespesa tipoDespesa = new TipoDespesa(id, descricao.trim());
        tipoDespesaDAO.salvar(tipoDespesa);
    }

    /**
     * Retorna todos os tipos de despesas cadastrados.
     */
    public List<TipoDespesa> obterTodosTipos() throws IOException {
        return tipoDespesaDAO.obterTodos();
    }

    /**
     * Busca um tipo de despesa pelo ID.
     */
    public TipoDespesa obterTipoPorId(Long id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo!");
        }
        return tipoDespesaDAO.obterPorId(id);
    }

    /**
     * Verifica se um tipo de despesa possui movimentações vinculadas.
     * Usado para impedir a exclusão de tipos em uso.
     */
    public boolean temMovimentacoesVinculadas(Long id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo!");
        }
        return movimentacaoDAO.existeMovimentacaoPorIdTipo(id);
    }

    /**
     * Exclui um tipo de despesa pelo ID.
     * Bloqueia exclusão se houver movimentações vinculadas.
     */
    public void deletarTipoDespesa(Long id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo!");
        }

        if (tipoDespesaDAO.obterPorId(id) == null) {
            throw new IllegalArgumentException("Tipo de despesa não encontrado!");
        }

        if (movimentacaoDAO.existeMovimentacaoPorIdTipo(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir: existem movimentações vinculadas a este tipo de despesa.");
        }

        tipoDespesaDAO.deletar(id);
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares de validação
    // -------------------------------------------------------------------------

    /**
     * Valida a descrição: não vazia, tamanho mínimo e máximo, apenas caracteres válidos.
     */
    private void validarDescricao(String descricao) {
        if (!Validacoes.validarCampoVazio(descricao)) {
            throw new IllegalArgumentException("Descrição não pode estar vazia!");
        }

        String desc = descricao.trim();

        if (desc.length() < DESCRICAO_MIN) {
            throw new IllegalArgumentException(
                    "Descrição deve ter pelo menos " + DESCRICAO_MIN + " caracteres!");
        }

        if (desc.length() > DESCRICAO_MAX) {
            throw new IllegalArgumentException(
                    "Descrição não pode ultrapassar " + DESCRICAO_MAX + " caracteres!");
        }

        // Permite letras (incluindo acentuadas), números, espaços e hífens
        if (!desc.matches("[\\p{L}\\p{N} \\-]+")) {
            throw new IllegalArgumentException(
                    "Descrição contém caracteres inválidos. Use apenas letras, números, espaços e hífens.");
        }
    }

    /**
     * Verifica se já existe outro tipo de despesa com a mesma descrição (case-insensitive).
     * @param descricao Descrição a verificar
     * @param idIgnorar ID do registro a ignorar na verificação (null para novos registros)
     */
    private void verificarDuplicidade(String descricao, Long idIgnorar) throws IOException {
        List<TipoDespesa> todos = tipoDespesaDAO.obterTodos();
        String descNormalizada = descricao.trim().toLowerCase();

        for (TipoDespesa t : todos) {
            if (t.getDescricao().trim().toLowerCase().equals(descNormalizada)) {
                if (idIgnorar == null || !t.getIdTipoDespesa().equals(idIgnorar)) {
                    throw new IllegalArgumentException(
                            "Já existe um tipo de despesa com a descrição \"" + descricao.trim() + "\"!");
                }
            }
        }
    }
}
