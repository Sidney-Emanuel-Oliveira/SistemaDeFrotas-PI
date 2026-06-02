package br.com.dao;

import br.com.model.Movimentacao;
import br.com.database.MySQLSincronizador;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    // Caminho do arquivo utilizado para persistência das movimentações
    private static final String ARQUIVO_MOVIMENTACOES = "dados/movimentacoes.txt";

    public MovimentacaoDAO() {
        criarDiretorioSeNaoExistir();
        criarArquivoSeNaoExistir();
    }

    // Cria o diretório de armazenamento caso ele não exista
    private void criarDiretorioSeNaoExistir() {
        File dir = new File("dados");

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Cria o arquivo de movimentações caso ele não exista
    private void criarArquivoSeNaoExistir() {
        File arquivo = new File(ARQUIVO_MOVIMENTACOES);

        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Salva uma movimentação ou atualiza uma existente
    public void salvar(Movimentacao movimentacao) throws IOException {
        List<Movimentacao> movimentacoes = obterTodos();

        boolean encontrado = false;

        for (int i = 0; i < movimentacoes.size(); i++) {
            if (movimentacoes.get(i).getIdMovimentacao().equals(movimentacao.getIdMovimentacao())) {
                movimentacoes.set(i, movimentacao);
                encontrado = true;
                break;
            }
        }

        // Adiciona a movimentação caso ela ainda não exista
        if (!encontrado) {
            movimentacoes.add(movimentacao);
        }

        escreverArquivo(movimentacoes);

        // Mantém os dados sincronizados com o banco MySQL
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    // Salva uma lista completa de movimentações
    public void salvarTodos(List<Movimentacao> movimentacoes) throws IOException {
        escreverArquivo(movimentacoes);
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    // Reescreve o arquivo com todas as movimentações informadas
    private void escreverArquivo(List<Movimentacao> movimentacoes) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_MOVIMENTACOES))) {

            for (Movimentacao m : movimentacoes) {
                String linha = formatarMovimentacao(m);

                writer.write(linha);
                writer.newLine();
            }
        }
    }

    // Converte um objeto Movimentacao para uma linha de texto
    private String formatarMovimentacao(Movimentacao m) {
        return m.getIdMovimentacao() + ";" +
                m.getIdVeiculo() + ";" +
                m.getIdTipoDespesa() + ";" +
                m.getDescricao() + ";" +
                m.getData() + ";" +
                m.getValor() + ";" +
                (m.getTipo() != null ? m.getTipo() : "") + ";" +
                m.getDistanciaPercorridaKm() + ";" +
                m.getLitrosCombustivel();
    }

    // Retorna todas as movimentações cadastradas
    public List<Movimentacao> obterTodos() throws IOException {
        List<Movimentacao> movimentacoes = new ArrayList<>();

        if (!Files.exists(Paths.get(ARQUIVO_MOVIMENTACOES))) {
            return movimentacoes;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_MOVIMENTACOES))) {

            String linha;

            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {

                    Movimentacao m = parseMovimentacao(linha);

                    if (m != null) {
                        movimentacoes.add(m);
                    }
                }
            }
        }

        return movimentacoes;
    }

    // Converte uma linha do arquivo em um objeto Movimentacao
    private Movimentacao parseMovimentacao(String linha) {
        try {
            String[] partes = linha.split(";");

            if (partes.length >= 7) {
                Long id = Long.parseLong(partes[0]);
                Long idVeiculo = Long.parseLong(partes[1]);
                Long idTipoDespesa = Long.parseLong(partes[2]);

                String descricao = partes[3];
                String data = partes[4];
                double valor = Double.parseDouble(partes[5]);
                String tipo = partes[6];

                double distanciaPercorridaKm =
                        partes.length > 7 && !partes[7].isBlank()
                                ? Double.parseDouble(partes[7])
                                : 0.0;

                double litrosCombustivel =
                        partes.length > 8 && !partes[8].isBlank()
                                ? Double.parseDouble(partes[8])
                                : 0.0;

                return new Movimentacao(
                        id,
                        idVeiculo,
                        idTipoDespesa,
                        descricao,
                        data,
                        valor,
                        tipo,
                        distanciaPercorridaKm,
                        litrosCombustivel
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Busca uma movimentação pelo seu identificador
    public Movimentacao obterPorId(Long id) throws IOException {
        List<Movimentacao> movimentacoes = obterTodos();

        for (Movimentacao m : movimentacoes) {
            if (m.getIdMovimentacao().equals(id)) {
                return m;
            }
        }

        return null;
    }

    // Retorna todas as movimentações associadas a um veículo
    public List<Movimentacao> obterPorVeiculo(Long idVeiculo) throws IOException {
        List<Movimentacao> movimentacoes = obterTodos();
        List<Movimentacao> resultado = new ArrayList<>();

        for (Movimentacao m : movimentacoes) {
            if (m.getIdVeiculo().equals(idVeiculo)) {
                resultado.add(m);
            }
        }

        return resultado;
    }

    // Retorna movimentações de um determinado tipo
    public List<Movimentacao> obterPorTipo(String tipo) throws IOException {
        List<Movimentacao> movimentacoes = obterTodos();
        List<Movimentacao> resultado = new ArrayList<>();

        for (Movimentacao m : movimentacoes) {
            if (m.getTipo() != null && m.getTipo().equalsIgnoreCase(tipo)) {
                resultado.add(m);
            }
        }

        return resultado;
    }

    // Remove uma movimentação pelo identificador
    public void deletar(Long id) throws IOException {
        List<Movimentacao> movimentacoes = obterTodos();

        movimentacoes.removeIf(m -> m.getIdMovimentacao().equals(id));

        escreverArquivo(movimentacoes);

        // Atualiza a base MySQL após exclusão
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    // Gera o próximo ID disponível para cadastro
    public Long obterProximoId() throws IOException {
        List<Movimentacao> movimentacoes = obterTodos();

        if (movimentacoes.isEmpty()) {
            return 1L;
        }

        return movimentacoes.stream()
                .mapToLong(Movimentacao::getIdMovimentacao)
                .max()
                .orElse(0L) + 1;
    }
}