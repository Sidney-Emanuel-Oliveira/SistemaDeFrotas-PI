package br.com.utils;

import br.com.model.Veiculo;
import br.com.model.Movimentacao;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Classe utilitária para geração de relatórios em formato CSV.
 * Versão refatorada para evitar duplicação de código e garantir consistência dos dados.
 */
public class GeradorCSV {

    private static final String DELIMITADOR = ";";
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Método auxiliar genérico para escrever uma linha no CSV tratando caracteres especiais.
     */
    private static void escreverLinhaCSV(BufferedWriter writer, Object... valores) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            String valorStr = valores[i] != null ? valores[i].toString().trim() : "";

            // Tratamento de caracteres especiais: se contiver o delimitador ou aspas, envolve em aspas e escapa aspas internas
            if (valorStr.contains(DELIMITADOR) || valorStr.contains("\"") || valorStr.contains("\n")) {
                valorStr = "\"" + valorStr.replace("\"", "\"\"") + "\"";
            }

            sb.append(valorStr);
            if (i < valores.length - 1) {
                sb.append(DELIMITADOR);
            }
        }
        writer.write(sb.toString());
        writer.newLine();
    }

    private static BufferedWriter inicializarWriter(String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        File parentDir = arquivo.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(arquivo);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        BufferedWriter writer = new BufferedWriter(osw);

        // Escreve o BOM (Byte Order Mark) para que o Excel reconheça o UTF-8 corretamente
        writer.write('\ufeff');
        return writer;
    }

    public static void gerarRelatorioVeiculosCSV(List<Veiculo> veiculos, String caminhoArquivo) throws IOException {
        try (BufferedWriter writer = inicializarWriter(caminhoArquivo)) {
            escreverLinhaCSV(writer, "ID", "Placa", "Marca", "Modelo", "Ano", "Status", "Tipo");

            for (Veiculo v : veiculos) {
                escreverLinhaCSV(writer,
                        v.getIdVeiculo(),
                        v.getPlaca(),
                        v.getMarca(),
                        v.getModelo(),
                        v.getFabricateYear(),
                        (v.getAtivo() ? "Ativo" : "Inativo"),
                        (v.getTipo() != null ? v.getTipo() : "")
                );
            }
        }
    }

    // Gera CSV com relatório de despesas e consumo de combustível
    public static void gerarRelatorioDespesasCSV(List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        try (BufferedWriter writer = inicializarWriter(caminhoArquivo)) {
            escreverLinhaCSV(writer, "ID Movimentacao", "ID Veiculo", "ID Tipo Despesa", "Descricao", "Data", "Valor", "Tipo", "Distancia Km", "Litros Combustivel", "Km por Litro");

            for (Movimentacao m : movimentacoes) {
                escreverLinhaCSV(writer,
                        m.getIdMovimentacao(),
                        m.getIdVeiculo(),
                        m.getIdTipoDespesa(),
                        m.getDescricao(),
                        m.getData(),
                        String.format("%.2f", m.getValor()).replace(".", ","),
                        (m.getTipo() != null ? m.getTipo() : ""),
                        String.format("%.2f", m.getDistanciaPercorridaKm()).replace(".", ","),
                        String.format("%.2f", m.getLitrosCombustivel()).replace(".", ","),
                        String.format("%.2f", m.calcularConsumoMedioKmPorLitro()).replace(".", ",")
                );
            }
        }
    }

    public static void gerarRelatorioDespesasVeiculoCSV(Long idVeiculo, Veiculo veiculo, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        try (BufferedWriter writer = inicializarWriter(caminhoArquivo)) {
            escreverLinhaCSV(writer, "RELATORIO DE DESPESAS DO VEICULO");
            escreverLinhaCSV(writer, "Data de Emissao", LocalDate.now().format(FORMATO_DATA));
            writer.newLine();

            if (veiculo != null) {
                escreverLinhaCSV(writer, "Veiculo", veiculo.getMarca() + " " + veiculo.getModelo());
                escreverLinhaCSV(writer, "Placa", veiculo.getPlaca());
                escreverLinhaCSV(writer, "Ano", veiculo.getFabricateYear());
                writer.newLine();
            }

            escreverLinhaCSV(writer, "ID Movimentacao", "Data", "Descricao", "Tipo", "Valor");

            double totalDespesas = 0;
            for (Movimentacao m : movimentacoes) {
                if (m.getIdVeiculo().equals(idVeiculo)) {
                    escreverLinhaCSV(writer,
                            m.getIdMovimentacao(),
                            m.getData(),
                            m.getDescricao(),
                            m.getTipo(),
                            String.format("%.2f", m.getValor()).replace(".", ",")
                    );
                    totalDespesas += m.getValor();
                }
            }

            writer.newLine();
            escreverLinhaCSV(writer, "TOTAL DESPESAS", String.format("%.2f", totalDespesas).replace(".", ","));
        }
    }

    public static void gerarRelatorioDespesasPorMesCSV(String mesAno, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        try (BufferedWriter writer = inicializarWriter(caminhoArquivo)) {
            escreverLinhaCSV(writer, "RELATORIO DE DESPESAS - " + mesAno);
            escreverLinhaCSV(writer, "Data de Emissao", LocalDate.now().format(FORMATO_DATA));
            writer.newLine();

            escreverLinhaCSV(writer, "ID Movimentacao", "ID Veiculo", "Data", "Descricao", "Tipo", "Valor");

            double totalDespesas = 0;
            for (Movimentacao m : movimentacoes) {
                if (correspondeMesAno(m.getData(), mesAno)) {
                    escreverLinhaCSV(writer,
                            m.getIdMovimentacao(),
                            m.getIdVeiculo(),
                            m.getData(),
                            m.getDescricao(),
                            m.getTipo(),
                            String.format("%.2f", m.getValor()).replace(".", ",")
                    );
                    totalDespesas += m.getValor();
                }
            }

            writer.newLine();
            escreverLinhaCSV(writer, "TOTAL DO MES", String.format("%.2f", totalDespesas).replace(".", ","));
        }
    }

    public static void gerarRelatorioCombustivelCSV(String mesAno, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        try (BufferedWriter writer = inicializarWriter(caminhoArquivo)) {
            escreverLinhaCSV(writer, "RELATORIO DE COMBUSTIVEL - " + mesAno);
            escreverLinhaCSV(writer, "Data de Emissao", LocalDate.now().format(FORMATO_DATA));
            writer.newLine();

            escreverLinhaCSV(writer, "ID Movimentacao", "ID Veiculo", "Data", "Descricao", "Valor", "Distancia Km", "Litros", "Consumo (Km/L)");

            double totalCombustivel = 0;
            for (Movimentacao m : movimentacoes) {
                if (isTipo(m, "Combustivel") && correspondeMesAno(m.getData(), mesAno)) {
                    escreverLinhaCSV(writer,
                            m.getIdMovimentacao(),
                            m.getIdVeiculo(),
                            m.getData(),
                            m.getDescricao(),
                            String.format("%.2f", m.getValor()).replace(".", ","),
                            String.format("%.2f", m.getDistanciaPercorridaKm()).replace(".", ","),
                            String.format("%.2f", m.getLitrosCombustivel()).replace(".", ","),
                            String.format("%.2f", m.calcularConsumoMedioKmPorLitro()).replace(".", ",")
                    );
                    totalCombustivel += m.getValor();
                }
            }

            writer.newLine();
            escreverLinhaCSV(writer, "TOTAL COMBUSTIVEL", String.format("%.2f", totalCombustivel).replace(".", ","));
        }
    }

    public static void gerarRelatorioIPVACSV(String ano, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        try (BufferedWriter writer = inicializarWriter(caminhoArquivo)) {
            escreverLinhaCSV(writer, "RELATORIO DE IPVA - ANO " + ano);
            escreverLinhaCSV(writer, "Data de Emissao", LocalDate.now().format(FORMATO_DATA));
            writer.newLine();

            escreverLinhaCSV(writer, "ID Movimentacao", "ID Veiculo", "Data", "Descricao", "Valor");

            double totalIPVA = 0;
            for (Movimentacao m : movimentacoes) {
                if (isTipo(m, "IPVA") && correspondeAno(m.getData(), ano)) {
                    escreverLinhaCSV(writer,
                            m.getIdMovimentacao(),
                            m.getIdVeiculo(),
                            m.getData(),
                            m.getDescricao(),
                            String.format("%.2f", m.getValor()).replace(".", ",")
                    );
                    totalIPVA += m.getValor();
                }
            }

            writer.newLine();
            escreverLinhaCSV(writer, "TOTAL IPVA", String.format("%.2f", totalIPVA).replace(".", ","));
        }
    }

    public static void gerarRelatorioMultasCSV(Long idVeiculo, String ano, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        try (BufferedWriter writer = inicializarWriter(caminhoArquivo)) {
            escreverLinhaCSV(writer, "RELATORIO DE MULTAS - ANO " + ano);
            escreverLinhaCSV(writer, "Data de Emissao", LocalDate.now().format(FORMATO_DATA));
            writer.newLine();

            escreverLinhaCSV(writer, "ID Movimentacao", "Data", "Descricao", "Valor");

            double totalMultas = 0;
            for (Movimentacao m : movimentacoes) {
                if (m.getIdVeiculo().equals(idVeiculo) && isTipo(m, "Multa") && correspondeAno(m.getData(), ano)) {
                    escreverLinhaCSV(writer,
                            m.getIdMovimentacao(),
                            m.getData(),
                            m.getDescricao(),
                            String.format("%.2f", m.getValor()).replace(".", ",")
                    );
                    totalMultas += m.getValor();
                }
            }

            writer.newLine();
            escreverLinhaCSV(writer, "TOTAL MULTAS", String.format("%.2f", totalMultas).replace(".", ","));
        }
    }

    // Métodos auxiliares de filtragem para manter o código limpo

    private static boolean correspondeMesAno(String data, String mesAno) {
        if (data == null || !data.contains("/")) return false;
        String[] partes = data.split("/");
        if (partes.length < 3) return false;
        return (partes[1] + "/" + partes[2]).equals(mesAno);
    }

    private static boolean correspondeAno(String data, String ano) {
        if (data == null || !data.contains("/")) return false;
        String[] partes = data.split("/");
        if (partes.length < 3) return false;
        return partes[2].equals(ano);
    }

    private static boolean isTipo(Movimentacao m, String tipoBusca) {
        return m.getTipo() != null && m.getTipo().equalsIgnoreCase(tipoBusca);
    }
}
