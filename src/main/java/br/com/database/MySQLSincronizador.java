package br.com.database;

// Modelos internos do sistema de frotas
import br.com.model.Movimentacao;
import br.com.model.TipoDespesa;
import br.com.model.Veiculo;

// Classes do Java para manipulação de arquivos e fluxos de texto
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// API JDBC para conexão e execução de comandos SQL no banco de dados
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

// Utilitários para tratamento e formatação de datas
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Estruturas de dados da API de coleções do Java
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

// Classe utilitária final responsável por sincronizar os arquivos de texto locais com o MySQL
public final class MySQLSincronizador {

    // Definição dos caminhos dos arquivos físicos de configuração e dados (.txt)
    private static final Path CONFIG_PATH = Paths.get("dados", "mysql.properties");
    private static final Path ARQUIVO_VEICULOS = Paths.get("dados", "veiculos.txt");
    private static final Path ARQUIVO_TIPOS = Paths.get("dados", "tipos_despesas.txt");
    private static final Path ARQUIVO_MOVIMENTACOES = Paths.get("dados", "movimentacoes.txt");
    private static final DateTimeFormatter FORMATO_DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Evita poluir o console repetidamente com logs caso ocorra um erro contínuo de conexão
    private static boolean avisoErroJaExibido = false;

    // Construtor privado para impedir a instanciação da classe (padrão utilitário)
    private MySQLSincronizador() {
    }

    // Executa a sincronização sem lançar exceções para o chamador, ideal para rotinas automáticas em background
    public static void sincronizarSilenciosamente() {
        try {
            if (isHabilitado()) {
                sincronizarAgora();
            }
        } catch (Exception erro) {
            if (!avisoErroJaExibido) {
                System.err.println("[MySQL] Sincronização ignorada: " + erro.getMessage());
                avisoErroJaExibido = true;
            }
        }
    }

    // Fluxo principal de sincronização: lê arquivos, valida dados e commita tudo no banco
    public static String sincronizarAgora() throws Exception {
        Properties config = carregarConfiguracao();

        // Aborta o processo se o módulo estiver desativado no arquivo de propriedades
        if (!Boolean.parseBoolean(config.getProperty("mysql.enabled", "false"))) {
            return "A sincronização MySQL está desativada. Altere mysql.enabled=true em dados/mysql.properties.";
        }

        // Carrega as listas a partir dos arquivos de texto locais
        List<Veiculo> veiculos = lerVeiculosTxt();
        List<TipoDespesa> tipos = lerTiposTxt();
        List<Movimentacao> movimentacoes = lerMovimentacoesTxt();

        // Mecanismo de consistência: cria tipos de despesa usados em movimentações mas ausentes no arquivo de tipos
        garantirTiposReferenciados(tipos, movimentacoes);

        // Gerencia a transação JDBC de forma manual para garantir atomicidade (tudo ou nada)
        try (Connection conexao = abrirConexao(config)) {
            conexao.setAutoCommit(false);

            criarEstruturaSeNecessario(conexao);
            substituirDados(conexao, veiculos, tipos, movimentacoes);

            conexao.commit();
        }

        return "Sincronização concluída com sucesso. Registros enviados ao MySQL: " +
                veiculos.size() + " veículo(s), " +
                tipos.size() + " tipo(s) de despesa e " +
                movimentacoes.size() + " movimentação(ões).";
    }

    // Verifica rapidamente se a sincronização está ativa no arquivo properties
    public static boolean isHabilitado() {
        try {
            Properties config = carregarConfiguracao();
            return Boolean.parseBoolean(config.getProperty("mysql.enabled", "false"));
        } catch (IOException erro) {
            return false;
        }
    }

    // Retorna uma String formatada com os dados atuais de conexão (utilizado para logs e painéis)
    public static String obterResumoConfiguracao() {
        try {
            Properties config = carregarConfiguracao();
            return "MySQL habilitado: " + config.getProperty("mysql.enabled", "false") + "\n" +
                    "URL: " + config.getProperty("mysql.url", "") + "\n" +
                    "Usuário: " + config.getProperty("mysql.user", "");
        } catch (IOException erro) {
            return "Não foi possível ler dados/mysql.properties: " + erro.getMessage();
        }
    }

    // Registra o driver JDBC e estabelece a conexão direta com o servidor de banco de dados
    private static Connection abrirConexao(Properties config) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String url = config.getProperty("mysql.url");
        String usuario = config.getProperty("mysql.user");
        String senha = config.getProperty("mysql.password");

        return DriverManager.getConnection(url, usuario, senha);
    }

    // Carrega o arquivo mysql.properties em memória usando a codificação UTF-8
    private static Properties carregarConfiguracao() throws IOException {
        criarConfiguracaoPadraoSeNaoExistir();

        Properties config = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            config.load(reader);
        }
        return config;
    }

    // Cria os diretórios e gera um arquivo de configuração inicial padrão caso ele não exista
    private static void criarConfiguracaoPadraoSeNaoExistir() throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());
        if (!Files.exists(CONFIG_PATH)) {
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                writer.write("mysql.enabled=false\n");
                writer.write("mysql.url=jdbc:mysql://localhost:3306/gynlog_frotas?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Sao_Paulo\n");
                writer.write("mysql.user=root\n");
                writer.write("mysql.password=\n");
            }
        }
    }

    // DDL: Cria a estrutura de tabelas relacionais e as Views de relatórios estatísticos
    private static void criarEstruturaSeNecessario(Connection conexao) throws Exception {
        try (Statement st = conexao.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS veiculos (" +
                    "id_veiculo BIGINT PRIMARY KEY, " +
                    "placa VARCHAR(20) NOT NULL UNIQUE, " +
                    "marca VARCHAR(100) NOT NULL, " +
                    "modelo VARCHAR(100) NOT NULL, " +
                    "ano_fabricacao VARCHAR(10) NOT NULL, " +
                    "ativo BOOLEAN NOT NULL, " +
                    "tipo VARCHAR(50) NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS tipos_despesas (" +
                    "id_tipo_despesa BIGINT PRIMARY KEY, " +
                    "descricao VARCHAR(120) NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS movimentacoes (" +
                    "id_movimentacao BIGINT PRIMARY KEY, " +
                    "id_veiculo BIGINT NOT NULL, " +
                    "id_tipo_despesa BIGINT NOT NULL, " +
                    "descricao VARCHAR(255) NOT NULL, " +
                    "data_movimentacao DATE NULL, " +
                    "valor DECIMAL(12,2) NOT NULL, " +
                    "tipo VARCHAR(120) NOT NULL, " +
                    "distancia_percorrida_km DECIMAL(12,2) DEFAULT 0, " +
                    "litros_combustivel DECIMAL(12,2) DEFAULT 0, " +
                    "CONSTRAINT fk_mov_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculos(id_veiculo) ON UPDATE CASCADE, " +
                    "CONSTRAINT fk_mov_tipo FOREIGN KEY (id_tipo_despesa) REFERENCES tipos_despesas(id_tipo_despesa) ON UPDATE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            // View 1: Totalizador de custos financeiros por veículo
            st.executeUpdate("CREATE OR REPLACE VIEW vw_gastos_por_veiculo AS " +
                    "SELECT v.id_veiculo, v.placa, v.marca, v.modelo, v.tipo AS tipo_veiculo, " +
                    "COUNT(m.id_movimentacao) AS qtd_movimentacoes, COALESCE(SUM(m.valor), 0) AS total_gasto " +
                    "FROM veiculos v LEFT JOIN movimentacoes m ON m.id_veiculo = v.id_veiculo " +
                    "GROUP BY v.id_veiculo, v.placa, v.marca, v.modelo, v.tipo");

            // View 2: Média de consumo de combustível (km/l) da frota
            st.executeUpdate("CREATE OR REPLACE VIEW vw_consumo_medio_veiculo AS " +
                    "SELECT v.id_veiculo, v.placa, v.marca, v.modelo, " +
                    "SUM(m.distancia_percorrida_km) AS distancia_total_km, " +
                    "SUM(m.litros_combustivel) AS litros_total, " +
                    "CASE WHEN SUM(m.litros_combustivel) > 0 THEN SUM(m.distancia_percorrida_km) / SUM(m.litros_combustivel) ELSE 0 END AS consumo_medio_km_l " +
                    "FROM veiculos v LEFT JOIN movimentacoes m ON m.id_veiculo = v.id_veiculo " +
                    "GROUP BY v.id_veiculo, v.placa, v.marca, v.modelo");

            // View 3: Filtro rápido para auditoria de veículos desativados
            st.executeUpdate("CREATE OR REPLACE VIEW vw_veiculos_inativos AS " +
                    "SELECT * FROM veiculos WHERE ativo = false");
        }
    }

    // Limpa o banco de dados e reinicia os inserts, desativando temporariamente as FKs para evitar conflitos no TRUNCATE
    private static void substituirDados(Connection conexao, List<Veiculo> veiculos,
                                        List<TipoDespesa> tipos,
                                        List<Movimentacao> movimentacoes) throws Exception {
        try (Statement st = conexao.createStatement()) {
            st.execute("SET FOREIGN_KEY_CHECKS=0");
            st.executeUpdate("TRUNCATE TABLE movimentacoes");
            st.executeUpdate("TRUNCATE TABLE veiculos");
            st.executeUpdate("TRUNCATE TABLE tipos_despesas");
            st.execute("SET FOREIGN_KEY_CHECKS=1");
        }

        // Envia os dados em lote respeitando a hierarquia das chaves estrangeiras
        inserirTipos(conexao, tipos);
        inserirVeiculos(conexao, veiculos);
        inserirMovimentacoes(conexao, movimentacoes);
    }

    // Insere a coleção de veículos utilizando processamento em lote (Batch)
    private static void inserirVeiculos(Connection conexao, List<Veiculo> veiculos) throws Exception {
        String sql = "INSERT INTO veiculos (id_veiculo, placa, marca, modelo, ano_fabricacao, ativo, tipo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            for (Veiculo v : veiculos) {
                ps.setLong(1, v.getIdVeiculo());
                ps.setString(2, v.getPlaca());
                ps.setString(3, v.getMarca());
                ps.setString(4, v.getModelo());
                ps.setString(5, v.getFabricateYear());
                ps.setBoolean(6, Boolean.TRUE.equals(v.getAtivo()));
                ps.setString(7, v.getTipo());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // Insere as tabelas de apoio (categorias de despesas) via Batch
    private static void inserirTipos(Connection conexao, List<TipoDespesa> tipos) throws Exception {
        String sql = "INSERT INTO tipos_despesas (id_tipo_despesa, descricao) VALUES (?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            for (TipoDespesa t : tipos) {
                ps.setLong(1, t.getIdTipoDespesa());
                ps.setString(2, t.getDescricao());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // Insere as movimentações financeiras no banco tratando dados opcionais e conversões de datas
    private static void inserirMovimentacoes(Connection conexao, List<Movimentacao> movimentacoes) throws Exception {
        String sql = "INSERT INTO movimentacoes (id_movimentacao, id_veiculo, id_tipo_despesa, descricao, " +
                "data_movimentacao, valor, tipo, distancia_percorrida_km, litros_combustivel) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            for (Movimentacao m : movimentacoes) {
                ps.setLong(1, m.getIdMovimentacao());
                ps.setLong(2, m.getIdVeiculo());
                ps.setLong(3, m.getIdTipoDespesa());
                ps.setString(4, m.getDescricao());

                Date dataSql = converterData(m.getData());
                if (dataSql == null) {
                    ps.setNull(5, java.sql.Types.DATE);
                } else {
                    ps.setDate(5, dataSql);
                }
                ps.setDouble(6, m.getValor());
                ps.setString(7, m.getTipo());
                ps.setDouble(8, m.getDistanciaPercorridaKm());
                ps.setDouble(9, m.getLitrosCombustivel());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // Converte de String brasileira ("dd/MM/yyyy") para o objeto java.sql.Date nativo do JDBC
    private static Date converterData(String texto) {
        try {
            if (texto == null || texto.isBlank()) {
                return null;
            }
            LocalDate data = LocalDate.parse(texto.trim(), FORMATO_DATA_BR);
            return Date.valueOf(data);
        } catch (Exception erro) {
            return null; // Retorna nulo de forma segura caso a data esteja corrompida no arquivo
        }
    }

    // Faz o parse do arquivo "veiculos.txt" que utiliza separadores por ponto e vírgula
    private static List<Veiculo> lerVeiculosTxt() throws IOException {
        List<Veiculo> veiculos = new ArrayList<>();
        if (!Files.exists(ARQUIVO_VEICULOS)) {
            return veiculos;
        }

        try (BufferedReader reader = Files.newBufferedReader(ARQUIVO_VEICULOS, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }
                String[] partes = linha.split(";");
                if (partes.length >= 6) {
                    Long id = Long.parseLong(partes[0]);
                    String tipo = partes.length > 6 ? partes[6] : "Outro"; // Define fallback se o tipo não estiver na linha
                    veiculos.add(new Veiculo(id, partes[1], partes[2], partes[3], partes[4],
                            Boolean.parseBoolean(partes[5]), tipo));
                }
            }
        }
        return veiculos;
    }

    // Faz o parse do arquivo "tipos_despesas.txt"
    private static List<TipoDespesa> lerTiposTxt() throws IOException {
        List<TipoDespesa> tipos = new ArrayList<>();
        if (!Files.exists(ARQUIVO_TIPOS)) {
            return tipos;
        }

        try (BufferedReader reader = Files.newBufferedReader(ARQUIVO_TIPOS, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }
                // Limita em 2 partes para garantir que descrições contendo ponto e vírgula não quebrem o split
                String[] partes = linha.split(";", 2);
                if (partes.length >= 2) {
                    tipos.add(new TipoDespesa(Long.parseLong(partes[0]), partes[1]));
                }
            }
        }
        return tipos;
    }

    // Faz o parse de "movimentacoes.txt" tratando dados numéricos e colunas opcionais no fim da linha
    private static List<Movimentacao> lerMovimentacoesTxt() throws IOException {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        if (!Files.exists(ARQUIVO_MOVIMENTACOES)) {
            return movimentacoes;
        }

        try (BufferedReader reader = Files.newBufferedReader(ARQUIVO_MOVIMENTACOES, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }
                String[] partes = linha.split(";");
                if (partes.length >= 7) {
                    double distancia = partes.length > 7 && !partes[7].isBlank() ? Double.parseDouble(partes[7]) : 0.0;
                    double litros = partes.length > 8 && !partes[8].isBlank() ? Double.parseDouble(partes[8]) : 0.0;
                    movimentacoes.add(new Movimentacao(
                            Long.parseLong(partes[0]),
                            Long.parseLong(partes[1]),
                            Long.parseLong(partes[2]),
                            partes[3],
                            partes[4],
                            Double.parseDouble(partes[5]),
                            partes[6],
                            distancia,
                            litros
                    ));
                }
            }
        }
        return movimentacoes;
    }

    // Previne erros de Foreign Key criando em memória tipos que existem nas movimentações mas não foram cadastrados
    private static void garantirTiposReferenciados(List<TipoDespesa> tipos, List<Movimentacao> movimentacoes) {
        Set<Long> idsExistentes = new HashSet<>();
        for (TipoDespesa tipo : tipos) {
            idsExistentes.add(tipo.getIdTipoDespesa());
        }

        for (Movimentacao movimentacao : movimentacoes) {
            Long idTipo = movimentacao.getIdTipoDespesa();
            if (idTipo != null && !idsExistentes.contains(idTipo)) {
                String descricao = movimentacao.getTipo() == null || movimentacao.getTipo().isBlank()
                        ? "Tipo " + idTipo
                        : movimentacao.getTipo();
                tipos.add(new TipoDespesa(idTipo, descricao));
                idsExistentes.add(idTipo); // Atualiza o Set para evitar a duplicação do tipo corretivo criado
            }
        }
    }
}