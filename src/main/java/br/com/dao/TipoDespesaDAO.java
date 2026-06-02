package br.com.dao;

import br.com.model.TipoDespesa;
import br.com.database.MySQLSincronizador;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TipoDespesaDAO {

    // Caminho do arquivo responsável por armazenar os tipos de despesa
    private static final String ARQUIVO_TIPOS = "dados/tipos_despesas.txt";

    public TipoDespesaDAO() {
        criarDiretorioSeNaoExistir();
        criarArquivoSeNaoExistir();
    }

    // Cria o diretório de dados caso ele não exista
    private void criarDiretorioSeNaoExistir() {
        File dir = new File("dados");

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Cria o arquivo de tipos de despesa caso ele não exista
    private void criarArquivoSeNaoExistir() {
        File arquivo = new File(ARQUIVO_TIPOS);

        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Salva um novo tipo de despesa ou atualiza um existente
    public void salvar(TipoDespesa tipoDespesa) throws IOException {
        List<TipoDespesa> tipos = obterTodos();

        boolean encontrado = false;

        for (int i = 0; i < tipos.size(); i++) {
            if (tipos.get(i).getIdTipoDespesa().equals(tipoDespesa.getIdTipoDespesa())) {
                tipos.set(i, tipoDespesa);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            tipos.add(tipoDespesa);
        }

        escreverArquivo(tipos);

        // Mantém os dados sincronizados com o MySQL
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    // Salva uma lista completa de tipos de despesa
    public void salvarTodos(List<TipoDespesa> tipos) throws IOException {
        escreverArquivo(tipos);
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    // Grava todos os tipos de despesa no arquivo
    private void escreverArquivo(List<TipoDespesa> tipos) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_TIPOS))) {

            for (TipoDespesa t : tipos) {
                String linha = t.getIdTipoDespesa() + ";" + t.getDescricao();

                writer.write(linha);
                writer.newLine();
            }
        }
    }

    // Retorna todos os tipos de despesa cadastrados
    public List<TipoDespesa> obterTodos() throws IOException {
        List<TipoDespesa> tipos = new ArrayList<>();

        if (!Files.exists(Paths.get(ARQUIVO_TIPOS))) {
            return tipos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_TIPOS))) {

            String linha;

            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {

                    TipoDespesa t = parseTipoDespesa(linha);

                    if (t != null) {
                        tipos.add(t);
                    }
                }
            }
        }

        return tipos;
    }

    // Converte uma linha do arquivo em um objeto TipoDespesa
    private TipoDespesa parseTipoDespesa(String linha) {
        try {
            String[] partes = linha.split(";", 2);

            if (partes.length >= 2) {
                Long id = Long.parseLong(partes[0]);
                String descricao = partes[1];

                return new TipoDespesa(id, descricao);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Busca um tipo de despesa pelo identificador
    public TipoDespesa obterPorId(Long id) throws IOException {
        List<TipoDespesa> tipos = obterTodos();

        for (TipoDespesa t : tipos) {
            if (t.getIdTipoDespesa().equals(id)) {
                return t;
            }
        }

        return null;
    }

    // Remove um tipo de despesa pelo identificador
    public void deletar(Long id) throws IOException {
        List<TipoDespesa> tipos = obterTodos();

        tipos.removeIf(t -> t.getIdTipoDespesa().equals(id));

        escreverArquivo(tipos);

        // Atualiza a base MySQL após a exclusão
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    // Retorna o próximo ID disponível para cadastro
    public Long obterProximoId() throws IOException {
        List<TipoDespesa> tipos = obterTodos();

        if (tipos.isEmpty()) {
            return 1L;
        }

        return tipos.stream()
                .mapToLong(TipoDespesa::getIdTipoDespesa)
                .max()
                .orElse(0L) + 1;
    }
}