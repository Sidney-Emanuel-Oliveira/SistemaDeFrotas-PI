package br.com.model;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

// Enum dos Tipos de veículo com uma descrição simples redirecionando para o que eles são com um formato melhor
public enum TipoVeiculo {
    CARRO("Carro"),
    MOTO("Moto"),
    CAMINHAO("Caminhão"),
    VAN("Van"),
    CAMINHONETE("Caminhonete"),
    ONIBUS("Ônibus"),
    OUTRO("Outro");

    private final String descricao;

    TipoVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    /**
     * Converte uma descrição em um TipoVeiculo, ignorando acentos e diferenças de maiúsculas/minúsculas.
     * Retorna OUTRO se não encontrar correspondência.
     */
    public static TipoVeiculo fromDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            return OUTRO;
        }

        String normalizada = normalizar(descricao);
        for (TipoVeiculo tipo : values()) {
            if (normalizar(tipo.descricao).equals(normalizada) || normalizar(tipo.name()).equals(normalizada)) {
                return tipo;
            }
        }
        return OUTRO;
    }

    private static final Pattern ACENTOS = Pattern.compile("\\p{M}");

    // Para deixar o texto normal, sem acentos
    private static String normalizar(String texto) {
        return ACENTOS.matcher(
                        Normalizer.normalize(texto, Normalizer.Form.NFD))
                .replaceAll("")
                .replace('_', ' ')
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}
