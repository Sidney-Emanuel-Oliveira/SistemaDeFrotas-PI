package br.com.model;

public class TipoDespesa {
    private Long idTipoDespesa;
    private String descricao;

    public TipoDespesa() {
    }

    public TipoDespesa(Long idTipoDespesa, String descricao) {
        this.idTipoDespesa = idTipoDespesa;
        this.descricao = descricao;
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

    @Override
    public String toString() {
        return descricao;
    }
}

