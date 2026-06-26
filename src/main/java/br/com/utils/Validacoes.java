package br.com.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validacoes {

    // -------------------------------------------------------------------------
    // Validações existentes (mantidas sem quebrar compatibilidade)
    // -------------------------------------------------------------------------

    public static boolean validarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return false;
        }
        return placa.matches("^[A-Z]{3}-?\\d{4}$|^[A-Z]{3}\\d{1}[A-Z]{1}\\d{2}$");
    }

    public static boolean validarCampoVazio(String campo) {
        return campo != null && !campo.trim().isEmpty();
    }

    /** Valida formato dd/MM/yyyy (apenas formato, não existência da data). */
    public static boolean validarData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return false;
        }
        return data.matches("^\\d{2}/\\d{2}/\\d{4}$");
    }

    public static boolean validarValor(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(valor.replace(",", "."));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean validarAno(String ano) {
        if (ano == null || ano.trim().isEmpty()) {
            return false;
        }
        try {
            int anoInt = Integer.parseInt(ano);
            return anoInt > 1900 && anoInt <= LocalDate.now().getYear();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String extrairMesAno(String data) {
        if (!validarData(data)) {
            return null;
        }
        String[] partes = data.split("/");
        return partes[1] + "/" + partes[2];
    }

    public static String extrairAno(String data) {
        if (!validarData(data)) {
            return null;
        }
        String[] partes = data.split("/");
        return partes[2];
    }

    // -------------------------------------------------------------------------
    // Novas validações
    // -------------------------------------------------------------------------

    /**
     * Valida a descrição de um tipo de despesa:
     * - Não vazia
     * - Entre minLen e maxLen caracteres (após trim)
     * - Apenas letras (incluindo acentuadas), números, espaços e hífens
     *
     * @return null se válido, ou a mensagem de erro se inválido
     */
    public static String validarDescricaoTipoDespesa(String descricao, int minLen, int maxLen) {
        if (!validarCampoVazio(descricao)) {
            return "Descrição não pode estar vazia!";
        }
        String desc = descricao.trim();
        if (desc.length() < minLen) {
            return "Descrição deve ter pelo menos " + minLen + " caracteres!";
        }
        if (desc.length() > maxLen) {
            return "Descrição não pode ultrapassar " + maxLen + " caracteres!";
        }
        if (!desc.matches("[\\p{L}\\p{N} \\-]+")) {
            return "Descrição contém caracteres inválidos. Use apenas letras, números, espaços e hífens.";
        }
        return null; // válido
    }

    /**
     * Valida se a data é real (não apenas o formato, mas também a existência).
     * Ex: 31/02/2024 retorna false mesmo com o formato correto.
     */
    public static boolean validarDataRealista(String data) {
        if (!validarData(data)) {
            return false;
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(data, fmt);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Valida se o valor é um número positivo (> 0).
     */
    public static boolean validarValorPositivo(String valor) {
        if (!validarValor(valor)) {
            return false;
        }
        try {
            return Double.parseDouble(valor.replace(",", ".")) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Sanitiza um texto removendo espaços múltiplos internos e fazendo trim.
     * Retorna null se a entrada for null.
     */
    public static String sanitizarTexto(String texto) {
        if (texto == null) return null;
        return texto.trim().replaceAll("\\s{2,}", " ");
    }
}
