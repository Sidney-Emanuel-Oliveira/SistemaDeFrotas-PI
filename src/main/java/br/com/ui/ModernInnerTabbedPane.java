package br.com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

// Painel de abas customizado com estilo plano, arredondado e feedbacks visuais modernos
public class ModernInnerTabbedPane extends JTabbedPane {

    public ModernInnerTabbedPane() {
        super();
        setUI(new ModernInnerTabbedPaneUI());
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setBackground(ModernColors.WHITE);
        setForeground(ModernColors.DARK_GRAY);
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(false); // Remove as linhas tracejadas nativas de foco nas abas
        setOpaque(true);
    }

    // Classe interna especializada que redefine os métodos de renderização do componente
    private static class ModernInnerTabbedPaneUI extends BasicTabbedPaneUI {

        // Zera os espaçamentos e bordas padrão para permitir que as abas encostem perfeitamente no container
        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(9, 20, 9, 20); // Respiro interno de cada aba
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(0, 0, 0, 0);
        }

        // Intercepta e desenha o plano de fundo em formato "pílula" arredondada, variando por estado
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean isHover = tabIndex == getRolloverTab(); // Verifica se o mouse está em cima da aba

            if (isSelected) {
                // Aba selecionada: Fundo azul escuro no hover ou azul primário padrão
                g2d.setColor(isHover ? ModernColors.DEEP_BLUE : ModernColors.PRIMARY_BLUE);
                g2d.fillRoundRect(x + 3, y + 3, w - 6, h - 5, 10, 10);
            } else {
                // Aba inativa: Fundo azul claro no hover ou cinza claro padrão
                g2d.setColor(isHover ? ModernColors.LIGHT_BLUE : ModernColors.BG_SECONDARY);
                g2d.fillRoundRect(x + 3, y + 5, w - 6, h - 7, 10, 10);
            }
            g2d.dispose();
        }

        // Neutraliza a pintura da borda nativa das abas (Deixa o design flat)
        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        }

        // Desenha uma linha fina horizontal cinza para separar elegantemente o menu de abas do conteúdo interno
        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            g.setColor(ModernColors.BORDER_GRAY);
            g.drawLine(0, 0, tabPane.getWidth(), 0);
        }

        // Renderiza o rótulo/texto da aba aplicando antialiasing de texto e modificando a cor pela seleção
        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                 int tabIndex, String title, Rectangle textRect,
                                 boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setFont(font);

            boolean isHover = tabIndex == getRolloverTab();

            if (isSelected) {
                g2d.setColor(Color.WHITE);
            } else if (isHover) {
                g2d.setColor(ModernColors.DEEP_BLUE);
            } else {
                g2d.setColor(ModernColors.DARK_GRAY);
            }

            // Ajusta o posicionamento do texto para compensar a margem e o recuo do desenho arredondado
            int x = textRect.x + 6;
            int y = textRect.y + metrics.getAscent();
            g2d.drawString(title, x, y);
            g2d.dispose();
        }

        // Aumenta a altura vertical padrão das abas para um visual mais confortável e moderno
        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return fontHeight + 20;
        }

        // Expande a largura horizontal para garantir o espaçamento do texto com os cantos arredondados
        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 24;
        }
    }
}