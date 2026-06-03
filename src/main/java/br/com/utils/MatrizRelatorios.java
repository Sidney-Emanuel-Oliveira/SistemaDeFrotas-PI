package br.com.utils;

import br.com.model.Movimentacao;
import br.com.model.Veiculo;
import br.com.model.TipoDespesa;

import java.util.*;
import java.util.stream.Collectors;

public class MatrizRelatorios {

    
    
    public static double[][] gerarMatrizA(List<Veiculo> veiculos, List<Movimentacao> movimentacoes, List<String> meses) {
        int m = veiculos.size();
        int n = meses.size();
        double[][] matrizA = new double[m][n];

        
        List<Movimentacao> combustiveis = movimentacoes.stream()
                .filter(mov -> mov.getTipo() != null &&
                        (mov.getTipo().equalsIgnoreCase("Combustível") ||
                         mov.getTipo().equalsIgnoreCase("Combustivel")))
                .toList();

        for (int i = 0; i < m; i++) {
            Veiculo veiculo = veiculos.get(i);
            for (int j = 0; j < n; j++) {
                String mes = meses.get(j);

                
                long quantidade = combustiveis.stream()
                        .filter(mov -> mov.getIdVeiculo().equals(veiculo.getIdVeiculo()))
                        .filter(mov -> {
                            String[] dataParts = mov.getData().split("/");
                            String mesAnoMov = dataParts[1] + "/" + dataParts[2];
                            return mesAnoMov.equals(mes);
                        })
                        .count();

                matrizA[i][j] = quantidade;
            }
        }

        return matrizA;
    }

    
    
    public static double[][] gerarMatrizB(List<String> meses, List<String> marcas, List<Veiculo> veiculos, List<Movimentacao> movimentacoes) {
        int n = meses.size();
        int p = marcas.size();
        double[][] matrizB = new double[n][p];

        
        List<Movimentacao> combustiveis = movimentacoes.stream()
                .filter(mov -> mov.getTipo() != null &&
                        (mov.getTipo().equalsIgnoreCase("Combustível") ||
                         mov.getTipo().equalsIgnoreCase("Combustivel")))
                .toList();

        
        Map<Long, String> veiculoMarcaMap = new HashMap<>();
        for (Veiculo v : veiculos) {
            veiculoMarcaMap.put(v.getIdVeiculo(), v.getMarca());
        }

        for (int i = 0; i < n; i++) {
            String mes = meses.get(i);
            for (int j = 0; j < p; j++) {
                String marca = marcas.get(j);

                
                List<Movimentacao> movsMesMarca = combustiveis.stream()
                        .filter(mov -> {
                            String[] dataParts = mov.getData().split("/");
                            String mesAnoMov = dataParts[1] + "/" + dataParts[2];
                            return mesAnoMov.equals(mes);
                        })
                        .filter(mov -> {
                            String marcaVeiculo = veiculoMarcaMap.get(mov.getIdVeiculo());
                            return marcaVeiculo != null && marcaVeiculo.equals(marca);
                        })
                        .collect(Collectors.toList());

                
                if (!movsMesMarca.isEmpty()) {
                    double soma = movsMesMarca.stream().mapToDouble(Movimentacao::getValor).sum();
                    double custoMedio = soma / movsMesMarca.size();
                    matrizB[i][j] = custoMedio;
                } else {
                    matrizB[i][j] = 0.0;
                }
            }
        }

        return matrizB;
    }

    
    public static double[][] gerarMatrizC(double[][] matrizA, double[][] matrizB) {

        
        if (matrizA == null || matrizA.length == 0 || matrizA[0].length == 0) {
            return new double[0][0];
        }
        if (matrizB == null || matrizB.length == 0 || matrizB[0].length == 0) {
            return new double[0][0];
        }

        int m = matrizA.length;
        int n = matrizA[0].length;
        int p = matrizB[0].length;

        double[][] matrizC = new double[m][p];

        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                double soma = 0;
                for (int k = 0; k < n; k++) {
                    soma += matrizA[i][k] * matrizB[k][j];
                }
                matrizC[i][j] = soma;
            }
        }

        return matrizC;
    }

    
    public static double calcularTotalGeral(double[][] matriz) {
        double total = 0;
        for (double[] linha : matriz) {
            for (double valor : linha) {
                total += valor;
            }
        }
        return total;
    }

    
    public static String formatarMatriz(double[][] matriz, List<String> rotulos, List<String> colunas, String titulo) {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("  ").append(titulo).append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");

        
        if (matriz == null || matriz.length == 0 || rotulos.isEmpty() || colunas.isEmpty()) {
            sb.append("Nenhum dado disponível para o período selecionado.\n");
            return sb.toString();
        }

        
        sb.append(String.format("%-20s", ""));
        for (String coluna : colunas) {
            sb.append(String.format("│ %-12s ", coluna.length() > 12 ? coluna.substring(0, 12) : coluna));
        }
        sb.append("\n");

        sb.append("─".repeat(20));
        for (int i = 0; i < colunas.size(); i++) {
            sb.append("┼──────────────");
        }
        sb.append("\n");
        
        for (int i = 0; i < matriz.length; i++) {
            String rotulo = rotulos.get(i);
            sb.append(String.format("%-20s", rotulo.length() > 20 ? rotulo.substring(0, 17) + "..." : rotulo));

            for (int j = 0; j < matriz[i].length; j++) {
                sb.append(String.format("│ %12.2f ", matriz[i][j]));
            }
            sb.append("\n");
        }

        
        sb.append("─".repeat(20));
        for (int i = 0; i < colunas.size(); i++) {
            sb.append("┴──────────────");
        }
        sb.append("\n");

        
        double total = calcularTotalGeral(matriz);
        sb.append(String.format("\nTOTAL GERAL: R$ %.2f\n", total));

        return sb.toString();
    }

    
    public static List<String> extrairMarcas(List<Veiculo> veiculos) {
        return veiculos.stream()
                .map(Veiculo::getMarca)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    
    public static List<String> extrairMeses(List<Movimentacao> movimentacoes) {
        return movimentacoes.stream()
                .map(mov -> {
                    String[] dataParts = mov.getData().split("/");
                    return dataParts[1] + "/" + dataParts[2];
                })
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    
    public static String formatarRotuloVeiculo(Veiculo veiculo) {
        return veiculo.getPlaca() + " - " + veiculo.getMarca() + " " + veiculo.getModelo();
    }

    
    public static List<String> extrairMesesPorPeriodo(List<Movimentacao> movimentacoes,
                                                       int mesInicial, int anoInicial,
                                                       int mesFinal, int anoFinal) {
        return movimentacoes.stream()
                .map(mov -> {
                    String[] dataParts = mov.getData().split("/");
                    return dataParts[1] + "/" + dataParts[2];
                })
                .distinct()
                .filter(mesAno -> {
                    String[] parts = mesAno.split("/");
                    int mes = Integer.parseInt(parts[0]);
                    int ano = Integer.parseInt(parts[1]);

                    
                    int dataAtual = ano * 100 + mes;
                    int dataInicial = anoInicial * 100 + mesInicial;
                    int dataFinal = anoFinal * 100 + mesFinal;

                    return dataAtual >= dataInicial && dataAtual <= dataFinal;
                })
                .sorted((a, b) -> {

                    
                    String[] partsA = a.split("/");
                    String[] partsB = b.split("/");

                    int anoA = Integer.parseInt(partsA[1]);
                    int anoB = Integer.parseInt(partsB[1]);
                    int mesA = Integer.parseInt(partsA[0]);
                    int mesB = Integer.parseInt(partsB[0]);

                    if (anoA != anoB) {
                        return Integer.compare(anoA, anoB);
                    }
                    return Integer.compare(mesA, mesB);
                })
                .collect(Collectors.toList());
    }

    
    public static boolean estaNoPeriodo(Movimentacao movimentacao,
                                        int mesInicial, int anoInicial,
                                        int mesFinal, int anoFinal) {
        String[] dataParts = movimentacao.getData().split("/");
        int mes = Integer.parseInt(dataParts[1]);
        int ano = Integer.parseInt(dataParts[2]);

        int dataAtual = ano * 100 + mes;
        int dataInicial = anoInicial * 100 + mesInicial;
        int dataFinal = anoFinal * 100 + mesFinal;

        return dataAtual >= dataInicial && dataAtual <= dataFinal;
    }
}