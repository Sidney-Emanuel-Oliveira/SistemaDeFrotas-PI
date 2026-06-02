package br.com.estruturas;

import br.com.model.Veiculo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdenacaoVeiculos {

    // Método principal que duplica a lista original para preservá-la e aplica a ordenação
    public static List<Veiculo> ordenar(List<Veiculo> veiculos, String criterio) {
        List<Veiculo> ordenados = new ArrayList<>(veiculos);
        Comparator<Veiculo> comparador = obterComparador(criterio);

        // Algoritmo de ordenação manual: Selection Sort (Ordenação por Seleção)
        for (int i = 0; i < ordenados.size() - 1; i++) {
            int menorIndice = i;

            // Varre o restante da lista procurando o menor elemento com base no comparador escolhido
            for (int j = i + 1; j < ordenados.size(); j++) {
                if (comparador.compare(ordenados.get(j), ordenados.get(menorIndice)) < 0) {
                    menorIndice = j;
                }
            }

            // Realiza a troca (swap) de posição se encontrou um elemento menor que o atual
            if (menorIndice != i) {
                Veiculo temporario = ordenados.get(i);
                ordenados.set(i, ordenados.get(menorIndice));
                ordenados.set(menorIndice, temporario);
            }
        }

        return ordenados;
    }

    // Fábrica de Comparators: mapeia o texto do critério recebido para a lógica de ordenação da classe
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

        // Critério padrão (fallback): ordena por ID do veículo tratando possíveis IDs nulos
        return Comparator.comparingLong(v -> v.getIdVeiculo() == null ? 0L : v.getIdVeiculo());
    }

    // Normaliza Strings nulas para vazias e padroniza em maiúsculas para a ordenação ignorar o Case Sensitive
    private static String textoSeguro(String texto) {
        return texto == null ? "" : texto.toUpperCase();
    }

    // Converte o ano String para int de forma segura, jogando anos inválidos ou corrompidos para o início da lista (0)
    private static int anoSeguro(Veiculo veiculo) {
        try {
            return Integer.parseInt(veiculo.getFabricateYear());
        } catch (Exception e) {
            return 0;
        }
    }
}