package br.com.estruturas;

import br.com.model.Veiculo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class OrdenacaoVeiculos {

    public static List<Veiculo> ordenar(List<Veiculo> veiculos, String criterio) {
        List<Veiculo> ordenados = new ArrayList<>(veiculos);
        Comparator<Veiculo> comparador = obterComparador(criterio);

        for (int i = 0; i < ordenados.size() - 1; i++) {
            int menorIndice = i;
            for (int j = i + 1; j < ordenados.size(); j++) {
                if (comparador.compare(ordenados.get(j), ordenados.get(menorIndice)) < 0) {
                    menorIndice = j;
                }
            }

            if (menorIndice != i) {
                Veiculo temporario = ordenados.get(i);
                ordenados.set(i, ordenados.get(menorIndice));
                ordenados.set(menorIndice, temporario);
            }
        }

        return ordenados;
    }

    private static Comparator<Veiculo> obterComparador(String criterio) {
        String c = criterio == null ? "ID / Ordem de cadastro" : criterio;

        if (c.equalsIgnoreCase("Marca")) {
            return Comparator.comparing(v -> textoSeguro(v.getMarca()));
        }
        if (c.equalsIgnoreCase("Modelo")) {
            return Comparator.comparing(v -> textoSeguro(v.getModelo()));
        }
        if (c.equalsIgnoreCase("Placa")) {
            return Comparator.comparing(v -> textoSeguro(v.getPlaca()));
        }
        if (c.equalsIgnoreCase("Ano de fabricação")) {
            return Comparator.comparingInt(OrdenacaoVeiculos::anoSeguro);
        }
        if (c.equalsIgnoreCase("Tipo de veículo")) {
            return Comparator.comparing(v -> textoSeguro(v.getTipo()));
        }

        return Comparator.comparingLong(v -> v.getIdVeiculo() == null ? 0L : v.getIdVeiculo());
    }

    private static String textoSeguro(String texto) {
        return texto == null ? "" : texto.toUpperCase();
    }

    private static int anoSeguro(Veiculo veiculo) {
        try {
            return Integer.parseInt(veiculo.getFabricateYear());
        } catch (Exception e) {
            return 0;
        }
    }
}
