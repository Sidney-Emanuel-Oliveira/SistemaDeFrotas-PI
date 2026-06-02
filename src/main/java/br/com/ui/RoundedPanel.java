package br.com.ui;

import javax.swing.*;
import java.awt.*;

// Painel customizado com cantos arredondados, bordas suavizadas e efeito de sombra projetada
public class RoundedPanel extends JPanel {
    private int arcRadius = 14;
    private Color shadowColor = ModernColors.SHADOW_LIGHT;
    private int shadowSize = 5;
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = ModernColors.BORDER_GRAY;

    public RoundedPanel() {
        this.setOpaque(false); // Torna transparente o fundo quadrado padrão do JPanel nativo
        this.setLayout(null);
    }

    public RoundedPanel(LayoutManager layout) {
        this.setOpaque(false);
        this.setLayout(layout);
    }

    public RoundedPanel(int arcRadius) {
        this();
        this.arcRadius = arcRadius;
    }

    public RoundedPanel(int arcRadius, Color backgroundColor) {
        this();
        this.arcRadius = arcRadius;
        this.backgroundColor = backgroundColor;
    }

    // Intercepta o ciclo de renderização para desenhar as camadas do card em vetor 2D
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Ativa o Antialiasing para evitar bordas serrilhadas nas curvas
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Subtrai o tamanho da sombra para garantir que o painel interno não estoure os limites do componente
        int contentWidth = width - shadowSize - 1;
        int contentHeight = height - shadowSize - 1;

        // Camada 1: Desenha a sombra projetada (deslocada em diagonal para a direita e para baixo)
        if (shadowSize > 0) {
            g2d.setColor(shadowColor);
            g2d.fillRoundRect(shadowSize, shadowSize, contentWidth - 1, contentHeight - 1, arcRadius, arcRadius);
        }

        // Camada 2: Desenha o plano de fundo (Background) do painel na posição original (0,0)
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, contentWidth, contentHeight, arcRadius, arcRadius);

        // Camada 3: Desenha a linha de contorno (Borda) contornando exatamente a forma do fundo
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(0, 0, contentWidth, contentHeight, arcRadius, arcRadius);

        g2d.dispose(); // Libera os recursos gráficos do pipeline 2D
        super.paintComponent(g);
    }

    // Mutadores configurados para disparar o repaint() e atualizar a interface em tempo de execução
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setArcRadius(int arcRadius) {
        this.arcRadius = arcRadius;
        repaint();
    }

    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
        repaint();
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }
}