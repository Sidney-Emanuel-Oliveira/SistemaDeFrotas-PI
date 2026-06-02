package br.com.utils;

import br.com.model.Veiculo;
import br.com.model.TipoDespesa;
import br.com.model.Movimentacao;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

public class GeradorCSV {

    // Gera um relatório CSV contendo todos os veículos
    public static void gerarRelatorioVeiculosCSV(List<Veiculo> veiculos, String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        // Cria a estrutura de pastas caso ela não exista
        File parentDir = arquivo.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            // Adiciona o BOM para evitar problemas de acentuação no Excel
            writer.write('\ufeff');

            // Escreve o cabeçalho do relatório
            writer.write("ID;Placa;Marca;Modelo;Ano;Status;Tipo");
            writer.newLine();

            // Escreve os dados de cada veículo
            for (Veiculo v : veiculos) {
                String linha = v.getIdVeiculo() + ";" +
                        v.getPlaca() + ";" +
                        v.getMarca() + ";" +
                        v.getModelo() + ";" +
                        v.getFabricateYear() + ";" +
                        (v.getAtivo() ? "Ativo" : "Inativo") + ";" +
                        (v.getTipo() != null ? v.getTipo() : "");
                writer.write(linha);
                writer.newLine();
            }
        }
    }

    // Gera um relatório CSV contendo todas as despesas
    public static void gerarRelatorioDespesasCSV(List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        File parentDir = arquivo.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            
            writer.write('\ufeff');

            writer.write("ID Movimentacao;ID Veiculo;ID Tipo Despesa;Descricao;Data;Valor;Tipo;Distancia Km;Litros Combustivel;Km por Litro");
            writer.newLine();

            for (Movimentacao m : movimentacoes) {
                String linha = m.getIdMovimentacao() + ";" +
                        m.getIdVeiculo() + ";" +
                        m.getIdTipoDespesa() + ";" +
                        "\"" + m.getDescricao() + "\";" +  
                        "\"" + m.getData() + "                    \";" +  
                        String.format("%.2f", m.getValor()).replace(".", ",") + ";" +  
                        "\"" + (m.getTipo() != null ? m.getTipo() : "") + "\"";  
                writer.write(linha);
                writer.newLine();
            }
        }
    }

    // Gera um relatório de despesas de um veículo específico
    public static void gerarRelatorioDespesasVeiculoCSV(Long idVeiculo, Veiculo veiculo, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        File parentDir = arquivo.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        // Calcula o total de despesas do veículo selecionado
        double totalDespesas = movimentacoes.stream()
                .filter(m -> m.getIdVeiculo().equals(idVeiculo))
                .mapToDouble(Movimentacao::getValor)
                .sum();

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            
            writer.write('\ufeff');

            writer.write("RELATORIO DE DESPESAS DO VEICULO");
            writer.newLine();
            writer.write("Data: " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.newLine();
            writer.newLine();

            if (veiculo != null) {
                writer.write("Veiculo: " + veiculo.getMarca() + " " + veiculo.getModelo());
                writer.newLine();
                writer.write("Placa: " + veiculo.getPlaca());
                writer.newLine();
                writer.write("Ano: " + veiculo.getFabricateYear());
                writer.newLine();
                writer.newLine();
            }

            writer.write("ID Movimentacao;Data                    ;Descricao;Tipo;Valor");
            writer.newLine();

            for (Movimentacao m : movimentacoes) {
                if (m.getIdVeiculo().equals(idVeiculo)) {
                    String linha = m.getIdMovimentacao() + ";" +
                            "\"" + m.getData() + "                    \";" +
                            "\"" + m.getDescricao() + "\";" +
                            "\"" + m.getTipo() + "\";" +
                            String.format("%.2f", m.getValor()).replace(".", ",");
                    writer.write(linha);
                    writer.newLine();
                }
            }

            writer.newLine();
            writer.write("TOTAL DESPESAS;" + String.format("%.2f", totalDespesas).replace(".", ","));
            writer.newLine();
        }
    }

    // Gera um relatório das despesas de um determinado mês
    public static void gerarRelatorioDespesasPorMesCSV(String mesAno, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        File parentDir = arquivo.getParentFile();

        // Exibe as informações do veículo no relatório
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        double totalDespesas = 0;

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {
            
            writer.write('\ufeff');

            writer.write("RELATORIO DE DESPESAS - " + mesAno);
            writer.newLine();
            writer.write("Data de Emissao: " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.newLine();
            writer.newLine();

            writer.write("ID Movimentacao;ID Veiculo;Data                    ;Descricao;Tipo;Valor");
            writer.newLine();

            for (Movimentacao m : movimentacoes) {
                // Verifica se a movimentação pertence ao mês e ano selecionados
                String[] dataParts = m.getData().split("/");
                String mesAnoMovimentacao = dataParts[1] + "/" + dataParts[2];
                if (mesAnoMovimentacao.equals(mesAno)) {
                    String linha = m.getIdMovimentacao() + ";" +
                            m.getIdVeiculo() + ";" +
                            "\"" + m.getData() + "                    \";" +
                            "\"" + m.getDescricao() + "\";" +
                            "\"" + m.getTipo() + "\";" +
                            String.format("%.2f", m.getValor()).replace(".", ",");
                    writer.write(linha);
                    writer.newLine();
                    totalDespesas += m.getValor();
                }
            }

            writer.newLine();
            writer.write("TOTAL DO MES;" + String.format("%.2f", totalDespesas).replace(".", ","));
            writer.newLine();
        }
    }

    // Gera um relatório dos gastos com combustível
    public static void gerarRelatorioCombustivelCSV(String mesAno, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        File parentDir = arquivo.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        double totalCombustivel = 0;

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {
            
            writer.write('\ufeff');

            writer.write("RELATORIO DE COMBUSTIVEL - " + mesAno);
            writer.newLine();
            writer.write("Data de Emissao: " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.newLine();
            writer.newLine();

            writer.write("ID Movimentacao;ID Veiculo;Data;Descricao;Valor");
            writer.newLine();

            for (Movimentacao m : movimentacoes) {
                // Considera apenas movimentações classificadas como combustível
                if (m.getTipo() != null && m.getTipo().equalsIgnoreCase("Combustivel")) {
                    String[] dataParts = m.getData().split("/");
                    String mesAnoMovimentacao = dataParts[1] + "/" + dataParts[2];
                    if (mesAnoMovimentacao.equals(mesAno)) {
                        String linha = m.getIdMovimentacao() + ";" +
                                m.getIdVeiculo() + ";" +
                                "\"" + m.getData() + "                    \";" +
                                "\"" + m.getDescricao() + "\";" +
                                String.format("%.2f", m.getValor()).replace(".", ",");
                        writer.write(linha);
                        writer.newLine();
                        totalCombustivel += m.getValor();
                    }
                }
            }

            writer.newLine();
            writer.write("TOTAL COMBUSTIVEL;" + String.format("%.2f", totalCombustivel).replace(".", ","));
            writer.newLine();
        }
    }

    // Gera um relatório dos pagamentos de IPVA
    public static void gerarRelatorioIPVACSV(String ano, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        File parentDir = arquivo.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        double totalIPVA = 0;

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            
            writer.write('\ufeff');

            writer.write("RELATORIO DE IPVA - ANO " + ano);
            writer.newLine();
            writer.write("Data de Emissao: " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.newLine();
            writer.newLine();

            writer.write("ID Movimentacao;ID Veiculo;Data                    ;Descricao;Valor");
            writer.newLine();

            for (Movimentacao m : movimentacoes) {
                // Considera apenas movimentações classificadas como IPVA
                if (m.getTipo() != null && m.getTipo().equalsIgnoreCase("IPVA")) {
                    String[] dataParts = m.getData().split("/");
                    String anoMovimentacao = dataParts[2];
                    if (anoMovimentacao.equals(ano)) {
                        String linha = m.getIdMovimentacao() + ";" +
                                m.getIdVeiculo() + ";" +
                                "\"" + m.getData() + "                    \";" +
                                "\"" + m.getDescricao() + "\";" +
                                String.format("%.2f", m.getValor()).replace(".", ",");
                        writer.write(linha);
                        writer.newLine();
                        totalIPVA += m.getValor();
                    }
                }
            }

            writer.newLine();
            writer.write("TOTAL IPVA;" + String.format("%.2f", totalIPVA).replace(".", ","));
            writer.newLine();
        }
    }

    // Gera um relatório das multas de um veículo em determinado ano
    public static void gerarRelatorioMultasCSV(Long idVeiculo, String ano, List<Movimentacao> movimentacoes, String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        File parentDir = arquivo.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        double totalMultas = 0;

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            writer.write('\ufeff');

            writer.write("RELATORIO DE MULTAS - ANO " + ano);
            writer.newLine();
            writer.write("Data de Emissao: " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.newLine();
            writer.newLine();

            writer.write("ID Movimentacao;Data                    ;Descricao;Valor");
            writer.newLine();

            for (Movimentacao m : movimentacoes) {
                // Filtra multas do veículo selecionado no ano informado
                if (m.getIdVeiculo().equals(idVeiculo) &&
                    m.getTipo() != null && m.getTipo().equalsIgnoreCase("Multa")) {
                    String[] dataParts = m.getData().split("/");
                    String anoMovimentacao = dataParts[2];
                    if (anoMovimentacao.equals(ano)) {
                        String linha = m.getIdMovimentacao() + ";" +
                                "\"" + m.getData() + "                    \";" +
                                "\"" + m.getDescricao() + "\";" +
                                String.format("%.2f", m.getValor()).replace(".", ",");
                        writer.write(linha);
                        writer.newLine();
                        totalMultas += m.getValor();
                    }
                }
            }

            writer.newLine();
            writer.write("TOTAL MULTAS;" + String.format("%.2f", totalMultas).replace(".", ","));
            writer.newLine();
        }
    }
}

