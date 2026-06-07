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

/**
 * DAO (Data Access Object) para gerenciar a persistência de Movimentações
 * Suporta armazenamento em arquivos de texto e sincronização com MySQL
 */
public class MovimentacaoDAO {

    // Caminho do arquivo de armazenamento de movimentações
    private static final String ARQUIVO_MOVIMENTACOES = "dados/movimentacoes.txt";
    // Formato padrão de data utilizado (dd/MM/yyyy)
    private static final DateTimeFormatter FORMATO_DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor que inicializa o diretório e arquivo
    public MovimentacaoDAO() {
        criarDiretorioSeNaoExistir();
        criarArquivoSeNaoExistir();
    }

    // Cria o diretório "dados" se não existir
    private void criarDiretorioSeNaoExistir() {
        File dir = new File("dados");

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Cria o arquivo de movimentações se não existir
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

    // Salva ou atualiza uma movimentação (em arquivo e opcionalmente no banco)
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

    // Salva uma movimentação no banco de dados MySQL
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

    // Salva uma lista completa de movimentações
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

    // Escreve a lista de movimentações no arquivo de texto
    private void escreverArquivo(List<Movimentacao> movimentacoes) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_MOVIMENTACOES))) {

            for (Movimentacao m : movimentacoes) {
                String linha = formatarMovimentacao(m);

                writer.write(linha);
                writer.newLine();
            }
        }
    }

    // Formata uma movimentação para armazenamento no arquivo (separado por ;)
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

    // Obtém todas as movimentações (do banco se habilitado, senão do arquivo)
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

    // Lê todas as movimentações do arquivo de texto
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

    // Lê todas as movimentações do banco de dados MySQL
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

    // Converte uma linha do arquivo em objeto Movimentacao
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

    // Busca uma movimentação pelo ID
    public Movimentacao obterPorId(Long id) throws IOException {
        List<Movimentacao> movimentacoes = obterTodos();

        for (Movimentacao m : movimentacoes) {
            if (m.getIdMovimentacao().equals(id)) {
                return m;
            }
        }

        return null;
    }

    // Obtém todas as movimentações de um veículo específico
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

    // Lê movimentações de um veículo específico do banco de dados
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

    // Obtém movimentações filtradas por tipo de despesa
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

    // Lê movimentações por tipo de despesa do banco de dados
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

    // Deleta uma movimentação pelo ID
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

    // Deleta uma movimentação do banco de dados
    private void deletarBanco(Long id) throws Exception {
        String sql = "DELETE FROM movimentacoes WHERE id_movimentacao = ?";
        try (Connection conn = MySQLSincronizador.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    // Verifica se existe alguma movimentação vinculada a um tipo de despesa pelo ID
    public boolean existeMovimentacaoPorIdTipo(Long idTipoDespesa) throws IOException {
        if (MySQLSincronizador.isHabilitado()) {
            try {
                String sql = "SELECT COUNT(*) FROM movimentacoes WHERE id_tipo_despesa = ?";
                try (Connection conn = MySQLSincronizador.obterConexao();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, idTipoDespesa);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) return rs.getInt(1) > 0;
                    }
                }
            } catch (Exception e) {
                System.err.println("[MovimentacaoDAO] Erro ao verificar vinculo por idTipo. Usando arquivo. Erro: " + e.getMessage());
            }
        }

        List<Movimentacao> movimentacoes = obterTodosArquivo();
        for (Movimentacao m : movimentacoes) {
            if (idTipoDespesa.equals(m.getIdTipoDespesa())) {
                return true;
            }
        }
        return false;
    }

    // Obtém o próximo ID disponível para uma nova movimentação
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

    // Obtém o próximo ID disponível do banco de dados
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

    // Converte string de data no formato DD/MM/YYYY para java.sql.Date
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

    // Formata java.sql.Date para string no formato DD/MM/YYYY
    private String formatarData(Date data) {
        if (data == null) {
            return "";
        }
        return data.toLocalDate().format(FORMATO_DATA_BR);
    }
}