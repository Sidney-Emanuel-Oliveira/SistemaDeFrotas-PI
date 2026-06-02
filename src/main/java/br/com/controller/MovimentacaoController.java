package br.com.controller;

import br.com.dao.MovimentacaoDAO;
import br.com.model.Movimentacao;
import br.com.utils.Validacoes;

import java.io.IOException;
import java.util.List;

public class MovimentacaoController {
    private MovimentacaoDAO movimentacaoDAO;

    // Inicializa o controller e o DAO de movimentações
    public MovimentacaoController() {
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    // Salva uma movimentação sem dados de consumo
    public void salvarMovimentacao(Long idVeiculo, Long idTipoDespesa, String descricao,
                                  String data, String valor, String tipo) throws IOException {
        salvarMovimentacao(idVeiculo, idTipoDespesa, descricao, data, valor, tipo, "", "");
    }

    // Salva uma movimentação sem dados de consumo com informações a mais
    public void salvarMovimentacao(Long idVeiculo, Long idTipoDespesa, String descricao,
                                  String data, String valor, String tipo,
                                  String distanciaPercorridaKm, String litrosCombustivel) throws IOException {
        validarMovimentacao(idVeiculo, idTipoDespesa, descricao, data, valor, distanciaPercorridaKm, litrosCombustivel);

        Long novoId = movimentacaoDAO.obterProximoId();
        double valorParsed = parseValor(valor);
        double distancia = parseValorOpcional(distanciaPercorridaKm);
        double litros = parseValorOpcional(litrosCombustivel);

        Movimentacao movimentacao = new Movimentacao(novoId, idVeiculo, idTipoDespesa,
                                                      descricao, data, valorParsed, tipo, distancia, litros);
        movimentacaoDAO.salvar(movimentacao);
    }

    // Atualiza uma movimentação sem dados de consumo
    public void atualizarMovimentacao(Long id, Long idVeiculo, Long idTipoDespesa, String descricao,
                                     String data, String valor, String tipo) throws IOException {
        atualizarMovimentacao(id, idVeiculo, idTipoDespesa, descricao, data, valor, tipo, "", "");
    }

    // Atualiza uma movimentação com ou sem dados de consumo
    public void atualizarMovimentacao(Long id, Long idVeiculo, Long idTipoDespesa, String descricao,
                                     String data, String valor, String tipo,
                                     String distanciaPercorridaKm, String litrosCombustivel) throws IOException {
        validarMovimentacao(idVeiculo, idTipoDespesa, descricao, data, valor, distanciaPercorridaKm, litrosCombustivel);

        Movimentacao existente = movimentacaoDAO.obterPorId(id);
        String dataFinal = data;
        if (existente != null && existente.getData() != null && !existente.getData().isBlank()) {
            
            dataFinal = existente.getData();
        }

        double valorParsed = parseValor(valor);
        double distancia = parseValorOpcional(distanciaPercorridaKm);
        double litros = parseValorOpcional(litrosCombustivel);

        Movimentacao movimentacao = new Movimentacao(id, idVeiculo, idTipoDespesa,
                                                      descricao, dataFinal, valorParsed, tipo, distancia, litros);
        movimentacaoDAO.salvar(movimentacao);
    }

    // Valida os dados informados para a movimentação
    private void validarMovimentacao(Long idVeiculo, Long idTipoDespesa, String descricao, String data,
                                     String valor, String distanciaPercorridaKm, String litrosCombustivel) {
        if (!Validacoes.validarCampoVazio(descricao) ||
            !Validacoes.validarData(data) ||
            !Validacoes.validarValor(valor) ||
            idVeiculo == null ||
            idTipoDespesa == null) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        double distancia = parseValorOpcional(distanciaPercorridaKm);
        double litros = parseValorOpcional(litrosCombustivel);
        if ((distancia > 0 && litros <= 0) || (litros > 0 && distancia <= 0)) {
            throw new IllegalArgumentException("Para calcular consumo médio, informe distância e litros juntos.");
        }
    }

    // Converte um valor obrigatório para double
    private double parseValor(String valor) {
        return Double.parseDouble(valor.replace(",", "."));
    }

    // Converte um valor opcional para double.
    // Retorna 0.0 quando vazio.
    private double parseValorOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return 0.0;
        }
        try {
            double valorParsed = Double.parseDouble(valor.trim().replace(",", "."));
            if (valorParsed < 0) {
                throw new IllegalArgumentException("Campos numéricos opcionais não podem ser negativos.");
            }
            return valorParsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe distância/litros usando números válidos.");
        }
    }

    // Retorna todas as movimentações cadastradas
    public List<Movimentacao> obterTodasMovimentacoes() throws IOException {
        return movimentacaoDAO.obterTodos();
    }

    // Retorna as movimentações de um veículo específico
    public List<Movimentacao> obterMovimentacoesPorVeiculo(Long idVeiculo) throws IOException {
        return movimentacaoDAO.obterPorVeiculo(idVeiculo);
    }

    // Retorna as movimentações de um determinado tipo
    public List<Movimentacao> obterMovimentacoesPorTipo(String tipo) throws IOException {
        return movimentacaoDAO.obterPorTipo(tipo);
    }

    // Busca uma movimentação pelo seu identificador
    public Movimentacao obterMovimentacaoPorId(Long id) throws IOException {
        return movimentacaoDAO.obterPorId(id);
    }

    // Remove uma movimentação pelo seu identificador
    public void deletarMovimentacao(Long id) throws IOException {
        movimentacaoDAO.deletar(id);
    }
}
