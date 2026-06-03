package br.com.controller;

import br.com.dao.MovimentacaoDAO;
import br.com.model.Movimentacao;
import br.com.utils.Validacoes;

import java.io.IOException;
import java.util.List;

public class MovimentacaoController {
    private MovimentacaoDAO movimentacaoDAO;

    
    public MovimentacaoController() {
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    
    public void salvarMovimentacao(Long idVeiculo, Long idTipoDespesa, String descricao,
                                  String data, String valor, String tipo) throws IOException {
        salvarMovimentacao(idVeiculo, idTipoDespesa, descricao, data, valor, tipo, "", "");
    }

    
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

    
    public void atualizarMovimentacao(Long id, Long idVeiculo, Long idTipoDespesa, String descricao,
                                     String data, String valor, String tipo) throws IOException {
        atualizarMovimentacao(id, idVeiculo, idTipoDespesa, descricao, data, valor, tipo, "", "");
    }

    
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

    
    private double parseValor(String valor) {
        return Double.parseDouble(valor.replace(",", "."));
    }

    
    
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

    
    public List<Movimentacao> obterTodasMovimentacoes() throws IOException {
        return movimentacaoDAO.obterTodos();
    }

    
    public List<Movimentacao> obterMovimentacoesPorVeiculo(Long idVeiculo) throws IOException {
        return movimentacaoDAO.obterPorVeiculo(idVeiculo);
    }

    
    public List<Movimentacao> obterMovimentacoesPorTipo(String tipo) throws IOException {
        return movimentacaoDAO.obterPorTipo(tipo);
    }

    
    public Movimentacao obterMovimentacaoPorId(Long id) throws IOException {
        return movimentacaoDAO.obterPorId(id);
    }

    
    public void deletarMovimentacao(Long id) throws IOException {
        movimentacaoDAO.deletar(id);
    }
}
