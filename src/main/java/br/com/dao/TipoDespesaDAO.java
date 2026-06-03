package br.com.dao;

import br.com.model.TipoDespesa;
import br.com.database.MySQLSincronizador;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
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
        if (MySQLSincronizador.isHabilitado()) {
            try {
                salvarBanco(tipoDespesa);
            } catch (Exception e) {
                System.err.println("[TipoDespesaDAO] Erro ao salvar no banco. Usando fallback de arquivo. Erro: "
                        + e.getMessage());
            }
        }

        List<TipoDespesa> tipos = obterTodosArquivo();

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

        if (MySQLSincronizador.isHabilitado()) {
            MySQLSincronizador.sincronizarSilenciosamente();
        }
    }

    private void salvarBanco(TipoDespesa t) throws Exception {
        String sql = "INSERT INTO tipos_despesas (id_tipo_despesa, descricao) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE descricao = ?";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, t.getIdTipoDespesa());
            stmt.setString(2, t.getDescricao());
            stmt.setString(3, t.getDescricao());
            stmt.executeUpdate();
        }
    }

    public void salvarTodos(List<TipoDespesa> tipos) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                for (TipoDespesa t : tipos) {
                    salvarBanco(t);
                }
            } catch (Exception e) {
                System.err.println("[TipoDespesaDAO] Erro ao salvar todos no banco. Erro: " + e.getMessage());
            }
        }
        escreverArquivo(tipos);
        if (MySQLSincronizador.isHabilitado()) {
            MySQLSincronizador.sincronizarSilenciosamente();
        }
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
        if (MySQLSincronizador.isHabilitado()) {
            try {
                return obterTodosBanco();
            } catch (Exception e) {
                System.err.println(
                        "[TipoDespesaDAO] Erro ao obter do banco. Usando fallback de arquivo. Erro: " + e.getMessage());
            }
        }

        return obterTodosArquivo();
    }

    private List<TipoDespesa> obterTodosArquivo() throws IOException {
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

    private List<TipoDespesa> obterTodosBanco() throws Exception {
        List<TipoDespesa> tipos = new ArrayList<>();
        String sql = "SELECT id_tipo_despesa, descricao FROM tipos_despesas";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Long id = rs.getLong("id_tipo_despesa");
                String descricao = rs.getString("descricao");
                tipos.add(new TipoDespesa(id, descricao));
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
        if (MySQLSincronizador.isHabilitado()) {
            try {
                deletarBanco(id);
            } catch (Exception e) {
                System.err.println("[TipoDespesaDAO] Erro ao deletar no banco. Erro: " + e.getMessage());
            }
        }

        List<TipoDespesa> tipos = obterTodosArquivo();

        tipos.removeIf(t -> t.getIdTipoDespesa().equals(id));

        escreverArquivo(tipos);

        if (MySQLSincronizador.isHabilitado()) {
            MySQLSincronizador.sincronizarSilenciosamente();
        }
    }

    private void deletarBanco(Long id) throws Exception {
        String sql = "DELETE FROM tipos_despesas WHERE id_tipo_despesa = ?";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Long obterProximoId() throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                return obterProximoIdBanco();
            } catch (Exception e) {
                System.err.println("[TipoDespesaDAO] Erro ao obter próximo ID do banco. Erro: " + e.getMessage());
            }
        }

        List<TipoDespesa> tipos = obterTodos();

        if (tipos.isEmpty()) {
            return 1L;
        }

        return tipos.stream()
                .mapToLong(TipoDespesa::getIdTipoDespesa)
                .max()
                .orElse(0L) + 1;
    }

    private Long obterProximoIdBanco() throws Exception {
        String sql = "SELECT MAX(id_tipo_despesa) FROM tipos_despesas";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                long max = rs.getLong(1);
                return max == 0 ? 1L : max + 1;
            }
        }
        return 1L;
    }
}
