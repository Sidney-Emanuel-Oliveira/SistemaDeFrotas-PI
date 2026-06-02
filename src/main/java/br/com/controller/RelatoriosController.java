package br.com.controller;

import br.com.dao.MovimentacaoDAO;
import br.com.dao.VeiculoDAO;
import br.com.model.Movimentacao;
import br.com.model.Veiculo;
import br.com.utils.Validacoes;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RelatoriosController {
    private MovimentacaoDAO movimentacaoDAO;
    private VeiculoDAO veiculoDAO;

    // Inicializa os DAOs utilizados nos relatórios
    public RelatoriosController() {
        this.movimentacaoDAO = new MovimentacaoDAO();
        this.veiculoDAO = new VeiculoDAO();
    }

    // Retorna todas as despesas de um veículo
    public List<Movimentacao> obterDespesasVeiculo(Long idVeiculo) throws IOException {
        return movimentacaoDAO.obterPorVeiculo(idVeiculo);
    }

    // Calcula o total de despesas de um veículo
    public double obterTotalDespesasVeiculo(Long idVeiculo) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterPorVeiculo(idVeiculo);
        return calcularTotalRecursivo(movimentacoes, 0);
    }

    // Calcula o total de despesas de um mês
    public double obterTotalDespesasMes(String mesAno) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        List<Movimentacao> filtradas = movimentacoes.stream()
                .filter(m -> obterMesAno(m).equals(mesAno))
                .toList();
        return calcularTotalRecursivo(filtradas, 0);
    }

    // Calcula o total gasto com combustível em um mês
    public double obterTotalCombustivelMes(String mesAno) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        List<Movimentacao> filtradas = movimentacoes.stream()
                .filter(this::isCombustivel)
                .filter(m -> obterMesAno(m).equals(mesAno))
                .toList();
        return calcularTotalRecursivo(filtradas, 0);
    }

    // Calcula o total gasto com IPVA em um ano
    public double obterTotalIPVAAno(String ano) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        List<Movimentacao> filtradas = movimentacoes.stream()
                .filter(this::isIPVA)
                .filter(m -> obterAno(m).equals(ano))
                .toList();
        return calcularTotalRecursivo(filtradas, 0);
    }

    // Retorna todos os veículos inativos
    public List<Veiculo> obterVeiculosInativos() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        return veiculos.stream()
                .filter(v -> !v.getAtivo())
                .toList();
    }

    // Calcula o total gasto com multas por veículo em um ano
    public double obterTotalMultasVeiculoAno(Long idVeiculo, String ano) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterPorVeiculo(idVeiculo);
        List<Movimentacao> filtradas = movimentacoes.stream()
                .filter(this::isMulta)
                .filter(m -> obterAno(m).equals(ano))
                .toList();
        return calcularTotalRecursivo(filtradas, 0);
    }

    // Retorna as multas de um veículo em determinado ano
    public List<Movimentacao> obterMultasVeiculoAno(Long idVeiculo, String ano) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterPorVeiculo(idVeiculo);
        return movimentacoes.stream()
                .filter(this::isMulta)
                .filter(m -> obterAno(m).equals(ano))
                .toList();
    }

    // Agrupa as despesas por veículo em um mês
    public Map<Long, Double> obterDespesasPorVeiculoMes(String mesAno) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        Map<Long, Double> despesasPorVeiculo = new HashMap<>();

        for (Movimentacao m : movimentacoes) {
            if (obterMesAno(m).equals(mesAno)) {
                Long idVeiculo = m.getIdVeiculo();
                despesasPorVeiculo.put(idVeiculo,
                    despesasPorVeiculo.getOrDefault(idVeiculo, 0.0) + m.getValor());
            }
        }

        return despesasPorVeiculo;
    }

    // Retorna todas as movimentações cadastradas
    public List<Movimentacao> obterTodasMovimentacoes() throws IOException {
        return movimentacaoDAO.obterTodos();
    }

    // Retorna todos os veículos cadastrados
    public List<Veiculo> obterTodosVeiculos() throws IOException {
        return veiculoDAO.obterTodos();
    }

    // Gera a Matriz A de abastecimentos por veículo e mês
    public String gerarRelatorioMatrizA() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMeses(movimentacoes);

        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoes, meses);
        
        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        return br.com.utils.MatrizRelatorios.formatarMatriz(
                matrizA,
                rotulosVeiculos,
                meses,
                "MATRIZ A - Quantidade de Abastecimentos por Veículo/Mês"
        );
    }

    // Gera a Matriz B de custo médio por abastecimento e marca
    public String gerarRelatorioMatrizB() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMeses(movimentacoes);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);
        
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoes);

        return br.com.utils.MatrizRelatorios.formatarMatriz(
                matrizB,
                meses,
                marcas,
                "MATRIZ B - Custo Médio por Abastecimento/Marca (R$)"
        );
    }

    // Gera a Matriz C resultante da multiplicação A × B
    public String gerarRelatorioMatrizC() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMeses(movimentacoes);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoes, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoes);

        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);

        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        return br.com.utils.MatrizRelatorios.formatarMatriz(
                matrizC,
                rotulosVeiculos,
                marcas,
                "MATRIZ C - Gasto Total Estimado com Combustível por Veículo/Marca (R$)"
        );
    }

    // Gera o relatório completo contendo as matrizes A, B e C
    public String gerarRelatorioMatrizCompleto() throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO COMPLETO - ANÁLISE MATRICIAL DE CUSTOS COM COMBUSTÍVEL\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        sb.append("DESCRIÇÃO:\n");
        sb.append("- Matriz A: Quantidade de abastecimentos por Veículo/Mês (m x n)\n");
        sb.append("- Matriz B: Custo médio por abastecimento/Marca (n x p)\n");
        sb.append("- Matriz C: Gasto Total Estimado = A × B (m x p)\n\n");

        sb.append(gerarRelatorioMatrizA()).append("\n\n");
        sb.append(gerarRelatorioMatrizB()).append("\n\n");
        sb.append(gerarRelatorioMatrizC()).append("\n\n");

        return sb.toString();
    }

    // Gera a Matriz A para um período específico
    public String gerarRelatorioMatrizAComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ A - Quantidade de Abastecimentos por Veículo/Mês (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);

        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }
        
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());
        
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        
        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());
        
        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizA, rotulosVeiculos, meses, titulo);
    }

    // Gera a Matriz B para um período específico
    public String gerarRelatorioMatrizBComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ B - Custo Médio por Abastecimento/Marca (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);
        
        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());
        
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizB, meses, marcas, titulo);
    }

    // Gera a Matriz C para um período específico
    public String gerarRelatorioMatrizCComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ C - Gasto Total Estimado com Combustível por Veículo/Marca (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);
        
        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());

        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);

        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizC, rotulosVeiculos, marcas, titulo);
    }

    // Gera o relatório matricial completo para um período
    public String gerarRelatorioMatrizCompletoComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append(String.format("  RELATÓRIO COMPLETO - ANÁLISE MATRICIAL (%02d/%d a %02d/%d)\n",
                mesInicial, anoInicial, mesFinal, anoFinal));
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        sb.append("DESCRIÇÃO:\n");
        sb.append("- Matriz A: Quantidade de abastecimentos por Veículo/Mês (m x n)\n");
        sb.append("- Matriz B: Custo médio por abastecimento/Marca (n x p)\n");
        sb.append("- Matriz C: Gasto Total Estimado = A × B (m x p)\n\n");

        sb.append(gerarRelatorioMatrizAComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal)).append("\n\n");
        sb.append(gerarRelatorioMatrizBComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal)).append("\n\n");
        sb.append(gerarRelatorioMatrizCComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal)).append("\n\n");

        return sb.toString();
    }

    // Gera a Matriz A filtrando por período e veículo
    public String gerarRelatorioMatrizAComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal, Long idVeiculo) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        
        if (idVeiculo != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(idVeiculo))
                    .collect(java.util.stream.Collectors.toList());
        }

        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ A - Quantidade de Abastecimentos por Veículo/Mês (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);

        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }
        
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());

        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        
        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizA, rotulosVeiculos, meses, titulo);
    }

    // Gera a Matriz B filtrando por período e veículo
    public String gerarRelatorioMatrizBComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal, Long idVeiculo) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();

        if (idVeiculo != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(idVeiculo))
                    .collect(java.util.stream.Collectors.toList());
        }

        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ B - Custo Médio por Abastecimento/Marca (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);

        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());

        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizB, meses, marcas, titulo);
    }

    // Gera a Matriz C filtrando por período e veículo
    public String gerarRelatorioMatrizCComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal, Long idVeiculo) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();

        if (idVeiculo != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(idVeiculo))
                    .collect(java.util.stream.Collectors.toList());
        }

        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ C - Gasto Total Estimado com Combustível por Veículo/Marca (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);
        
        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());
        
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);
        
        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizC, rotulosVeiculos, marcas, titulo);
    }

    // Gera o relatório matricial completo filtrando por período e veículo
    public String gerarRelatorioMatrizCompletoComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal, Long idVeiculo) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append(String.format("  RELATÓRIO COMPLETO - ANÁLISE MATRICIAL (%02d/%d a %02d/%d)\n",
                mesInicial, anoInicial, mesFinal, anoFinal));
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        sb.append("DESCRIÇÃO:\n");
        sb.append("- Matriz A: Quantidade de abastecimentos por Veículo/Mês (m x n)\n");
        sb.append("- Matriz B: Custo médio por abastecimento/Marca (n x p)\n");
        sb.append("- Matriz C: Gasto Total Estimado = A × B (m x p)\n\n");

        sb.append(gerarRelatorioMatrizAComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal, idVeiculo)).append("\n\n");
        sb.append(gerarRelatorioMatrizBComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal, idVeiculo)).append("\n\n");
        sb.append(gerarRelatorioMatrizCComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal, idVeiculo)).append("\n\n");

        return sb.toString();
    }

    // Soma recursivamente os valores de uma lista de movimentações
    public double calcularTotalRecursivo(List<Movimentacao> movimentacoes, int indice) {
        if (movimentacoes == null || indice >= movimentacoes.size()) {
            return 0.0;
        }
        return movimentacoes.get(indice).getValor() + calcularTotalRecursivo(movimentacoes, indice + 1);
    }

    // Calcula a média de despesas por categoria de veículo
    public Map<String, Double> obterMediaDespesasPorCategoriaVeiculo() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        Map<String, Double> totais = new LinkedHashMap<>();
        Map<String, Integer> quantidades = new LinkedHashMap<>();

        for (Veiculo veiculo : veiculos) {
            String categoria = veiculo.getTipo();
            for (Movimentacao movimentacao : movimentacoes) {
                if (movimentacao.getIdVeiculo().equals(veiculo.getIdVeiculo())) {
                    totais.put(categoria, totais.getOrDefault(categoria, 0.0) + movimentacao.getValor());
                    quantidades.put(categoria, quantidades.getOrDefault(categoria, 0) + 1);
                }
            }
        }

        Map<String, Double> medias = new LinkedHashMap<>();
        for (String categoria : totais.keySet()) {
            int qtd = quantidades.getOrDefault(categoria, 0);
            medias.put(categoria, qtd > 0 ? totais.get(categoria) / qtd : 0.0);
        }
        return medias;
    }

    // Calcula indicadores de consumo por veículo
    public Map<Long, ConsumoVeiculo> obterConsumoMedioPorVeiculo(String mesAno) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        Map<Long, ConsumoVeiculo> resultado = new LinkedHashMap<>();

        for (Movimentacao movimentacao : movimentacoes) {
            if (!isCombustivel(movimentacao) || !movimentacao.possuiDadosConsumo()) {
                continue;
            }
            if (mesAno != null && !mesAno.isBlank() && !obterMesAno(movimentacao).equals(mesAno)) {
                continue;
            }

            ConsumoVeiculo consumo = resultado.getOrDefault(movimentacao.getIdVeiculo(), new ConsumoVeiculo(movimentacao.getIdVeiculo()));
            consumo.distanciaKm += movimentacao.getDistanciaPercorridaKm();
            consumo.litros += movimentacao.getLitrosCombustivel();
            consumo.custoCombustivel += movimentacao.getValor();
            consumo.quantidadeRegistros++;
            resultado.put(movimentacao.getIdVeiculo(), consumo);
        }

        return resultado;
    }

    // Calcula o custo médio anual de IPVA
    public double obterCustoMedioIPVAAno(String ano) throws IOException {
        List<Movimentacao> ipvas = movimentacaoDAO.obterTodos().stream()
                .filter(this::isIPVA)
                .filter(m -> obterAno(m).equals(ano))
                .toList();

        if (ipvas.isEmpty()) {
            return 0.0;
        }
        return calcularTotalRecursivo(ipvas, 0) / ipvas.size();
    }

    // Identifica os veículos com maior e menor custo por quilômetro
    public Map<String, ConsumoVeiculo> identificarMaiorMenorCustoConsumo(String mesAno) throws IOException {
        Map<Long, ConsumoVeiculo> consumos = obterConsumoMedioPorVeiculo(mesAno);
        Map<String, ConsumoVeiculo> resultado = new LinkedHashMap<>();

        ConsumoVeiculo maior = null;
        ConsumoVeiculo menor = null;
        for (ConsumoVeiculo consumo : consumos.values()) {
            if (maior == null || consumo.getCustoPorKm() > maior.getCustoPorKm()) {
                maior = consumo;
            }
            if (menor == null || consumo.getCustoPorKm() < menor.getCustoPorKm()) {
                menor = consumo;
            }
        }

        if (maior != null) {
            resultado.put("Maior custo de consumo", maior);
        }
        if (menor != null) {
            resultado.put("Menor custo de consumo", menor);
        }
        return resultado;
    }

    // Extrai o mês e ano da data da movimentação
    public String obterMesAno(Movimentacao movimentacao) {
        String[] dataParts = movimentacao.getData().split("/");
        if (dataParts.length >= 3) {
            return dataParts[1] + "/" + dataParts[2];
        }
        return "";
    }

    // Extrai o ano da data da movimentação
    public String obterAno(Movimentacao movimentacao) {
        String[] dataParts = movimentacao.getData().split("/");
        if (dataParts.length >= 3) {
            return dataParts[2];
        }
        return "";
    }

    // Verifica se a movimentação é do tipo combustível
    public boolean isCombustivel(Movimentacao movimentacao) {
        return movimentacao != null && (Long.valueOf(1L).equals(movimentacao.getIdTipoDespesa()) || normalizar(movimentacao.getTipo()).contains("COMBUSTIVEL"));
    }

    // Verifica se a movimentação é do tipo IPVA
    public boolean isIPVA(Movimentacao movimentacao) {
        return movimentacao != null && (Long.valueOf(2L).equals(movimentacao.getIdTipoDespesa()) || normalizar(movimentacao.getTipo()).contains("IPVA"));
    }

    // Verifica se a movimentação é do tipo multa
    public boolean isMulta(Movimentacao movimentacao) {
        return movimentacao != null && (Long.valueOf(6L).equals(movimentacao.getIdTipoDespesa()) || normalizar(movimentacao.getTipo()).contains("MULTA"));
    }

    // Remove acentos e padroniza o texto para comparação
    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase();
    }

    // Armazena informações de consumo de um veículo
    public static class ConsumoVeiculo {
        private final Long idVeiculo;
        private double distanciaKm;
        private double litros;
        private double custoCombustivel;
        private int quantidadeRegistros;

        public ConsumoVeiculo(Long idVeiculo) {
            this.idVeiculo = idVeiculo;
        }

        public Long getIdVeiculo() {
            return idVeiculo;
        }

        public double getDistanciaKm() {
            return distanciaKm;
        }

        public double getLitros() {
            return litros;
        }

        public double getCustoCombustivel() {
            return custoCombustivel;
        }

        public int getQuantidadeRegistros() {
            return quantidadeRegistros;
        }

        // Calcula a média de quilômetros por litro
        public double getKmPorLitro() {
            return litros > 0 ? distanciaKm / litros : 0.0;
        }

        //  Calcula o custo médio por quilômetro rodado
        public double getCustoPorKm() {
            return distanciaKm > 0 ? custoCombustivel / distanciaKm : 0.0;
        }
    }

}
