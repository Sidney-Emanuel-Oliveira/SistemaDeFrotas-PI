package br.com.dao;

import br.com.model.Veiculo;
import br.com.database.MySQLSincronizador;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    private static final String ARQUIVO_VEICULOS = "dados/veiculos.txt";

    public VeiculoDAO() {
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
        File arquivo = new File(ARQUIVO_VEICULOS);

        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void salvar(Veiculo veiculo) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                salvarBanco(veiculo);
            } catch (Exception e) {
                System.err.println(
                        "[VeiculoDAO] Erro ao salvar no banco. Usando fallback de arquivo. Erro: " + e.getMessage());
            }
        }

        List<Veiculo> veiculos = obterTodosArquivo();

        boolean encontrado = false;

        for (int i = 0; i < veiculos.size(); i++) {
            if (veiculos.get(i).getIdVeiculo().equals(veiculo.getIdVeiculo())) {
                veiculos.set(i, veiculo);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            veiculos.add(veiculo);
        }

        escreverArquivo(veiculos);

        if (MySQLSincronizador.isHabilitado()) {
            MySQLSincronizador.sincronizarSilenciosamente();
        }
    }

    private void salvarBanco(Veiculo v) throws Exception {
        String sql = "INSERT INTO veiculos (id_veiculo, placa, marca, modelo, ano_fabricacao, ativo, tipo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE placa = ?, marca = ?, modelo = ?, ano_fabricacao = ?, ativo = ?, tipo = ?";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, v.getIdVeiculo());
            stmt.setString(2, v.getPlaca());
            stmt.setString(3, v.getMarca());
            stmt.setString(4, v.getModelo());
            stmt.setString(5, v.getFabricateYear());
            stmt.setBoolean(6, Boolean.TRUE.equals(v.getAtivo()));
            stmt.setString(7, v.getTipo());

            stmt.setString(8, v.getPlaca());
            stmt.setString(9, v.getMarca());
            stmt.setString(10, v.getModelo());
            stmt.setString(11, v.getFabricateYear());
            stmt.setBoolean(12, Boolean.TRUE.equals(v.getAtivo()));
            stmt.setString(13, v.getTipo());

            stmt.executeUpdate();
        }
    }

    public void salvarTodos(List<Veiculo> veiculos) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                for (Veiculo v : veiculos) {
                    salvarBanco(v);
                }
            } catch (Exception e) {
                System.err.println("[VeiculoDAO] Erro ao salvar todos no banco. Erro: " + e.getMessage());
            }
        }
        escreverArquivo(veiculos);
        if (MySQLSincronizador.isHabilitado()) {
            MySQLSincronizador.sincronizarSilenciosamente();
        }
    }

    private void escreverArquivo(List<Veiculo> veiculos) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_VEICULOS))) {

            for (Veiculo v : veiculos) {
                String inline = formatarVeiculo(v);

                writer.write(inline);
                writer.newLine();
            }
        }
    }

    private String formatarVeiculo(Veiculo v) {
        return v.getIdVeiculo() + ";" +
                v.getPlaca() + ";" +
                v.getMarca() + ";" +
                v.getModelo() + ";" +
                v.getFabricateYear() + ";" +
                v.getAtivo() + ";" +
                (v.getTipo() != null ? v.getTipo() : "");
    }

    public List<Veiculo> obterTodos() throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                return obterTodosBanco();
            } catch (Exception e) {
                System.err.println(
                        "[VeiculoDAO] Erro ao obter do banco. Usando fallback de arquivo. Erro: " + e.getMessage());
            }
        }

        return obterTodosArquivo();
    }

    private List<Veiculo> obterTodosArquivo() throws IOException {
        List<Veiculo> veiculos = new ArrayList<>();

        if (!Files.exists(Paths.get(ARQUIVO_VEICULOS))) {
            return veiculos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_VEICULOS))) {

            String linha;

            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {

                    Veiculo v = parseVeiculo(linha);

                    if (v != null) {
                        veiculos.add(v);
                    }
                }
            }
        }

        return veiculos;
    }

    private List<Veiculo> obterTodosBanco() throws Exception {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT id_veiculo, placa, marca, modelo, ano_fabricacao, ativo, tipo FROM veiculos";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Long id = rs.getLong("id_veiculo");
                String placa = rs.getString("placa");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String ano = rs.getString("ano_fabricacao");
                Boolean ativo = rs.getBoolean("ativo");
                String tipo = rs.getString("tipo");
                veiculos.add(new Veiculo(id, placa, marca, modelo, ano, ativo, tipo));
            }
        }
        return veiculos;
    }

    private Veiculo parseVeiculo(String linha) {
        try {
            String[] partes = linha.split(";");

            if (partes.length >= 6) {
                Long id = Long.parseLong(partes[0]);
                String placa = partes[1];
                String marca = partes[2];
                String modelo = partes[3];
                String ano = partes[4];
                Boolean ativo = Boolean.parseBoolean(partes[5]);
                String tipo = partes.length > 6 ? partes[6] : "";

                return new Veiculo(id, placa, marca, modelo, ano, ativo, tipo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Veiculo obterPorId(Long id) throws IOException {
        List<Veiculo> veiculos = obterTodos();

        for (Veiculo v : veiculos) {
            if (v.getIdVeiculo().equals(id)) {
                return v;
            }
        }

        return null;
    }

    public Veiculo obterPorPlaca(String placa) throws IOException {
        List<Veiculo> veiculos = obterTodos();

        for (Veiculo v : veiculos) {
            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return v;
            }
        }

        return null;
    }

    public void deletar(Long id) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                deletarBanco(id);
            } catch (Exception e) {
                System.err.println("[VeiculoDAO] Erro ao deletar no banco. Erro: " + e.getMessage());
            }
        }

        List<Veiculo> veiculos = obterTodosArquivo();

        veiculos.removeIf(v -> v.getIdVeiculo().equals(id));

        escreverArquivo(veiculos);

        if (MySQLSincronizador.isHabilitado()) {
            MySQLSincronizador.sincronizarSilenciosamente();
        }
    }

    private void deletarBanco(Long id) throws Exception {
        String sql = "DELETE FROM veiculos WHERE id_veiculo = ?";
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
                System.err.println("[VeiculoDAO] Erro ao obter próximo ID do banco. Erro: " + e.getMessage());
            }
        }

        List<Veiculo> veiculos = obterTodos();

        if (veiculos.isEmpty()) {
            return 1L;
        }

        return veiculos.stream()
                .mapToLong(Veiculo::getIdVeiculo)
                .max()
                .orElse(0L) + 1;
    }

    private Long obterProximoIdBanco() throws Exception {
        String sql = "SELECT MAX(id_veiculo) FROM veiculos";
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
