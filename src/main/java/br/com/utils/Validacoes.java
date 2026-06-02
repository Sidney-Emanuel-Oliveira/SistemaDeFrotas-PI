package br.com.utils;

public class Validacoes {

    public static boolean validarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return false;
        }
        return placa.matches("^[A-Z]{3}-?\\d{4}$|^[A-Z]{3}\\d{1}[A-Z]{1}\\d{2}$");
    }

    public static boolean validarCampoVazio(String campo) {
        return campo != null && !campo.trim().isEmpty();
    }

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
            return anoInt > 1900 && anoInt <= java.time.LocalDate.now().getYear();
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
}

