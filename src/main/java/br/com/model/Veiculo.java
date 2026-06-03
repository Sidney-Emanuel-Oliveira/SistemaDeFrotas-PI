package br.com.model;

/**
 * Classe modelo que representa um Veículo na frota
 * Armazena informações como placa, marca, modelo, ano e tipo de veículo
 */
public class Veiculo {
    // Identificador único do veículo
    private Long idVeiculo;
    // Placa do veículo (ex: ABC-1234)
    private String placa;
    // Marca/Fabricante do veículo (ex: Toyota, Ford)
    private String marca;
    // Modelo do veículo (ex: Corolla, Fusion)
    private String modelo;
    // Ano de fabricação do veículo
    private String fabricateYear;
    // Status: true=ativo, false=inativo
    private Boolean ativo;
    // Tipo de veículo (Carro, Moto, Caminhão, etc)
    private TipoVeiculo tipo;

    // Construtor padrão
    public Veiculo() {
        this.tipo = TipoVeiculo.OUTRO;
    }

    // Construtor com parâmetros (tipo como String)
    public Veiculo(Long idVeiculo, String placa, String marca, String modelo, String fabricateYear, Boolean ativo, String tipo) {
        this.idVeiculo = idVeiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.fabricateYear = fabricateYear;
        this.ativo = ativo;
        setTipo(tipo);
    }

    // Construtor com parâmetros (tipo como Enum TipoVeiculo)
    public Veiculo(Long idVeiculo, String placa, String marca, String modelo, String fabricateYear, Boolean ativo, TipoVeiculo tipo) {
        this.idVeiculo = idVeiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.fabricateYear = fabricateYear;
        this.ativo = ativo;
        this.tipo = tipo != null ? tipo : TipoVeiculo.OUTRO;
    }
    
    // Getters e Setters para cada atributo
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
    // Retorna uma representação em String simplificada do veículo (placa - marca modelo)
    public String toString() {
        return placa + " - " + marca + " " + modelo;
    }

    // Retorna uma representação detalhada com todos os atributos do veículo
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
