package br.com.dao;

import br.com.model.Veiculo;
import br.com.database.MySQLSincronizador;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        List<Veiculo> veiculos = obterTodos();

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

        
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    
    public void salvarTodos(List<Veiculo> veiculos) throws IOException {
        escreverArquivo(veiculos);
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    
    private void escreverArquivo(List<Veiculo> veiculos) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_VEICULOS))) {

            for (Veiculo v : veiculos) {
                String linha = formatarVeiculo(v);

                writer.write(linha);
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
        List<Veiculo> veiculos = obterTodos();

        veiculos.removeIf(v -> v.getIdVeiculo().equals(id));

        escreverArquivo(veiculos);

        
        MySQLSincronizador.sincronizarSilenciosamente();
    }

    
    public Long obterProximoId() throws IOException {
        List<Veiculo> veiculos = obterTodos();

        if (veiculos.isEmpty()) {
            return 1L;
        }

        return veiculos.stream()
                .mapToLong(Veiculo::getIdVeiculo)
                .max()
                .orElse(0L) + 1;
    }
}