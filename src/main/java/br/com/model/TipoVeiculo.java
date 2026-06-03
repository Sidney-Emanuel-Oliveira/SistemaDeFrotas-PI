package br.com.model;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;


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

    public static TipoVeiculo[] valuesCadastro() {
        return new TipoVeiculo[]{CARRO, MOTO, CAMINHAO, VAN, CAMINHONETE, ONIBUS};
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

    private static final Pattern ACENTOS = Pattern.compile("\\p{M}");

    // Remove acentos e normaliza string para comparação (maiúscula)
    private static String normalizar(String texto) {
        return ACENTOS.matcher(
                        Normalizer.normalize(texto, Normalizer.Form.NFD))
                .replaceAll("")
                .replace('_', ' ')
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}
