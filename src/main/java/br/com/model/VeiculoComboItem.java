package br.com.model;

public class VeiculoComboItem {
    private final Veiculo veiculo;
    private final String texto;
    private final boolean isTodos;

    // Cria a opção "Todos"
    public VeiculoComboItem() {
        this.veiculo = null;
        this.texto = "Todos";
        this.isTodos = true;
    }

    // Cria um item a partir de um veículo
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

