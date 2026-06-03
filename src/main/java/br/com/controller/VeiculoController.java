package br.com.controller;

import br.com.dao.VeiculoDAO;
import br.com.estruturas.ListaLinear;
import br.com.estruturas.OrdenacaoVeiculos;
import br.com.model.Veiculo;
import br.com.utils.Validacoes;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;

public class VeiculoController {
    private VeiculoDAO veiculoDAO;

    public VeiculoController() {
        this.veiculoDAO = new VeiculoDAO();
    }

    public void salvarVeiculo(String placa, String marca, String modelo, String ano, Boolean ativo, String tipo) throws IOException {
        // Validações existentes
        if (!Validacoes.validarCampoVazio(placa) ||
                !Validacoes.validarCampoVazio(marca) ||
                !Validacoes.validarCampoVazio(modelo) ||
                !Validacoes.validarAno(ano)) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        // Adicionar validação de placa usando Validacoes.java
        if (!Validacoes.validarPlaca(placa)) {
            throw new IllegalArgumentException("Formato de placa inválido. Use ABC-1234 ou ABC1D23 (Mercosul).");
        }
        // Adicionar validação de tamanho de marca/modelo se necessário
        if (marca.length() < 2) {
            throw new IllegalArgumentException("Marca deve ter pelo menos 2 caracteres!");
        }
        if (modelo.length() < 2) {
            throw new IllegalArgumentException("Modelo deve ter pelo menos 2 caracteres!");
        }

        // Valida se tem campos vazios
        if (!Validacoes.validarCampoVazio(placa) ||
                !Validacoes.validarCampoVazio(marca) ||
                !Validacoes.validarCampoVazio(modelo) ||
                !Validacoes.validarAno(ano)) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        Veiculo existente = veiculoDAO.obterPorPlaca(placa);
        if (existente != null) {
            throw new IllegalArgumentException("Placa já cadastrada!");
        }
        
        Long novoId = veiculoDAO.obterProximoId();

        Veiculo veiculo = new Veiculo(novoId, placa, marca, modelo, ano, ativo, tipo);

        veiculoDAO.salvar(veiculo);
    }

    public void atualizarVeiculo(Long id, String placa, String marca, String modelo, String ano, Boolean ativo, String tipo) throws IOException {
        
        if (!Validacoes.validarCampoVazio(placa) ||
                !Validacoes.validarCampoVazio(marca) ||
                !Validacoes.validarCampoVazio(modelo) ||
                !Validacoes.validarAno(ano)) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        
        Veiculo existente = veiculoDAO.obterPorPlaca(placa);
        if (existente != null && !existente.getIdVeiculo().equals(id)) {
            throw new IllegalArgumentException("Placa já cadastrada por outro veículo!");
        }
        
        Veiculo veiculo = new Veiculo(id, placa, marca, modelo, ano, ativo, tipo);
        
        veiculoDAO.salvar(veiculo);
    }
    
    // Retorna todos os veículos cadastrados
    public List<Veiculo> obterTodosVeiculos() throws IOException {
        return veiculoDAO.obterTodos();
    }
    
    // Retorna apenas veículos com status ativo
    public List<Veiculo> obterVeiculosAtivos() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        return veiculos.stream()
                .filter(Veiculo::getAtivo)
                .toList();
    }
    
    // Retorna apenas veículos com status inativo
    public List<Veiculo> obterVeiculosInativos() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        return veiculos.stream()
                .filter(v -> !v.getAtivo())
                .toList();
    }
    
    public List<Veiculo> obterVeiculosFiltradosOrdenados(String termoPesquisa, String tipoFiltro, String criterioOrdenacao) throws IOException {
        
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        
        ListaLinear<Veiculo> listaFiltrada = new ListaLinear<>();

        String termo = normalizar(termoPesquisa);
        String tipo = tipoFiltro == null ? "Todos" : tipoFiltro.trim();
        
        for (Veiculo veiculo : veiculos) {

            boolean correspondePesquisa = termo.isEmpty() ||
                    normalizar(veiculo.getPlaca()).contains(termo) ||
                    normalizar(veiculo.getMarca()).contains(termo) ||
                    normalizar(veiculo.getModelo()).contains(termo);
            
            boolean correspondeTipo = tipo.equalsIgnoreCase("Todos") ||
                    normalizar(veiculo.getTipo()).equals(normalizar(tipo));
            
            if (correspondePesquisa && correspondeTipo) {
                listaFiltrada.adicionar(veiculo);
            }
        }

        return OrdenacaoVeiculos.ordenar(
                listaFiltrada.paraList(),
                criterioOrdenacao
        );
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase();
    }
    
    public Veiculo obterVeiculoPorId(Long id) throws IOException {
        return veiculoDAO.obterPorId(id);
    }

    public void deletarVeiculo(Long id) throws IOException {
        veiculoDAO.deletar(id);
    }
}