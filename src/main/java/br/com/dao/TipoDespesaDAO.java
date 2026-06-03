package br.com.dao;

import br.com.model.TipoDespesa;
import br.com.database.MySQLSincronizador;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TipoDespesaDAO {

    
    private static final String ARQUIVO_TIPOS = "dados/tipos_despesas.txt";

    public TipoDespesaDAO() {
        criarDiretorioSeNaoExistir();
        criarArquivoSeNaoExistir();
    }

    
    private void criarDiretorioSeNaoExistir() {
        File dir = new File("dados");

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    
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

        
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    
    public void salvarTodos(List<TipoDespesa> tipos) throws IOException {
        escreverArquivo(tipos);
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    
    private void escreverArquivo(List<TipoDespesa> tipos) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_TIPOS))) {

            for (TipoDespesa t : tipos) {
                String linha = t.getIdTipoDespesa() + ";" + t.getDescricao();

                writer.write(linha);
                writer.newLine();
            }
        }
    }

    
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

    
    public TipoDespesa obterPorId(Long id) throws IOException {
        List<TipoDespesa> tipos = obterTodos();

        for (TipoDespesa t : tipos) {
            if (t.getIdTipoDespesa().equals(id)) {
                return t;
            }
        }

        return null;
    }

    
    public void deletar(Long id) throws IOException {
        List<TipoDespesa> tipos = obterTodos();

        tipos.removeIf(t -> t.getIdTipoDespesa().equals(id));

        escreverArquivo(tipos);

        
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    
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