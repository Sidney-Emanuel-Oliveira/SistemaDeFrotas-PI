package br.com.model;

import java.text.Normalizer;


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

    private static String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.trim().replace("_", " ").toUpperCase();
    }
}
