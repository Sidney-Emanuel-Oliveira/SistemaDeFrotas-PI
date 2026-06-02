package br.com.model;

// Classe de modelo (JavaBean) que representa a entidade ou categoria de uma despesa
public class TipoDespesa {
    private Long idTipoDespesa;
    private String descricao;

    // Construtor padrão necessário para frameworks de persistência e serialização
    public TipoDespesa() {
    }

    // Construtor completo para inicialização rápida de instâncias na memória
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

    // Sobrescrita para exibir diretamente o nome/descrição em componentes visuais (ex: ComboBox)
    @Override
    public String toString() {
        return descricao;
    }
}