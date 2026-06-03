package br.com.model;

/**
 * Classe auxiliar para representar itens em combobox de veículos
 * Pode representar "Todos" ou um veículo específico
 */
public class VeiculoComboItem {
    // Referência ao veículo (null se for "Todos")
    private final Veiculo veiculo;
    // Texto exibido no combobox
    private final String texto;
    // Flag indicando se é o item "Todos"
    private final boolean isTodos;

    // Construtor para item "Todos"
    public VeiculoComboItem() {
        this.veiculo = null;
        this.texto = "Todos";
        this.isTodos = true;
    }

    // Construtor para item específico de veículo
    public VeiculoComboItem(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.texto = veiculo.getPlaca() + " - " + veiculo.getMarca() + " " + veiculo.getModelo();
        this.isTodos = false;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public boolean isTodos() {
        return isTodos;
    }

    @Override
    public String toString() {
        return texto;
    }
}

