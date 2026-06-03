package br.com.dao;

import br.com.model.Movimentacao;
import br.com.database.MySQLSincronizador;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    private static final String ARQUIVO_MOVIMENTACOES = "dados/movimentacoes.txt";
    private static final DateTimeFormatter FORMATO_DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MovimentacaoDAO() {
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
        File arquivo = new File(ARQUIVO_MOVIMENTACOES);

        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void salvar(Movimentacao movimentacao) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                salvarBanco(movimentacao);
            } catch (Exception e) {
                System.err.println("[MovimentacaoDAO] Erro ao salvar no banco. Usando fallback de arquivo. Erro: "
                        + e.getMessage());
            }
        }

        List<Movimentacao> movimentacoes = obterTodosArquivo();

        boolean encontrado = false;

        for (int i = 0; i < movimentacoes.size(); i++) {
            if (movimentacoes.get(i).getIdMovimentacao().equals(movimentacao.getIdMovimentacao())) {
                movimentacoes.set(i, movimentacao);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            movimentacoes.add(movimentacao);
        }

        escreverArquivo(movimentacoes);

        if (MySQLSincronizador.isHabilitado()) {
            MySQLSincronizador.sincronizarSilenciosamente();
        }
    }

    private void salvarBanco(Movimentacao m) throws Exception {
        String sql = "INSERT INTO movimentacoes (id_movimentacao, id_veiculo, id_tipo_despesa, descricao, " +
                "data_movimentacao, valor, tipo, distancia_percorrida_km, litros_combustivel) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE id_veiculo = ?, id_tipo_despesa = ?, descricao = ?, " +
                "data_movimentacao = ?, valor = ?, tipo = ?, distancia_percorrida_km = ?, litros_combustivel = ?";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, m.getIdMovimentacao());
            stmt.setLong(2, m.getIdVeiculo());
            stmt.setLong(3, m.getIdTipoDespesa());
            stmt.setString(4, m.getDescricao());

            Date dataSql = converterData(m.getData());
            if (dataSql == null) {
                stmt.setNull(5, Types.DATE);
            } else {
                stmt.setDate(5, dataSql);
            }
            stmt.setDouble(6, m.getValor());
            stmt.setString(7, m.getTipo());
            stmt.setDouble(8, m.getDistanciaPercorridaKm());
            stmt.setDouble(9, m.getLitrosCombustivel());

            stmt.setLong(10, m.getIdVeiculo());
            stmt.setLong(11, m.getIdTipoDespesa());
            stmt.setString(12, m.getDescricao());
            if (dataSql == null) {
                stmt.setNull(13, Types.DATE);
            } else {
                stmt.setDate(13, dataSql);
            }
            stmt.setDouble(14, m.getValor());
            stmt.setString(15, m.getTipo());
            stmt.setDouble(16, m.getDistanciaPercorridaKm());
            stmt.setDouble(17, m.getLitrosCombustivel());

            stmt.executeUpdate();
        }
    }

    public void salvarTodos(List<Movimentacao> movimentacoes) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                for (Movimentacao m : movimentacoes) {
                    salvarBanco(m);
                }
            } catch (Exception e) {
                System.err.println("[MovimentacaoDAO] Erro ao salvar todos no banco. Erro: " + e.getMessage());
            }
        }
        escreverArquivo(movimentacoes);
        if (MySQLSincronizador.isHabilitado()) {
            MySQLSincronizador.sincronizarSilenciosamente();
        }
    }

    private void escreverArquivo(List<Movimentacao> movimentacoes) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_MOVIMENTACOES))) {

            for (Movimentacao m : movimentacoes) {
                String linha = formatarMovimentacao(m);

                writer.write(linha);
                writer.newLine();
            }
        }
    }

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

    public List<Movimentacao> obterTodos() throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                return obterTodosBanco();
            } catch (Exception e) {
                System.err.println("[MovimentacaoDAO] Erro ao obter do banco. Usando fallback de arquivo. Erro: "
                        + e.getMessage());
            }
        }

        return obterTodosArquivo();
    }

    private List<Movimentacao> obterTodosArquivo() throws IOException {
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

    private List<Movimentacao> obterTodosBanco() throws Exception {
        List<Movimentacao> list = new ArrayList<>();
        String sql = "SELECT id_movimentacao, id_veiculo, id_tipo_despesa, descricao, data_movimentacao, " +
                "valor, tipo, distancia_percorrida_km, litros_combustivel FROM movimentacoes";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Long id = rs.getLong("id_movimentacao");
                Long idVeiculo = rs.getLong("id_veiculo");
                Long idTipo = rs.getLong("id_tipo_despesa");
                String desc = rs.getString("descricao");
                Date dataSql = rs.getDate("data_movimentacao");
                String dataStr = formatarData(dataSql);
                double valor = rs.getDouble("valor");
                String tipo = rs.getString("tipo");
                double dist = rs.getDouble("distancia_percorrida_km");
                double litros = rs.getDouble("litros_combustivel");
                list.add(new Movimentacao(id, idVeiculo, idTipo, desc, dataStr, valor, tipo, dist, litros));
            }
        }
        return list;
    }

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

                double distanciaPercorridaKm = partes.length > 7 && !partes[7].isBlank()
                        ? Double.parseDouble(partes[7])
                        : 0.0;

                double litrosCombustivel = partes.length > 8 && !partes[8].isBlank()
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
                        litrosCombustivel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Movimentacao obterPorId(Long id) throws IOException {
        List<Movimentacao> movimentacoes = obterTodos();

        for (Movimentacao m : movimentacoes) {
            if (m.getIdMovimentacao().equals(id)) {
                return m;
            }
        }

        return null;
    }

    public List<Movimentacao> obterPorVeiculo(Long idVeiculo) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                return obterPorVeiculoBanco(idVeiculo);
            } catch (Exception e) {
                System.err.println("[MovimentacaoDAO] Erro ao obter por veiculo. Erro: " + e.getMessage());
            }
        }

        List<Movimentacao> movimentacoes = obterTodosArquivo();
        List<Movimentacao> resultado = new ArrayList<>();

        for (Movimentacao m : movimentacoes) {
            if (m.getIdVeiculo().equals(idVeiculo)) {
                resultado.add(m);
            }
        }

        return resultado;
    }

    private List<Movimentacao> obterPorVeiculoBanco(Long idVeiculo) throws Exception {
        List<Movimentacao> list = new ArrayList<>();
        String sql = "SELECT id_movimentacao, id_veiculo, id_tipo_despesa, descricao, data_movimentacao, " +
                "valor, tipo, distancia_percorrida_km, litros_combustivel FROM movimentacoes WHERE id_veiculo = ?";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idVeiculo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id_movimentacao");
                    Long idTipo = rs.getLong("id_tipo_despesa");
                    String desc = rs.getString("descricao");
                    Date dataSql = rs.getDate("data_movimentacao");
                    String dataStr = formatarData(dataSql);
                    double valor = rs.getDouble("valor");
                    String tipo = rs.getString("tipo");
                    double dist = rs.getDouble("distancia_percorrida_km");
                    double litros = rs.getDouble("litros_combustivel");
                    list.add(new Movimentacao(id, idVeiculo, idTipo, desc, dataStr, valor, tipo, dist, litros));
                }
            }
        }
        return list;
    }

    public List<Movimentacao> obterPorTipo(String tipo) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                return obterPorTipoBanco(tipo);
            } catch (Exception e) {
                System.err.println("[MovimentacaoDAO] Erro ao obter por tipo. Erro: " + e.getMessage());
            }
        }

        List<Movimentacao> movimentacoes = obterTodos();
        List<Movimentacao> resultado = new ArrayList<>();

        for (Movimentacao m : movimentacoes) {
            if (m.getTipo() != null && m.getTipo().equalsIgnoreCase(tipo)) {
                resultado.add(m);
            }
        }

        return resultado;
    }

    private List<Movimentacao> obterPorTipoBanco(String tipo) throws Exception {
        List<Movimentacao> list = new ArrayList<>();
        String sql = "SELECT id_movimentacao, id_veiculo, id_tipo_despesa, descricao, data_movimentacao, " +
                "valor, tipo, distancia_percorrida_km, litros_combustivel FROM movimentacoes WHERE tipo = ?";
        try (Connection conn = MySQLSincronizador.obterConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id_movimentacao");
                    Long idVeiculo = rs.getLong("id_veiculo");
                    Long idTipo = rs.getLong("id_tipo_despesa");
                    String desc = rs.getString("descricao");
                    Date dataSql = rs.getDate("data_movimentacao");
                    String dataStr = formatarData(dataSql);
                    double valor = rs.getDouble("valor");
                    double dist = rs.getDouble("distancia_percorrida_km");
                    double litros = rs.getDouble("litros_combustivel");
                    list.add(new Movimentacao(id, idVeiculo, idTipo, desc, dataStr, valor, tipo, dist, litros));
                }
            }
        }
        return list;
    }

    public void deletar(Long id) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                deletarBanco(id);
            } catch (Exception e) {
                System.err.println("[MovimentacaoDAO] Erro ao deletar no banco. Erro: " + e.getMessage());
            }
        }

        List<Movimentacao> movimentacoes = obterTodosArquivo();

        movimentacoes.removeIf(m -> m.getIdMovimentacao().equals(id));

        escreverArquivo(movimentacoes);

        if (MySQLSincronizador.isHabilitado()) {
            MySQLSincronizador.sincronizarSilenciosamente();
        }
    }

    private void deletarBanco(Long id) throws Exception {
        String sql = "DELETE FROM movimentacoes WHERE id_movimentacao = ?";
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
                System.err.println("[MovimentacaoDAO] Erro ao obter proximo ID do banco. Erro: " + e.getMessage());
            }
        }

        List<Movimentacao> movimentacoes = obterTodos();

        if (movimentacoes.isEmpty()) {
            return 1L;
        }

        return movimentacoes.stream()
                .mapToLong(Movimentacao::getIdMovimentacao)
                .max()
                .orElse(0L) + 1;
    }

    private Long obterProximoIdBanco() throws Exception {
        String sql = "SELECT MAX(id_movimentacao) FROM movimentacoes";
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

    private Date converterData(String texto) {
        try {
            if (texto == null || texto.isBlank()) {
                return null;
            }
            LocalDate data = LocalDate.parse(texto.trim(), FORMATO_DATA_BR);
            return Date.valueOf(data);
        } catch (Exception erro) {
            return null;
        }
    }

    private String formatarData(Date data) {
        if (data == null) {
            return "";
        }
        return data.toLocalDate().format(FORMATO_DATA_BR);
    }
}
