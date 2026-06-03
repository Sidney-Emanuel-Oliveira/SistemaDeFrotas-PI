package br.com;

import br.com.database.MySQLSincronizador;
import br.com.view.TelaPrincipal;

public class Main {

    public static void main(String[] args) {
        MySQLSincronizador.sincronizarSilenciosamente();
        TelaPrincipal.main(args);
    }
}
