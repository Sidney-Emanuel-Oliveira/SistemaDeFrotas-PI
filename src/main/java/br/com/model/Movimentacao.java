package br.com.model;

/**
 * Classe modelo que representa uma Movimentação (transação financeira) de um veículo
 * Registra despesas, consumo de combustível e distância percorrida
 */
public class Movimentacao {
    // Identificador único da movimentação
    private Long idMovimentacao;
    // ID do veículo associado a esta movimentação
    private Long idVeiculo;
    // ID do tipo de despesa (Combustível, Seguro, Manutenção, etc)
    private Long idTipoDespesa;
    // Descrição da movimentação (ex: "Abastecimento em SP")
    private String descricao;
    // Data da movimentação (formato: dd/MM/yyyy)
    private String data;
    // Valor em reais da movimentação
    private double valor;
    // Tipo de movimentação (pode ser usado para categorização)
    private String tipo;
    // Distância percorrida em quilômetros (para cálculo de consumo)
    private double distanciaPercorridaKm;
    // Quantidade de litros de combustível consumidos
    private double litrosCombustivel;

    // Construtor padrão
    public Movimentacao() {
    }

    // Construtor sem dados de consumo (distância e litros)
    public Movimentacao(Long idMovimentacao, Long idVeiculo, Long idTipoDespesa, String descricao, String data, double valor, String tipo) {
        this(idMovimentacao, idVeiculo, idTipoDespesa, descricao, data, valor, tipo, 0.0, 0.0);
    }

    // Construtor completo com todos os parâmetros incluindo dados de consumo
    public Movimentacao(Long idMovimentacao, Long idVeiculo, Long idTipoDespesa, String descricao, String data,
                        double valor, String tipo, double distanciaPercorridaKm, double litrosCombustivel) {
        this.idMovimentacao = idMovimentacao;
        this.idVeiculo = idVeiculo;
        this.idTipoDespesa = idTipoDespesa;
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
        this.tipo = tipo;
        this.distanciaPercorridaKm = distanciaPercorridaKm;
        this.litrosCombustivel = litrosCombustivel;
    }

    // Getters e Setters para cada atributo
    public Long getIdMovimentacao() {
        return idMovimentacao;
    }

    public void setIdMovimentacao(Long idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }

    public Long getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(Long idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public Long getIdTipoDespesa() {
        return idTipoDespesa;
    }

    public void setIdTipoDespesa(Long idTipoDespesa) {
        this.idTipoDespesa = idTipoDespesa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getDistanciaPercorridaKm() {
        return distanciaPercorridaKm;
    }

    public void setDistanciaPercorridaKm(double distanciaPercorridaKm) {
        this.distanciaPercorridaKm = distanciaPercorridaKm;
    }

    public double getLitrosCombustivel() {
        return litrosCombustivel;
    }

    public void setLitrosCombustivel(double litrosCombustivel) {
        this.litrosCombustivel = litrosCombustivel;
    }

    // Verifica se a movimentação possui dados válidos de consumo
    public boolean possuiDadosConsumo() {
        return distanciaPercorridaKm > 0 && litrosCombustivel > 0;
    }

    // Calcula e retorna o consumo médio em km/l (quilômetro por litro)
    public double calcularConsumoMedioKmPorLitro() {
        if (!possuiDadosConsumo()) {
            return 0.0;
        }
        return distanciaPercorridaKm / litrosCombustivel;
    }

    // Retorna uma representação em String com todos os atributos da movimentação
    @Override
    public String toString() {
        return "Movimentacao{" +
                "idMovimentacao=" + idMovimentacao +
                ", idVeiculo=" + idVeiculo +
                ", idTipoDespesa=" + idTipoDespesa +
                ", descricao='" + descricao + '\'' +
                ", data='" + data + '\'' +
                ", valor=" + valor +
                ", tipo='" + tipo + '\'' +
                ", distanciaPercorridaKm=" + distanciaPercorridaKm +
                ", litrosCombustivel=" + litrosCombustivel +
                '}';
    }
}
