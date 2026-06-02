package br.com.ui;

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

// Gerenciador de layout baseado em FlowLayout que força a quebra de linha (Wrap)
// quando os componentes horizontais estouram a largura útil do contêiner pai
public class WrapLayout extends FlowLayout {

    public WrapLayout() {
        super();
    }

    public WrapLayout(int alinhamento) {
        super(alinhamento);
    }

    public WrapLayout(int alinhamento, int espacamentoHorizontal, int espacamentoVertical) {
        super(alinhamento, espacamentoHorizontal, espacamentoVertical);
    }

    // Calcula as dimensões ideais baseando-se no comportamento de quebra de linha
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return calcularTamanhoLayout(target, true);
    }

    // Calcula as dimensões mínimas necessárias para a renderização do bloco de componentes
    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension tamanhoMinimo = calcularTamanhoLayout(target, false);
        tamanhoMinimo.width -= (getHgap() + 1);
        return tamanhoMinimo;
    }

    // Algoritmo matemático principal que simula o fluxo dos elementos para definir a altura total do bloco
    private Dimension calcularTamanhoLayout(Container target, boolean usarTamanhoPreferido) {
        synchronized (target.getTreeLock()) {

            // 1. Identifica a largura útil real em tempo de execução
            int larguraDisponivel = obterLarguraDisponivel(target);

            // 2. Desconta as margens internas (Insets) e o espaçamento regulamentar (Gaps) do layout
            int espacoHorizontal = getHgap();
            int espacoVertical = getVgap();
            Insets margens = target.getInsets();
            int margensHorizontais = margens.left + margens.right + (espacoHorizontal * 2);
            int larguraMaximaLinha = larguraDisponivel - margensHorizontais;

            Dimension dimensaoFinal = new Dimension(0, 0);
            int larguraLinhaAtual = 0;
            int alturaLinhaAtual = 0;

            // 3. Itera sobre a coleção de componentes filhos visíveis para empilhar as dimensões
            int totalComponentes = target.getComponentCount();
            for (int i = 0; i < totalComponentes; i++) {
                Component componente = target.getComponent(i);

                if (componente.isVisible()) {
                    Dimension tamanhoComponente = usarTamanhoPreferido
                            ? componente.getPreferredSize()
                            : componente.getMinimumSize();

                    // Quebra de linha física: se o componente atual estoura o limite horizontal,
                    // consolida a linha anterior no somatório de altura e reseta a largura do cursor
                    if (larguraLinhaAtual + tamanhoComponente.width > larguraMaximaLinha) {
                        adicionarLinha(dimensaoFinal, larguraLinhaAtual, alturaLinhaAtual);
                        larguraLinhaAtual = 0;
                        alturaLinhaAtual = 0;
                    }

                    // Insere o espaçamento entre componentes adjacentes na linha
                    if (larguraLinhaAtual != 0) {
                        larguraLinhaAtual += espacoHorizontal;
                    }

                    // Incrementa os acumuladores temporários da linha em execução
                    larguraLinhaAtual += tamanhoComponente.width;
                    alturaLinhaAtual = Math.max(alturaLinhaAtual, tamanhoComponente.height);
                }
            }

            // Garante a inclusão dos dados da última linha processada
            adicionarLinha(dimensaoFinal, larguraLinhaAtual, alturaLinhaAtual);

            // 4. Devolve o padding total ao cálculo da caixa geométrica final
            dimensaoFinal.width += margensHorizontais;
            dimensaoFinal.height += margens.top + margens.bottom + espacoVertical * 2;

            // Ajuste fino antiqueda: se estiver dentro de um JScrollPane válido, reduz a largura
            // para mitigar conflitos de concorrência com a barra de rolagem vertical (Scrollbar)
            Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
            if (scrollPane != null && target.isValid()) {
                dimensaoFinal.width -= (espacoHorizontal + 1);
            }

            return dimensaoFinal;
        }
    }

    // Sobe recursivamente a árvore de componentes buscando uma largura válida (maior que zero)
    // para evitar falhas de dimensionamento crítico na primeira renderização da tela
    private int obterLarguraDisponivel(Container target) {
        int largura = target.getSize().width;
        Container container = target;

        while (container.getSize().width == 0 && container.getParent() != null) {
            container = container.getParent();
        }

        largura = container.getSize().width;

        // Fallback de segurança: evita divisão por zero aplicando o valor limite inteiro
        if (largura == 0) {
            largura = Integer.MAX_VALUE;
        }

        return largura;
    }

    // Consolida e empilha a altura da linha finalizada no registro geral do Layout
    private void adicionarLinha(Dimension dimensaoFinal, int larguraLinha, int alturaLinha) {
        // A largura final do layout será correspondente à linha mais larga encontrada no processo
        dimensaoFinal.width = Math.max(dimensaoFinal.width, larguraLinha);

        // Se já existirem linhas empilhadas abaixo, injeta o espaçamento vertical regulamentado
        if (dimensaoFinal.height > 0) {
            dimensaoFinal.height += getVgap();
        }

        dimensaoFinal.height += alturaLinha;
    }
}