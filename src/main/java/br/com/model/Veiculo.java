package br.com.model;

public class Veiculo {
    private Long idVeiculo;
    private String placa;
    private String marca;
    private String modelo;
    private String fabricateYear;
    private Boolean ativo;
    private TipoVeiculo tipo;

    public Veiculo() {
        this.tipo = TipoVeiculo.OUTRO;
    }

    public Veiculo(Long idVeiculo, String placa, String marca, String modelo, String fabricateYear, Boolean ativo, String tipo) {
        this.idVeiculo = idVeiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.fabricateYear = fabricateYear;
        this.ativo = ativo;
        setTipo(tipo);
    }

    public Veiculo(Long idVeiculo, String placa, String marca, String modelo, String fabricateYear, Boolean ativo, TipoVeiculo tipo) {
        this.idVeiculo = idVeiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.fabricateYear = fabricateYear;
        this.ativo = ativo;
        this.tipo = tipo != null ? tipo : TipoVeiculo.OUTRO;
    }
    
    public Long getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(Long idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFabricateYear() {
        return fabricateYear;
    }

    public void setFabricateYear(String fabricateYear) {
        this.fabricateYear = fabricateYear;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getTipo() {
        return tipo != null ? tipo.getDescricao() : TipoVeiculo.OUTRO.getDescricao();
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipo != null ? tipo : TipoVeiculo.OUTRO;
    }

    public void setTipo(String tipo) {
        this.tipo = TipoVeiculo.fromDescricao(tipo);
    }

    public void setTipoVeiculo(TipoVeiculo tipo) {
        this.tipo = tipo != null ? tipo : TipoVeiculo.OUTRO;
    }

    @Override
    public String toString() {
        return placa + " - " + marca + " " + modelo;
    }

    
    public String toStringDetailed() {
        return "Veiculo{" +
                "idVeiculo=" + idVeiculo +
                ", placa='" + placa + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", fabricateYear='" + fabricateYear + '\'' +
                ", ativo=" + ativo +
                ", tipo='" + getTipo() + '\'' +
                '}';
    }
}
