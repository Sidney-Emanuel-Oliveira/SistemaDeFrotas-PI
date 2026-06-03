package br.com.view;

import br.com.controller.TipoDespesaController;
import br.com.database.MySQLSincronizador;
import br.com.model.TipoDespesa;
import br.com.ui.ModernTabbedPane;
import br.com.ui.ModernColors;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;


public class TelaPrincipal extends JFrame {

    private JTabbedPane abas;
    private TipoDespesaController tipoDespesaController;

    
    private TelaCadastroVeiculo telaCadastroVeiculo;
    private TelaMovimentacao telaMovimentacao;
    private TelaRelatorios telaRelatorios;
    private TelaAbout telaAbout;

    
    private static final int LARGURA_JANELA = 1200;
    private static final int ALTURA_JANELA = 750;
    private static final String TITULO_SISTEMA = "Sistema de Controle de Frotas - GynLog";

    public TelaPrincipal() {
        
        configurarAparenciaGlobal();

        configurarJanela();
        inicializarSistema();
        construirInterface();
    }

    
    private void configurarJanela() {
        setTitle(TITULO_SISTEMA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setMinimumSize(new Dimension(1100, 700)); 
        setLocationRelativeTo(null);
        setResizable(true);

        getContentPane().setBackground(ModernColors.BG_PRIMARY);
    }

    
    private void configurarAparenciaGlobal() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {

        }

        Font fontePadrao = new Font("Segoe UI", Font.PLAIN, 12);
        UIManager.put("defaultFont", fontePadrao);
        UIManager.put("Panel.background", ModernColors.BG_PRIMARY);
        UIManager.put("control", ModernColors.BG_PRIMARY);
        UIManager.put("nimbusBase", ModernColors.PRIMARY_BLUE);
        UIManager.put("nimbusBlueGrey", ModernColors.BORDER_GRAY);
        UIManager.put("nimbusSelectionBackground", ModernColors.PRIMARY_BLUE);
        UIManager.put("Table.alternateRowColor", ModernColors.VERY_LIGHT_GRAY);
        UIManager.put("TextField.font", fontePadrao);
        UIManager.put("ComboBox.font", fontePadrao);
        UIManager.put("Label.font", fontePadrao);
        UIManager.put("MenuBar.background", ModernColors.WHITE);
        UIManager.put("Menu.background", ModernColors.WHITE);
        UIManager.put("Menu.foreground", ModernColors.DARK_GRAY);
        UIManager.put("MenuItem.background", ModernColors.WHITE);
        UIManager.put("MenuItem.foreground", ModernColors.DARK_GRAY);
        UIManager.put("OptionPane.background", ModernColors.WHITE);
        UIManager.put("OptionPane.messageForeground", ModernColors.DARK_GRAY);
        UIManager.put("Table.background", ModernColors.WHITE);
        UIManager.put("Table.foreground", ModernColors.DARK_GRAY);
        UIManager.put("Table.gridColor", ModernColors.BORDER_GRAY);
    }

    private void inicializarSistema() {
        tipoDespesaController = new TipoDespesaController();
        inicializarTiposDespesaPadrao();
    }

    private void construirInterface() {
        criarMenuBar();
        criarAbas();
    }

    
    private void criarAbas() {
        abas = new ModernTabbedPane();

        telaCadastroVeiculo = new TelaCadastroVeiculo();
        telaMovimentacao = new TelaMovimentacao();
        telaRelatorios = new TelaRelatorios();
        telaAbout = new TelaAbout();

        abas.addTab("Veículos", telaCadastroVeiculo);
        abas.addTab("Movimentações", telaMovimentacao);
        abas.addTab("Relatórios", telaRelatorios);
        abas.addTab("Sobre", telaAbout);

        
        abas.addChangeListener(evento -> atualizarAbaSelecionada());

        add(abas);
    }

    
    private void atualizarAbaSelecionada() {
        Component abaSelecionada = abas.getSelectedComponent();

        if (abaSelecionada == telaMovimentacao) {
            telaMovimentacao.atualizarDados();
        } else if (abaSelecionada == telaRelatorios) {
            telaRelatorios.atualizarDados();
        }
    }

    
    private void inicializarTiposDespesaPadrao() {
        try {
            List<TipoDespesa> tiposExistentes = tipoDespesaController.obterTodosTipos();

            if (tiposExistentes.isEmpty()) {
                cadastrarTiposPadrao();
            }
        } catch (IOException erro) {
            exibirErro("Erro ao inicializar tipos de despesa", erro);
        }
    }

    private void cadastrarTiposPadrao() throws IOException {
        String[] tiposPadrao = {
                "Combustível",
                "Seguro",
                "Lavagem",
                "Manutenção",
                "IPVA",
                "Multa"
        };

        for (String tipo : tiposPadrao) {
            tipoDespesaController.salvarTipoDespesa(tipo);
        }
    }

    private void exibirErro(String mensagem, Exception erro) {
        System.err.println(mensagem + ": " + erro.getMessage());
        erro.printStackTrace();
    }

    private void criarMenuBar() {
        JMenuBar barraMenu = criarBarraMenu();

        barraMenu.add(criarMenuArquivo());
        barraMenu.add(criarMenuBancoDados());
        barraMenu.add(criarMenuTema());
        barraMenu.add(criarMenuAjuda());

        setJMenuBar(barraMenu);
    }

    private JMenuBar criarBarraMenu() {
        JMenuBar barraMenu = new JMenuBar();
        barraMenu.setBackground(ModernColors.WHITE);
        barraMenu.setForeground(ModernColors.DARK_GRAY);
        barraMenu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ModernColors.BORDER_GRAY),
                BorderFactory.createEmptyBorder(3, 4, 3, 4)
        ));
        return barraMenu;
    }

    private JMenu criarMenuArquivo() {
        JMenu menuArquivo = new JMenu("Arquivo");
        estilizarMenu(menuArquivo);

        JMenuItem itemSair = new JMenuItem("Sair");
        estilizarMenuItem(itemSair);
        itemSair.addActionListener(evento -> encerrarSistema());

        menuArquivo.add(itemSair);
        return menuArquivo;
    }

    private JMenu criarMenuBancoDados() {
        JMenu menuBanco = new JMenu("Banco de Dados");
        estilizarMenu(menuBanco);

        JMenuItem itemSincronizar = new JMenuItem("Sincronizar MySQL agora");
        estilizarMenuItem(itemSincronizar);
        itemSincronizar.addActionListener(evento -> sincronizarMySQLAgora());

        JMenuItem itemStatus = new JMenuItem("Ver configuração MySQL");
        estilizarMenuItem(itemStatus);
        itemStatus.addActionListener(evento -> exibirConfiguracaoMySQL());

        menuBanco.add(itemSincronizar);
        menuBanco.add(itemStatus);
        return menuBanco;
    }

    private JMenu criarMenuTema() {
        JMenu menuTema = new JMenu("Tema");
        estilizarMenu(menuTema);

        JMenuItem itemAlternarTema = new JMenuItem(ModernColors.isDarkTheme() ? "Usar tema claro" : "Usar tema escuro");
        estilizarMenuItem(itemAlternarTema);
        itemAlternarTema.addActionListener(evento -> alternarTemaInterface());

        menuTema.add(itemAlternarTema);
        return menuTema;
    }

    private JMenu criarMenuAjuda() {
        JMenu menuAjuda = new JMenu("Ajuda");
        estilizarMenu(menuAjuda);

        JMenuItem itemSobre = new JMenuItem("Sobre");
        estilizarMenuItem(itemSobre);
        itemSobre.addActionListener(evento -> exibirSobre());

        menuAjuda.add(itemSobre);
        return menuAjuda;
    }

    private void estilizarMenu(JMenu menu) {
        menu.setForeground(ModernColors.DARK_GRAY);
        menu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void estilizarMenuItem(JMenuItem item) {
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    
    private void alternarTemaInterface() {
        ModernColors.alternarTema();
        configurarAparenciaGlobal();
        getContentPane().removeAll();
        getContentPane().setBackground(ModernColors.BG_PRIMARY);
        construirInterface();
        SwingUtilities.updateComponentTreeUI(this);
        revalidate();
        repaint();
    }

    private void sincronizarMySQLAgora() {
        try {
            String resultado = MySQLSincronizador.sincronizarAgora();
            JOptionPane.showMessageDialog(
                    this,
                    resultado,
                    "Banco de Dados MySQL",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (ClassNotFoundException erro) { 
            JOptionPane.showMessageDialog(
                    this,
                    "Driver do MySQL não encontrado. Adicione o mysql-connector-j ao projeto Maven ou ao classpath.",
                    "Erro ao sincronizar MySQL",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível sincronizar com o MySQL:\n" + erro.getMessage(),
                    "Erro ao sincronizar MySQL",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void exibirConfiguracaoMySQL() {
        JOptionPane.showMessageDialog(
                this,
                MySQLSincronizador.obterResumoConfiguracao(),
                "Configuração MySQL",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void encerrarSistema() {
        System.exit(0);
    }

    private void exibirSobre() {
        String mensagem = "Sistema de Controle de Frotas v2.0\n" +
                "Desenvolvido para GynLog\n" +
                "Gerenciamento completo de frotas de veículos";

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Sobre o Sistema",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal telaPrincipal = new TelaPrincipal();
            telaPrincipal.setVisible(true);
        });
    }
}