package br.com.utils;

public class Validacoes {

    // Valida placas nos formatos antigo (ABC-1234)
    // e Mercosul (ABC1D23).
    public static boolean validarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return false;
        }

        return placa.matches("^[A-Z]{3}-?\\d{4}$|^[A-Z]{3}\\d{1}[A-Z]{1}\\d{2}$");
    }

    // Verifica se um campo possui conteúdo válido.
    public static boolean validarCampoVazio(String campo) {
        return campo != null && !campo.trim().isEmpty();
    }

    // Valida datas no formato dd/MM/yyyy.
    public static boolean validarData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return false;
        }

        return data.matches("^\\d{2}/\\d{2}/\\d{4}$");
    }

    // Verifica se o valor informado pode ser convertido para double.
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

    // Valida se o ano está dentro de um intervalo aceitável.
    public static boolean validarAno(String ano) {
        if (ano == null || ano.trim().isEmpty()) {
            return false;
        }

        try {
            int anoInt = Integer.parseInt(ano);

            return anoInt > 1900
                    && anoInt <= java.time.LocalDate.now().getYear();

        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Extrai o mês e o ano de uma data no formato MM/yyyy.
    public static String extrairMesAno(String data) {

        // Retorna nulo caso a data seja inválida
        if (!validarData(data)) {
            return null;
        }

        String[] partes = data.split("/");

        return partes[1] + "/" + partes[2];
    }

    // Extrai apenas o ano de uma data.
    public static String extrairAno(String data) {

        // Retorna nulo caso a data seja inválida
        if (!validarData(data)) {
            return null;
        }

        String[] partes = data.split("/");

        return partes[2];
    }
}