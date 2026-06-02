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

    // Inicializa o DAO de veículos
    public VeiculoController() {
        this.veiculoDAO = new VeiculoDAO();
    }

    // Salva um novo veículo
    public void salvarVeiculo(String placa, String marca, String modelo, String ano, Boolean ativo, String tipo) throws IOException {

        // Validação dos campos obrigatórios
        if (!Validacoes.validarCampoVazio(placa) ||
                !Validacoes.validarCampoVazio(marca) ||
                !Validacoes.validarCampoVazio(modelo) ||
                !Validacoes.validarAno(ano)) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        // Verifica se já existe um veículo com a mesma placa
        Veiculo existente = veiculoDAO.obterPorPlaca(placa);
        if (existente != null) {
            throw new IllegalArgumentException("Placa já cadastrada!");
        }

        // Obtém o próximo ID disponível
        Long novoId = veiculoDAO.obterProximoId();

        // Cria o objeto veículo
        Veiculo veiculo = new Veiculo(novoId, placa, marca, modelo, ano, ativo, tipo);

        // Salva o veículo
        veiculoDAO.salvar(veiculo);
    }

    // Atualiza os dados de um veículo existente
    public void atualizarVeiculo(Long id, String placa, String marca, String modelo, String ano, Boolean ativo, String tipo) throws IOException {
        // Validação dos campos obrigatórios
        if (!Validacoes.validarCampoVazio(placa) ||
                !Validacoes.validarCampoVazio(marca) ||
                !Validacoes.validarCampoVazio(modelo) ||
                !Validacoes.validarAno(ano)) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        // Verifica se a placa já pertence a outro veículo
        Veiculo existente = veiculoDAO.obterPorPlaca(placa);
        if (existente != null && !existente.getIdVeiculo().equals(id)) {
            throw new IllegalArgumentException("Placa já cadastrada por outro veículo!");
        }

        // Cria o objeto atualizado
        Veiculo veiculo = new Veiculo(id, placa, marca, modelo, ano, ativo, tipo);

        // Salva as alterações
        veiculoDAO.salvar(veiculo);
    }

    // Retorna todos os veículos cadastrados
    public List<Veiculo> obterTodosVeiculos() throws IOException {
        return veiculoDAO.obterTodos();
    }

    // Retorna apenas os veículos ativos
    public List<Veiculo> obterVeiculosAtivos() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        return veiculos.stream()
                .filter(Veiculo::getAtivo)
                .toList();
    }

    // Retorna apenas os veículos inativos
    public List<Veiculo> obterVeiculosInativos() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        return veiculos.stream()
                .filter(v -> !v.getAtivo())
                .toList();
    }

    // Filtra e ordena veículos conforme os parâmetros informados
    public List<Veiculo> obterVeiculosFiltradosOrdenados(String termoPesquisa, String tipoFiltro, String criterioOrdenacao) throws IOException {
        // Obtém todos os veículos cadastrados
        List<Veiculo> veiculos = veiculoDAO.obterTodos();

        // Estrutura que armazenará os veículos filtrados
        ListaLinear<Veiculo> listaFiltrada = new ListaLinear<>();

        // Normaliza os filtros para facilitar comparações
        String termo = normalizar(termoPesquisa);
        String tipo = tipoFiltro == null ? "Todos" : tipoFiltro.trim();

        // Percorre todos os veículos aplicando os filtros
        for (Veiculo veiculo : veiculos) {

            // Verifica se corresponde ao termo pesquisado
            boolean correspondePesquisa = termo.isEmpty() ||
                    normalizar(veiculo.getPlaca()).contains(termo) ||
                    normalizar(veiculo.getMarca()).contains(termo) ||
                    normalizar(veiculo.getModelo()).contains(termo);

            // Verifica se corresponde ao tipo selecionado
            boolean correspondeTipo = tipo.equalsIgnoreCase("Todos") ||
                    normalizar(veiculo.getTipo()).equals(normalizar(tipo));

            // Adiciona à lista caso atenda aos filtros
            if (correspondePesquisa && correspondeTipo) {
                listaFiltrada.adicionar(veiculo);
            }
        }

        // Retorna a lista ordenada conforme o critério escolhido
        return OrdenacaoVeiculos.ordenar(
                listaFiltrada.paraList(),
                criterioOrdenacao
        );
    }

    // ormaliza um texto removendo acentos, espaços excedentes e convertendo para maiúsculo
    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase();
    }

    // Busca um veículo pelo ID
    public Veiculo obterVeiculoPorId(Long id) throws IOException {
        return veiculoDAO.obterPorId(id);
    }

    // Remove um veículo pelo ID
    public void deletarVeiculo(Long id) throws IOException {
        veiculoDAO.deletar(id);
    }
}

