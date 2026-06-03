package br.com.model;

public class Movimentacao {
    private Long idMovimentacao;
    private Long idVeiculo;
    private Long idTipoDespesa;
    private String descricao;
    private String data;
    private double valor;
    private String tipo;
    private double distanciaPercorridaKm;
    private double litrosCombustivel;

    public Movimentacao() {
    }

    public Movimentacao(Long idMovimentacao, Long idVeiculo, Long idTipoDespesa, String descricao, String data, double valor, String tipo) {
        this(idMovimentacao, idVeiculo, idTipoDespesa, descricao, data, valor, tipo, 0.0, 0.0);
    }

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

    public boolean possuiDadosConsumo() {
        return distanciaPercorridaKm > 0 && litrosCombustivel > 0;
    }

    
    public double calcularConsumoMedioKmPorLitro() {
        if (!possuiDadosConsumo()) {
            return 0.0;
        }
        return distanciaPercorridaKm / litrosCombustivel;
    }

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
