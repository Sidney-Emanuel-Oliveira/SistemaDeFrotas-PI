package br.com.ui;

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;


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

    

    


    @Override
    public Dimension preferredLayoutSize(Container target) {
        return calcularTamanhoLayout(target, true);
    }

    


    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension tamanhoMinimo = calcularTamanhoLayout(target, false);
        tamanhoMinimo.width -= (getHgap() + 1);
        return tamanhoMinimo;
    }

    

    


    private Dimension calcularTamanhoLayout(Container target, boolean usarTamanhoPreferido) {
        synchronized (target.getTreeLock()) {
            
            
            int larguraDisponivel = obterLarguraDisponivel(target);

            
            int espacoHorizontal = getHgap();
            int espacoVertical = getVgap();
            Insets margens = target.getInsets();
            int margensHorizontais = margens.left + margens.right + (espacoHorizontal * 2);
            int larguraMaximaLinha = larguraDisponivel - margensHorizontais;

            
            Dimension dimensaoFinal = new Dimension(0, 0);

            
            int larguraLinhaAtual = 0;
            int alturaLinhaAtual = 0;

            
            int totalComponentes = target.getComponentCount();
            for (int i = 0; i < totalComponentes; i++) {
                Component componente = target.getComponent(i);

                if (componente.isVisible()) {
                    
                    Dimension tamanhoComponente = usarTamanhoPreferido
                        ? componente.getPreferredSize()
                        : componente.getMinimumSize();

                    
                    if (larguraLinhaAtual + tamanhoComponente.width > larguraMaximaLinha) {
                        
                        adicionarLinha(dimensaoFinal, larguraLinhaAtual, alturaLinhaAtual);
                        larguraLinhaAtual = 0;
                        alturaLinhaAtual = 0;
                    }

                    
                    if (larguraLinhaAtual != 0) {
                        larguraLinhaAtual += espacoHorizontal;
                    }

                    
                    larguraLinhaAtual += tamanhoComponente.width;
                    alturaLinhaAtual = Math.max(alturaLinhaAtual, tamanhoComponente.height);
                }
            }

            
            adicionarLinha(dimensaoFinal, larguraLinhaAtual, alturaLinhaAtual);

            
            dimensaoFinal.width += margensHorizontais;
            dimensaoFinal.height += margens.top + margens.bottom + espacoVertical * 2;

            
            Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
            if (scrollPane != null && target.isValid()) {
                dimensaoFinal.width -= (espacoHorizontal + 1);
            }

            return dimensaoFinal;
        }
    }

    


    private int obterLarguraDisponivel(Container target) {
        int largura = target.getSize().width;
        Container container = target;

        
        while (container.getSize().width == 0 && container.getParent() != null) {
            container = container.getParent();
        }

        largura = container.getSize().width;

        
        if (largura == 0) {
            largura = Integer.MAX_VALUE;
        }

        return largura;
    }

    


    private void adicionarLinha(Dimension dimensaoFinal, int larguraLinha, int alturaLinha) {
        
        dimensaoFinal.width = Math.max(dimensaoFinal.width, larguraLinha);

        
        if (dimensaoFinal.height > 0) {
            dimensaoFinal.height += getVgap();
        }

        
        dimensaoFinal.height += alturaLinha;
    }
}

