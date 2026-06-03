package br.com.model;

/**
 * Classe modelo que representa um Tipo de Despesa
 * Categoriza as movimentações (ex: Combustível, Seguro, Manutenção)
 */
public class TipoDespesa {
    // Identificador único do tipo de despesa
    private Long idTipoDespesa;
    // Descrição do tipo de despesa (ex: "Combustível", "Seguro")
    private String descricao;

    // Construtor padrão
    public TipoDespesa() {
    }

    // Construtor com parâmetros
    public TipoDespesa(Long idTipoDespesa, String descricao) {
        this.idTipoDespesa = idTipoDespesa;
        this.descricao = descricao;
    }

    // Getters e Setters
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

    // Retorna a descrição do tipo de despesa
    @Override
    public String toString() {
        return descricao;
    }
}